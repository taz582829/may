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

    public static void subscribe(final Object obj) {
        try {
            for (final Method method : obj.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(SubscribeEvent.class) || 1 != method.getParameterCount()) continue;
                final SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                final Class<?> eventType = method.getParameterTypes()[0];
                final List<EventListener> list = listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());
                list.add(new EventListener(method, obj, annotation.priority()));
                list.sort(Comparator.comparingInt(l -> l.priority.ordinal()));
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void unsubscribe(final Object obj) {
        for (final List<EventListener> list : listeners.values()) {
            list.removeIf(l -> l.owner == obj);
        }
    }

    public static <T extends Event> T postEvent(final T event) {
        for (Class<?> clazz = event.getClass(); Object.class != clazz; clazz = clazz.getSuperclass()) {
            if (null == listeners.get(clazz)) continue;
            for (final EventListener listener : listeners.get(clazz)) {
                listener.accept(event);
            }
        }
        return event;
    }

    static class EventListener implements Consumer<Event> {
        public final Method method;
        public final Object owner;
        public final Priority priority;

        public EventListener(final Method method, final Object owner, final Priority priority) {
            this.method = method;
            this.owner = owner;
            this.priority = priority;
            method.setAccessible(true);
        }

        @Override
        public void accept(final Event event) {
            try {
                method.invoke(owner, event);
            } catch (final IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}

