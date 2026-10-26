package lol.ethan.may.utils;

import lol.ethan.may.events.EventBus;
import lol.ethan.may.events.SubscribeEvent;
import lol.ethan.may.events.TickEvent;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {

    public static Timer setTimeout(final Runnable runnable, int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        }, delay);
        return timer;
    }

    public static Timer setInterval(final Runnable runnable, int delay, int interval) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runnable.run();
            }
        }, delay, interval);
        return timer;
    }

    public static void setTickDelay(Runnable runnable) {
        TimeUtils.setTickDelay(runnable, 1);
    }

    public static void setTickDelay(final Runnable runnable, final int delay) {
        EventBus.subscribe(new Object() {
            int ticks = 0;

            @SubscribeEvent
            public void onTick(TickEvent.Post event) {
                ++this.ticks;
                if (this.ticks >= delay) {
                    runnable.run();
                    EventBus.unsubscribe(this);
                }
            }
        });
    }

    public static String formatTime(LocalDateTime time) {
        boolean addZeroToMonths = time.getMonthValue() < 10;
        boolean addZeroToDay = time.getDayOfMonth() < 10;
        return (addZeroToMonths ? "0" : "") + time.getMonthValue() + "/" + (addZeroToDay ? "0" : "") + time.getDayOfMonth() + "/" + time.getYear();
    }
}

