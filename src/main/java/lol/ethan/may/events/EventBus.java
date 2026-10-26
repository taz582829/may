package lol.ethan.may.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<EventListener>> listeners = new ConcurrentHashMap<>();

    public static void subscribe(Object obj) {
        try {
            for (Method method : obj.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(SubscribeEvent.class) || method.getParameterCount() != 1) continue;
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                Class<?> eventType = method.getParameterTypes()[0];
                List<EventListener> list = listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());
                list.add(new EventListener(method, obj, annotation.priority()));
                list.sort(Comparator.comparingInt(l -> l.priority.ordinal()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void unsubscribe(Object obj) {
        for (List<EventListener> list : listeners.values()) {
            list.removeIf(l -> l.owner == obj);
        }
    }

    public static <T extends Event> T postEvent(T event) {
        for (Class<?> clazz = event.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (listeners.get(clazz) == null) continue;
            for (EventListener listener : listeners.get(clazz)) {
                listener.accept(event);
            }
        }
        return event;
    }

    static class EventListener
            implements Consumer<Event> {
        public final Method method;
        public final Object owner;
        public final Priority priority;

        public EventListener(Method method, Object owner, Priority priority) {
            this.method = method;
            this.owner = owner;
            this.priority = priority;
            method.setAccessible(true);
        }

        @Override
        public void accept(Event event) {
            try {
                this.method.invoke(this.owner, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}

