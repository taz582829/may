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

    default boolean shouldTransform(String className) {
        return this.getTargets().contains(className);
    }

    @Override
    default byte[] transform(final ClassLoader loader, String className, Class<?> target, ProtectionDomain prot, byte[] bytes) throws IllegalClassFormatException {
        if (!this.shouldTransform(className.replace("/", "."))) {
            return null;
        }
        try {
            ClassNode node = new ClassNode();
            ClassReader reader = new ClassReader(bytes);
            int flags = ClassWriter.COMPUTE_MAXS;
            if (shouldComputeFrames()) flags |= ClassWriter.COMPUTE_FRAMES;
            ClassWriter writer = new ClassWriter(reader, flags) {
                @Override
                protected String getCommonSuperClass(String type1, String type2) {
                    Class<?> class1;
                    try {
                        class1 = Class.forName(type1.replace('/', '.'), false, loader);
                    } catch (ClassNotFoundException e) {
                        throw new TypeNotPresentException(type1, e);
                    }
                    Class<?> class2;
                    try {
                        class2 = Class.forName(type2.replace('/', '.'), false, loader);
                    } catch (ClassNotFoundException e) {
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
            byte[] transformed = writer.toByteArray();
            File file = new File(new File(Entry.FILE_PATH, "debug"), target.getSimpleName() + "_transformed.class");
            if (!file.exists()) {
                file.mkdirs();
            }
            Files.write(file.toPath(), transformed);
            return transformed;
        } catch (Exception e) {
            System.out.println(target.getName() + " failed to transform! (" + this.getClass().getName() + ")");
            throw new RuntimeException(e);
        }
    }

    default boolean shouldComputeFrames() {
        return false;
    }
}

