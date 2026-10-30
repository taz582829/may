package lol.ethan.may.asm;

import lol.ethan.may.Entry;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.security.ProtectionDomain;
import java.util.List;

public interface ITransformer extends ClassFileTransformer {
    void transform(ClassNode node);

    List<String> getTargets();

    default boolean shouldTransform(final String className) {
        return getTargets().contains(className);
    }

    @Override
    default byte[] transform(final ClassLoader loader, final String className, final Class<?> target, final ProtectionDomain prot, final byte[] bytes) throws IllegalClassFormatException {
        if (!shouldTransform(className.replace("/", "."))) {
            return null;
        }
        try {
            final ClassNode node = new ClassNode();
            final ClassReader reader = new ClassReader(bytes);
            reader.accept(node, 0);
            try {
                transform(node);
            } catch (Throwable t) {
                t.printStackTrace();
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
            int lastSlash = className.lastIndexOf('/');
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
    default boolean shouldSave(String className) {
        return true;
    }

    default boolean shouldComputeFrames() {
        return false;
    }
}

