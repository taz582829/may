package lol.ethan.may.asm;

import lol.ethan.may.events.Event;
import lol.ethan.may.events.EventBus;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

public class MayInstructionAdapter extends InstructionAdapter {
    public MayInstructionAdapter(final MethodVisitor methodVisitor) {
        super(Opcodes.ASM9, methodVisitor);
    }

    public void postEvent() {
        invokestatic(Type.getInternalName(EventBus.class), "postEvent", "(L" + Type.getInternalName(Event.class) + ";)L" + Type.getInternalName(Event.class) + ";", false);
    }

    /**
     * ifeq = if false
     */
    @Override
    public void ifeq(final Label label) {
        super.ifeq(label);
    }

    /**
     * ifne = if true
     */
    @Override
    public void ifne(final Label label) {
        super.ifne(label);
    }

    public void invokestatic(final Class<?> owner, final String name, final String descriptor) {
        this.invokestatic(owner, name, descriptor, false);
    }

    public void invokestatic(final Class<?> owner, final String name, final String descriptor, final boolean isInterface) {
        super.invokestatic(Type.getInternalName(owner), name, descriptor, isInterface);
    }

    public void invokespecial(final Class<?> owner, final String name, final String descriptor) {
        super.invokespecial(Type.getInternalName(owner), name, descriptor, false);
    }

    public void invokevirtual(final Class<?> owner, final String name, final String descriptor, final boolean isInterface) {
        super.invokevirtual(Type.getInternalName(owner), name, descriptor, isInterface);
    }
    public void invokevirtual(final Class<?> owner, final String name, final String descriptor) {
        super.invokevirtual(Type.getInternalName(owner), name, descriptor, false);
    }

    public void anew(final Class<?> clazz) {
        super.anew(Type.getType(clazz));
    }
}
