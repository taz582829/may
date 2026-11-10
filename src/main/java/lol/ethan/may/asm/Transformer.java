package lol.ethan.may.asm;

import lol.ethan.may.Entry;
import lol.ethan.may.config.Config;
import lol.ethan.may.config.component.Button;
import lol.ethan.may.events.Event;
import lol.ethan.may.events.EventBus;
import lol.ethan.may.mappings.Mappings;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Transformer implements ClassFileTransformer {
    public String[] targets;

    protected Transformer(final String... targets) {
        if (targets == null) return;
        this.targets = Arrays.stream(targets).map(t -> t.replace("/", ".")).toArray(String[]::new);
    }
    protected Transformer(final Class<?>... targets) {
        this.targets = Arrays.stream(targets).map(Class::getName).toArray(String[]::new);
    }

    public abstract void transform(final ClassNode node);
    @Override
    public byte[] transform(
            final ClassLoader loader,
            final String className,
            final Class<?> target,
            final ProtectionDomain prot,
            final byte[] bytes
    ) throws IllegalClassFormatException {
        if (!shouldTransform(className.replace("/", "."))) {
            return null;
        }
        try {
            final ClassNode node = new ClassNode();
            final ClassReader reader = new ClassReader(bytes);
            reader.accept(node, 0);
            try {
                transform(node);
            } catch (final Throwable t) {
                t.printStackTrace();
                return null;
            }
            int flags = ClassWriter.COMPUTE_MAXS;
            if (this.shouldComputeFrames()) flags |= ClassWriter.COMPUTE_FRAMES;
            final ClassWriter writer = new ClassWriter(reader, flags) {
                @Override
                protected String getCommonSuperClass(final String type1, final String type2) {
                    Class<?> class1;
                    try {
                        class1 = Class.forName(type1.replace('/', '.'), false, loader);
                    } catch (final ClassNotFoundException e) {
                        throw new TypeNotPresentException(type1, e);
                    }
                    final Class<?> class2;
                    try {
                        class2 = Class.forName(type2.replace('/', '.'), false, loader);
                    } catch (final ClassNotFoundException e) {
                        throw new TypeNotPresentException(type2, e);
                    }
                    if (class1.isAssignableFrom(class2)) {
                        return type1;
                    }
                    if (class2.isAssignableFrom(class1)) {
                        return type2;
                    }
                    if (class1.isInterface() || class2.isInterface()) {
                        return "java/lang/Object";
                    } else {
                        do {
                            class1 = class1.getSuperclass();
                        } while (!class1.isAssignableFrom(class2));
                        return class1.getName().replace('.', '/');
                    }
                }
            };
            node.accept(writer);
            final byte[] transformed = writer.toByteArray();

            String simpleName = className;
            final int lastSlash = className.lastIndexOf('/');
            if (lastSlash != -1) {
                simpleName = className.substring(lastSlash + 1);
            }

            if (shouldSave(className.replace("/", "."))) {
                final File file = new File(Entry.DEBUG_DIR, simpleName + "_transformed.class");
                final File preFile = new File(Entry.DEBUG_DIR, simpleName + "_pretransform.class");

                Files.write(preFile.toPath(), bytes);
                Files.write(file.toPath(), transformed);
            }
            return transformed;
        } catch (final Throwable t) {
            System.out.println(className + " failed to transform! (" + getClass().getName() + ")");
            t.printStackTrace();
            return null;
        }
    }

    /**
     * set to true if using a {@code Label}
     */
    public boolean shouldComputeFrames() {
        return false;
    }
    public boolean shouldTransform(final String className) {
        for (final String target : targets) {
            if (target.equals(className)) return true;
            if (target.endsWith("*")) {
                final String start = target.substring(0, target.length() - 1);
                if (className.startsWith(start)) return true;
            }
        }
        return false;
    }
    public boolean shouldSave(final String className) {
        return true;
    }

    public MethodInsnNode postEvent() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EventBus.class), "postEvent", "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";");
    }
    public String mapField(final Class<?> owner, final String name) {
        if (Mappings.getMappingType() != Mappings.MappingType.MCP) return name;
        return Mappings.mapFieldInHierarchy(Type.getInternalName(owner), name, name);
    }
    public String mapMethod(final Class<?> owner, final String name, final String desc) {
        if (Mappings.getMappingType() != Mappings.MappingType.MCP) return name;
        return Mappings.mapMethodInHierarchy(Type.getInternalName(owner), name, desc, name);
    }

    public void postEvent(final InstructionAdapter a) {
        a.invokestatic(Type.getInternalName(EventBus.class), "postEvent", "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";", false);
    }

    public void ifEventCancel(final InstructionAdapter a, final Consumer<InstructionAdapter> onCancel) {
        final Label end = new Label();
        a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
        a.ifeq(end);
        onCancel.accept(a);
        a.mark(end);
    }

    public Type getType(final Class<?> clazz) {
        return Type.getObjectType(Type.getInternalName(clazz));
    }

    public InsnList asm(final Consumer<MayInstructionAdapter> body) {
        final MethodNode temp = new MethodNode();
        final MayInstructionAdapter a = new MayInstructionAdapter(temp);
        body.accept(a);
        return temp.instructions;
    }
    public InsnList getConfigFieldValue(final String cfgName) {
        return asm(a -> {
            a.getstatic(Config.class, cfgName, "L" + Type.getInternalName(Button.class) + ";");
            a.getfield(Button.class, "enabled", "Z");
        });
    }

    public void logCalls(final MethodNode node) {
        System.out.println("___________________________________________________________________");
        System.out.println("Logging calls for " + node.name + " " + node.desc);
        System.out.println("\n");
        final Textifier printer = new Textifier();
        node.accept(new TraceMethodVisitor(printer));
        final StringWriter writer = new StringWriter();
        printer.print(new PrintWriter(writer));
        System.out.println(writer);
        System.out.println("___________________________________________________________________");
    }

    public void insertBeforeReturn(final MethodNode method, final InsnList list) {
        for (final AbstractInsnNode insnNode : method.instructions.toArray()) {
            final int opcode = insnNode.getOpcode();
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                method.instructions.insertBefore(insnNode, cloneList(list));
            }
        }
    }

    protected static InsnList cloneList(final InsnList list) {
        final Map<LabelNode, LabelNode> labels = new HashMap<>();
        for (final AbstractInsnNode insn : list.toArray()) {
            if (insn instanceof LabelNode) labels.put((LabelNode) insn, new LabelNode());
        }
        final InsnList copy = new InsnList();
        for (final AbstractInsnNode insn : list.toArray()) {
            copy.add(insn.clone(labels));
        }
        return copy;
    }
    protected boolean isPublic(final int modifiers) {
        return (modifiers & Opcodes.ACC_PUBLIC) != 0;
    }
    protected boolean isPrivate(final int modifiers) {
        return (modifiers & Opcodes.ACC_PRIVATE) != 0;
    }
    protected boolean isProtected(final int modifiers) {
        return (modifiers & Opcodes.ACC_PROTECTED) != 0;
    }
    protected boolean isStatic(final int modifiers) {
        return (modifiers & Opcodes.ACC_STATIC) != 0;
    }
    protected boolean isFinal(final int modifiers) {
        return (modifiers & Opcodes.ACC_FINAL) != 0;
    }
}
