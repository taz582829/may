package lol.ethan.may;

import lol.ethan.may.utils.HttpUtils;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.CompletableFuture;

public class Installer {
    private static final File AGENT_FOLDER = new File("C:\\agent");

    public static void main(String[] args) {
        final String home = System.getProperty("user.home");
        File downloadsFolder = new File(home + "/Downloads");
        JOptionPane.showMessageDialog(null, "installing may...");
        HttpUtils.download("https://github.com/taz582829/may/releases/latest/download/agent.jar", new File(AGENT_FOLDER, "agent.jar").toPath())
                .thenAccept(r ->
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null, "downloaded jar to " + r.toAbsolutePath())))
                .exceptionally(e -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }).join();
        HttpUtils.download("https://github.com/taz582829/may/releases/latest/download/may.dll", new File(AGENT_FOLDER, "may.dll").toPath())
                .thenAccept(r ->
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null, "downloaded attacher to " + r.toAbsolutePath())))
                .exceptionally(e -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }).join();
        HttpUtils.download("https://github.com/taz582829/may/releases/latest/download/may.exe", new File(downloadsFolder, "may.exe").toPath())
                .thenAccept(r ->
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(null, "downloaded injector to " + r.toAbsolutePath())))
                .exceptionally(e -> {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }).join();
    }
    public static CompletableFuture<String> getLatestReleaseTag() {
        return HttpUtils.get("https://api.github.com/repos/taz582829/may/releases/latest").thenApply(r ->
             r.getAsJsonObject().get("tag_name").getAsString()
        ).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
    public static void notifyOnUpdate(String nv) {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                int choice = JOptionPane.showConfirmDialog(
//                        null,
//                        "A new version of May (" + nv + ") is available!\nUpdate now?",
//                        "Update Available",
//                        JOptionPane.OK_CANCEL_OPTION,
//                        JOptionPane.INFORMATION_MESSAGE
//                );
//                if (choice == JOptionPane.OK_OPTION) {
//                    new ProcessBuilder(
//                            "java",
//                            "-jar",
//                            "agent.jar"
//                    ).start();
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }));
    }
}
