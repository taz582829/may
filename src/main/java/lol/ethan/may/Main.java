package lol.ethan.may;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Main {
    public static Instrumentation inst;
    public static ClassLoader targetLoader;

    public static void agentmain(final String args, final Instrumentation inst) {
        Main.inst = inst;
        try {
            final ClassLoader mc = Main.findMinecraftLoader(inst);
            Thread.currentThread().setContextClassLoader(mc);
            Main.openJavaLang();
            Main.defineAll(mc);
            targetLoader = mc;
            Class.forName("lol.ethan.may.Entry", true, mc).getMethod("load", Instrumentation.class, ClassLoader.class).invoke(null, inst, mc);
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    public static ClassLoader findMinecraftLoader(final Instrumentation inst) {
        try {
            for (final Class clazz : inst.getAllLoadedClasses()) {
                if (!clazz.getName().replace("/", ".").equals("net.minecraft.client.Minecraft")) continue;
                return clazz.getClassLoader();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    static void openJavaLang() {
        final Method getModule;
        try {
            getModule = Class.class.getMethod("getModule");
        } catch (final NoSuchMethodException e) {
            return;
        }
        try {
            final Class<?> moduleCls = Class.forName("java.lang.Module");
            final Object base = getModule.invoke(Object.class);
            final Object me = getModule.invoke(Main.class);
            final HashMap<String, Set<Object>> opens = new HashMap<>();
            opens.put("java.lang", Collections.singleton(me));
            final Method redefine = Instrumentation.class.getMethod("redefineModule", moduleCls, Set.class, Map.class, Map.class, Set.class, Map.class);
            redefine.invoke(inst, base, Collections.emptySet(), Collections.emptyMap(), opens, Collections.emptySet(), Collections.emptyMap());
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    static byte[] readAll(final InputStream in) throws IOException {
        int n;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buf = new byte[8192];
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
        return out.toByteArray();
    }

    static void defineAll(final ClassLoader target) throws Exception {
        Main.openJavaLang();
        final Method define = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE, ProtectionDomain.class);
        define.setAccessible(true);
        final File agent = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        final LinkedHashMap<String, byte[]> classBytes = new LinkedHashMap<>();

        try (final JarFile jar = new JarFile(agent)) {
            final Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                final JarEntry entry = e.nextElement();
                final String name = entry.getName();
                if (name.endsWith(".class") && name.startsWith("lol/ethan/may/")) {
                    try (InputStream in = jar.getInputStream(entry)) {
                        classBytes.put(name.substring(0, name.length() - 6).replace('/', '.'), readAll(in));
                    }
                }
            }
        }
        boolean progress = true;
        while (!classBytes.isEmpty() && progress) {
            progress = false;
            Iterator<Map.Entry<String, byte[]>> it = classBytes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, byte[]> entry = it.next();
                try {
                    define.invoke(target, entry.getKey(), entry.getValue(), 0, entry.getValue().length, null);
                    it.remove();
                    progress = true;
                } catch (Throwable ignored) {
                }
            }
        }
        if (!classBytes.isEmpty()) {
            throw new IllegalStateException("could not define: " + classBytes.keySet());
        }
    }
}

