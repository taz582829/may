package lol.ethan.may.mappings;

import lol.ethan.may.Entry;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Type;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Mappings {
    private static final Map<String, String> fieldMappings;
    private static final Map<String, String> methodMappings;
    private static final Executor executor;
    private static final Map<String, String> superCache;
    private static Minecraft mc;

    static {
        fieldMappings = new HashMap<>();
        methodMappings = new HashMap<>();
        executor = Executors.newSingleThreadExecutor();
        superCache = new HashMap<>();
    }

    public static MappingType getMappingType() {
        try {
            mc.getClass().getMethod("func_71410_x");
            return MappingType.MCP;
        } catch (final Exception e) {
            try {
                mc.getClass().getMethod("getMinecraft");
                return MappingType.NONE;
            } catch (final Exception ex) {
                new JTextField("couldnt find minecraft class; exiting program").setVisible(true);
                System.exit(-1);
                return null;
            }
        }
    }

    private static InputStream open(final String path) {
        final ClassLoader[] loaders;
        final String rel = path.startsWith("/") ? path.substring(1) : path;
        for (final ClassLoader cl : loaders = new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader(), Mappings.class.getClassLoader()}) {
            final InputStream in;
            if (cl == null || (in = cl.getResourceAsStream(rel)) == null) continue;
            return in;
        }
        return null;
    }

    private static List<String> readLines(final String path) throws IOException {
        try (final InputStream in = Mappings.open(path)) {
            String l;
            if (in == null) {
                throw new FileNotFoundException(path);
            }
            final BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            final ArrayList<String> lines = new ArrayList<>();
            while ((l = r.readLine()) != null) {
                lines.add(l);
            }
            final ArrayList<String> arrayList = lines;
            return arrayList;
        }
    }

    public static void load(final Minecraft mc) throws IOException {
        Mappings.mc = mc;
        try {
            String[] p;
            final HashMap dataMap = new HashMap();
            final HashMap<String, String> fieldNames = new HashMap<>();
            for (final String line : Mappings.readLines("/mappings/fields.csv")) {
                final String[] p2;
                if (line.startsWith("searge,") || (p2 = line.split(",", 3)).length < 2) continue;
                fieldNames.put(p2[0], p2[1]);
            }
            final HashMap<String, String> methodNames = new HashMap<>();
            for (final String line : Mappings.readLines("/mappings/methods.csv")) {
                if (line.startsWith("searge,") || (p = line.split(",", 3)).length < 2) continue;
                methodNames.put(p[0], p[1]);
            }
            for (final String line : Mappings.readLines("/mappings/joined.srg")) {
                final String full;
                if (line.equals("searge,name,side,desc")) continue;
                if (line.startsWith("FD: ")) {
                    p = line.substring(4).trim().split("\\s+");
                    full = p[1];
                    final int slash = full.lastIndexOf(47);
                    final String owner = full.substring(0, slash);
                    final String srg = full.substring(slash + 1);
                    final String mcp = fieldNames.get(srg);
                    if (mcp == null) continue;
                    fieldMappings.put(owner + "." + mcp, srg);
                    continue;
                }
                if (!line.startsWith("MD: ")) continue;
                p = line.substring(4).trim().split("\\s+");
                full = p[2];
                final String desc = p[3];
                final int slash = full.lastIndexOf(47);
                final String owner = full.substring(0, slash);
                final String srg = full.substring(slash + 1);
                final String mcp = methodNames.get(srg);
                if (mcp == null) continue;
                methodMappings.put(owner + "." + mcp + desc, srg);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getField(final String owner, final String name) {
        return fieldMappings.getOrDefault(owner + "." + name, name);
    }

    public static String getField(final Class<?> owner, final String name) {
        return fieldMappings.getOrDefault(Type.getInternalName(owner) + "." + name, name);
    }

    public static String getMethod(final String owner, final String name, final String desc) {
        return methodMappings.getOrDefault(owner + "." + name + desc, name);
    }

    public static String mapMethodInHierarchy(final String owner, final String name, final String desc) {
        return Mappings.mapMethodInHierarchy(owner, name, desc, name);
    }

    public static String mapMethodInHierarchy(final String owner, final String name, final String desc, final String fallback) {
        String cur = owner;
        while (cur != null) {
            final String srg = methodMappings.get(cur + "." + name + desc);
            if (srg != null) {
                return srg;
            }
            cur = Mappings.superOf(cur);
        }
        return fallback;
    }

    public static String mapFieldInHierarchy(final String owner, final String name, final String fallback) {
        String cur = owner;
        while (cur != null) {
            final String srg = fieldMappings.get(cur + "." + name);
            if (srg != null) {
                return srg;
            }
            cur = Mappings.superOf(cur);
        }
        return fallback;
    }

    private static String superOf(final String internalName) {
        if (internalName.equals("java/lang/Object")) return null;
        final String cached = superCache.get(internalName);
        if (cached != null) return cached;
        String result = null;
        try {
            final Class<?> c = Class.forName(internalName.replace('/', '.'), false, Entry.loader);
            final Class<?> s = c.getSuperclass();
            result = s == null ? "java/lang/Object" : s.getName().replace('.', '/');
        } catch (final Throwable ignored) {
        }
        if (result == null) return "java/lang/Object";
        superCache.put(internalName, result);
        return result;
    }

    public enum MappingType {
        MCP,
        NONE

    }
}

