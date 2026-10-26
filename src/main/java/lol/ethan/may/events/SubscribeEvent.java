package lol.ethan.may.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface SubscribeEvent {
    Priority priority() default Priority.NORMAL;
}

