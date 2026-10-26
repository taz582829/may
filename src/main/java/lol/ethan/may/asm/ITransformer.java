package lol.ethan.may.asm;

import lol.ethan.may.Entry;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.ProtectionDomain;
import java.util.List;

public interface ITransformer extends ClassFileTransformer {
    void transform(ClassNode var1);

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
            reader.accept(node, 8);
            this.transform(node);
            int flags = 1;
            if (this.shouldComputeFrames()) {
                flags |= 2;
            }
            ClassWriter writer = new ClassWriter(reader, flags) {
                @Override
                protected String getCommonSuperClass(String type1, String type2) {
                    Class<?> class2;
                    Class<?> class1;
                    if (!shouldComputeFrames()) {
                        return super.getCommonSuperClass(type1, type2);
                    }
                    try {
                        class1 = Class.forName(type1.replace('/', '.'), false, loader);
                    } catch (ClassNotFoundException e) {
                        throw new TypeNotPresentException(type1, e);
                    }
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
                    }
                    return class1.getName().replace('.', '/');
                }
            };
            node.accept(writer);
            byte[] transformedBytes = writer.toByteArray();
            if (transformedBytes != bytes) {
                File debugFolder = new File(Entry.FILE_PATH, "debug");
                if (!debugFolder.exists()) {
                    debugFolder.mkdirs();
                }
                Files.write(new File(debugFolder, target.getSimpleName() + "_transformed.class").toPath(), writer.toByteArray());
            }
            return transformedBytes;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    default boolean shouldComputeFrames() {
        return false;
    }
}

