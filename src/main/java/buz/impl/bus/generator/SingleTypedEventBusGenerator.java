package buz.impl.bus.generator;

import buz.api.EventBus;
import buz.api.event.Event;
import buz.api.event.ResultListener;
import lombok.SneakyThrows;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleTypedEventBusGenerator<P extends Event<?, ?>> implements Opcodes {
    private static final String FIELD_LISTENERS_ARRAY = "listeners";
    private final AtomicInteger generatedBusCounter = new AtomicInteger(0);
    private final Map<Class<? extends P>, EventBus<? extends P>> cachedEventBus = new ConcurrentHashMap<>();

    @SneakyThrows
    public <T extends P> void createFor(Class<T> typeOfEvent) {
        var classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        var name = "buz/generated/_" + generatedBusCounter.incrementAndGet() + "_" + typeOfEvent.getSimpleName();
        classWriter.visit(V11, ACC_PUBLIC + ACC_FINAL, name, null, Type.getInternalName(EventBus.class), null);
        classWriter.visitField(ACC_PRIVATE + ACC_FINAL, FIELD_LISTENERS_ARRAY,
                Type.getDescriptor(ArrayList.class), null, null);
        buildInitializer(classWriter, name);
        buildPostEventNode(classWriter, typeOfEvent,name);
        Files.write(Path.of("Test.class"), classWriter.toByteArray());
    }

    private void buildInitializer(ClassWriter classNode, String name) {
        var m = classNode.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        m.visitVarInsn(ALOAD, 0);
        m.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Stub.class), "newSortAtInsertList", "()Ljava/util/List;", false);
        m.visitFieldInsn(PUTFIELD, name, FIELD_LISTENERS_ARRAY, Type.getDescriptor(List.class));
        m.visitInsn(RETURN);
    }

    @SneakyThrows
    private <T extends P> void buildPostEventNode(ClassWriter classWriter, Class<T> typeOfEvent, String name) {
        var m = classWriter.visitMethod(ACC_PUBLIC + ACC_FINAL,
                "postEvent", Type.getMethodDescriptor(EventBus.class.getMethod("postEvent", Event.class, boolean.class, ResultListener.class)),
                null, null);
        // load the list to local
        m.visitVarInsn(ALOAD, 0);
        m.visitInsn(DUP);
        m.visitFieldInsn(GETFIELD,name,FIELD_LISTENERS_ARRAY,Type.getDescriptor(ArrayList.class));
        m.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(List.class), "size", "()I", true);
        // store the size then the list into local variable. 1 is the list, 2 is size of list and 3 is the counter.
        int INDEX_LIST = 1, INDEX_SIZE = 2, INDEX_COUNTER = 3;
        m.visitIntInsn(ISTORE, INDEX_SIZE);
        m.visitVarInsn(ASTORE, INDEX_LIST);
        // prepare for the counter
        m.visitInsn(ICONST_0);
        m.visitIntInsn(ISTORE, INDEX_COUNTER);
        // Our stack is empty now. Let's begin the loop.
        var loopBegin = new Label();
        var loopEnd = new Label();
        m.visitLabel(loopBegin);
        m.visitIntInsn(ILOAD, INDEX_COUNTER); // the counter
        m.visitIntInsn(ILOAD, INDEX_SIZE); // the size of list
        m.visitJumpInsn(IF_ICMPEQ, loopEnd);
        // loop body begin
        m.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out", Type.getDescriptor(PrintStream.class));
        m.visitIntInsn(ILOAD, 3);
        m.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "print", "(I)V", false);
        // loop body end
        m.visitIincInsn(INDEX_COUNTER, 1); // +1
        m.visitJumpInsn(GOTO, loopBegin);
        m.visitLabel(loopEnd);
        m.visitInsn(RETURN);
    }
}
