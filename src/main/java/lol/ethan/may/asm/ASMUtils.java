package lol.ethan.may.asm;

import lol.ethan.may.events.Event;
import lol.ethan.may.events.EventBus;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

public class ASMUtils {
    public static final String POST_EVENT_DESC = "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";";

    public static MethodInsnNode postEvent() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(EventBus.class), "postEvent", "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";");
    }

    public static void postEvent(InstructionAdapter a) {
        a.invokestatic(Type.getInternalName(EventBus.class), "postEvent", "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";", false);
    }

    public static void returnIfEventCancelled(InstructionAdapter a) {
        Label end = new Label();
        a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
        a.ifeq(end);
        a.areturn(Type.VOID_TYPE);
        a.mark(end);
    }

    public static void ifEventCancel(InstructionAdapter a, Consumer<InstructionAdapter> onCancel) {
        Label end = new Label();
        a.invokevirtual(Type.getInternalName(Event.class), "isCancelled", "()Z", false);
        a.ifeq(end);
        onCancel.accept(a);
        a.mark(end);
    }

    public static Type getType(Class<?> clazz) {
        return Type.getObjectType(Type.getInternalName(clazz));
    }

    public static InsnList asm(Consumer<InstructionAdapter> body) {
        MethodNode temp = new MethodNode();
        InstructionAdapter a = new InstructionAdapter(temp);
        body.accept(a);
        return temp.instructions;
    }

    public static void logCalls(MethodNode node) {
        System.out.println("___________________________________________________________________");
        Textifier printer = new Textifier();
        node.accept(new TraceMethodVisitor(printer));
        StringWriter writer = new StringWriter();
        printer.print(new PrintWriter(writer));
        System.out.println(writer);
        System.out.println("___________________________________________________________________");
    }
}

