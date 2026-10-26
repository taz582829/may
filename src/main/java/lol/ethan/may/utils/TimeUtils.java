package lol.ethan.may.utils;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {

    public static Timer setTimeout(final Runnable runnable, final int delay) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
        return timer;
    }

    public static Timer setInterval(final Runnable runnable, final int delay, final int interval) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        }, delay, interval);
        return timer;
    }

    public static void setTickDelay(final Runnable runnable) {
        TimeUtils.setTickDelay(runnable, 1);
    }

    public static void setTickDelay(final Runnable runnable, final int delay) {
        EventBus.subscribe(new Object() {
            int ticks;

            @SubscribeEvent
            public void onTick(final TickEvent.Post event) {
                ++ticks;
                if (ticks >= delay) {
                    runnable.run();
                    EventBus.unsubscribe(this);
                }
            }
        });
    }

    public static String formatTime(final LocalDateTime time) {
        final boolean addZeroToMonths = 10 > time.getMonthValue();
        final boolean addZeroToDay = 10 > time.getDayOfMonth();
        return (addZeroToMonths ? "0" : "") + time.getMonthValue() + "/" + (addZeroToDay ? "0" : "") + time.getDayOfMonth() + "/" + time.getYear();
    }
}

