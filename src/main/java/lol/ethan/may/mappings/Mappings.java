package lol.ethan.may.mappings;

import net.minecraft.client.Minecraft;
import org.objectweb.asm.ClassReader;
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
        } catch (Exception e) {
            try {
                mc.getClass().getMethod("getMinecraft");
                return MappingType.NONE;
            } catch (Exception ex) {
                new JTextField("couldnt find minecraft class; exiting program").setVisible(true);
                System.exit(-1);
                return null;
            }
        }
    }

    private static InputStream open(String path) {
        ClassLoader[] loaders;
        String rel = path.startsWith("/") ? path.substring(1) : path;
        for (ClassLoader cl : loaders = new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader(), Mappings.class.getClassLoader()}) {
            InputStream in;
            if (cl == null || (in = cl.getResourceAsStream(rel)) == null) continue;
            return in;
        }
        return null;
    }

    private static List<String> readLines(String path) throws IOException {
        try (InputStream in = Mappings.open(path)) {
            String l;
            if (in == null) {
                throw new FileNotFoundException(path);
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            ArrayList<String> lines = new ArrayList<>();
            while ((l = r.readLine()) != null) {
                lines.add(l);
            }
            ArrayList<String> arrayList = lines;
            return arrayList;
        }
    }

    public static void load(Minecraft mc) throws IOException {
        Mappings.mc = mc;
        try {
            String[] p;
            HashMap dataMap = new HashMap();
            HashMap<String, String> fieldNames = new HashMap<>();
            for (String line : Mappings.readLines("/mappings/fields.csv")) {
                String[] p2;
                if (line.startsWith("searge,") || (p2 = line.split(",", 3)).length < 2) continue;
                fieldNames.put(p2[0], p2[1]);
            }
            HashMap<String, String> methodNames = new HashMap<>();
            for (String line : Mappings.readLines("/mappings/methods.csv")) {
                if (line.startsWith("searge,") || (p = line.split(",", 3)).length < 2) continue;
                methodNames.put(p[0], p[1]);
            }
            for (String line : Mappings.readLines("/mappings/joined.srg")) {
                String full;
                if (line.equals("searge,name,side,desc")) continue;
                if (line.startsWith("FD: ")) {
                    p = line.substring(4).trim().split("\\s+");
                    full = p[1];
                    int slash = full.lastIndexOf(47);
                    String owner = full.substring(0, slash);
                    String srg = full.substring(slash + 1);
                    String mcp = fieldNames.get(srg);
                    if (mcp == null) continue;
                    fieldMappings.put(owner + "." + mcp, srg);
                    continue;
                }
                if (!line.startsWith("MD: ")) continue;
                p = line.substring(4).trim().split("\\s+");
                full = p[2];
                String desc = p[3];
                int slash = full.lastIndexOf(47);
                String owner = full.substring(0, slash);
                String srg = full.substring(slash + 1);
                String mcp = methodNames.get(srg);
                if (mcp == null) continue;
                methodMappings.put(owner + "." + mcp + desc, srg);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getField(String owner, String name) {
        return fieldMappings.getOrDefault(owner + "." + name, name);
    }

    public static String getField(Class<?> owner, String name) {
        return fieldMappings.getOrDefault(Type.getInternalName(owner) + "." + name, name);
    }

    public static String getMethod(String owner, String name, String desc) {
        return methodMappings.getOrDefault(owner + "." + name + desc, name);
    }

    public static String mapMethodInHierarchy(String owner, String name, String desc) {
        return Mappings.mapMethodInHierarchy(owner, name, desc, null);
    }

    public static String mapMethodInHierarchy(String owner, String name, String desc, String fallback) {
        String cur = owner;
        while (cur != null) {
            String srg = methodMappings.get(cur + "." + name + desc);
            if (srg != null) {
                return srg;
            }
            cur = Mappings.superOf(cur);
        }
        return fallback;
    }

    public static String mapFieldInHierarchy(String owner, String name, String fallback) {
        String cur = owner;
        while (cur != null) {
            String srg = fieldMappings.get(cur + "." + name);
            if (srg != null) {
                return srg;
            }
            cur = Mappings.superOf(cur);
        }
        return fallback;
    }

    private static String superOf(String internalName) {
        if (internalName.equals("java/lang/Object")) {
            return null;
        }
        return superCache.computeIfAbsent(internalName, n -> {
            try (InputStream in = Mappings.class.getClassLoader().getResourceAsStream(n + ".class")) {
                if (in == null) {
                    String string2 = "java/lang/Object";
                    return string2;
                }
                String string = new ClassReader(in).getSuperName();
                return string;
            } catch (IOException e) {
                return "java/lang/Object";
            }
        });
    }

    public enum MappingType {
        MCP,
        NONE

    }
}

