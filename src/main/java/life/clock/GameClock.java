package life.clock;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class GameClock {
    private final CopyOnWriteArrayList<TickListener> listeners = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executorService;
    private final AtomicLong tickNumber = new AtomicLong(0);
    private final Object lock = new Object();
    private final int minimumDelayMillis;
    private final int maximumDelayMillis;
    private final int delayStepMillis = 50;

    private ScheduledFuture<?> scheduledTask;
    private int delayMillis;
    private boolean running;

    public GameClock(int initialDelayMillis, int minimumDelayMillis, int maximumDelayMillis) {
        this.delayMillis = initialDelayMillis;
        this.minimumDelayMillis = minimumDelayMillis;
        this.maximumDelayMillis = maximumDelayMillis;
        this.executorService = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "game-clock");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void addListener(TickListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    public long getTickNumber() {
        return tickNumber.get();
    }

    public int getDelayMillis() {
        synchronized (lock) {
            return delayMillis;
        }
    }

    public boolean isRunning() {
        synchronized (lock) {
            return running;
        }
    }

    public void start() {
        synchronized (lock) {
            tickNumber.set(0);
            running = true;
            scheduleTicks();
        }
    }

    public void pause() {
        synchronized (lock) {
            running = false;
            cancelScheduledTask();
        }
    }

    public void resume() {
        synchronized (lock) {
            if (running) {
                return;
            }
            running = true;
            scheduleTicks();
        }
    }

    public void reset() {
        synchronized (lock) {
            running = false;
            cancelScheduledTask();
            tickNumber.set(0);
        }
    }

    public void faster() {
        synchronized (lock) {
            delayMillis = Math.max(minimumDelayMillis, delayMillis - delayStepMillis);
            if (running) {
                scheduleTicks();
            }
        }
    }

    public void slower() {
        synchronized (lock) {
            delayMillis = Math.min(maximumDelayMillis, delayMillis + delayStepMillis);
            if (running) {
                scheduleTicks();
            }
        }
    }

    public void tickNow() {
        long currentTick = tickNumber.incrementAndGet();
        for (TickListener listener : listeners) {
            listener.onTick(currentTick);
        }
    }

    public void shutdown() {
        synchronized (lock) {
            running = false;
            cancelScheduledTask();
        }
        executorService.shutdownNow();
    }

    private void scheduleTicks() {
        cancelScheduledTask();
        scheduledTask = executorService.scheduleAtFixedRate(
                this::tickNow,
                delayMillis,
                delayMillis,
                TimeUnit.MILLISECONDS
        );
    }

    private void cancelScheduledTask() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            scheduledTask = null;
        }
    }
}
