package lol.ethan.may.asm.transformers.chat;

import lol.ethan.may.asm.Transformer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class ChatStyleTransformer extends Transformer {
    public ChatStyleTransformer() {
        super(ChatStyle.class);
    }

    @Override
    public void transform(final ClassNode node) {
        for (final MethodNode method : node.methods) {
            if ("getChatHoverEvent".equals(method.name)) {
                insertBeforeReturn(method, asm(a -> {
                    a.load(0, Type.getType(ChatStyle.class));
                    a.invokestatic(ChatStyleTransformer.class, "may$getChatHoverEvent", "(Lnet/minecraft/event/HoverEvent;Lnet/minecraft/util/ChatStyle;)Lnet/minecraft/event/HoverEvent;");
                }));
            }
        }
    }

    public static HoverEvent may$getChatHoverEvent(final HoverEvent hoverEvent, final ChatStyle style) {
        final ClickEvent clickEvent = style.getChatClickEvent();
        if (clickEvent == null) return hoverEvent;

        final ClickEvent.Action action = clickEvent.getAction();
        if (!(action == ClickEvent.Action.OPEN_FILE || action == ClickEvent.Action.OPEN_URL || action == ClickEvent.Action.RUN_COMMAND)) {
            return hoverEvent;
        }
        final String actionMessage = "§e" + (action == ClickEvent.Action.RUN_COMMAND ? "Runs " : "Opens ") + clickEvent.getValue() + " on click.";
        if (hoverEvent == null) {
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(actionMessage));
        }

        if (hoverEvent.getValue().getUnformattedText().contains(actionMessage)) {
            return hoverEvent;
        }

        return new HoverEvent(hoverEvent.getAction(), hoverEvent.getValue().createCopy().appendText("\n" + actionMessage));
    }
}
