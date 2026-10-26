package lol.ethan.may.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorUtils {
    public static void begin(int x, int y, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = new ScaledResolution(mc).getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
                x * scale,
                mc.displayHeight - (y + height) * scale,
                Math.max(0, width * scale),
                Math.max(0, height * scale)
        );
    }
    public static void end() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
