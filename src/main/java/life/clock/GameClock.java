package life.clock;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public final class GameClock {
    private final List<TickListener> listeners = new ArrayList<>();
    private final int minimumDelayMillis;
    private final int maximumDelayMillis;
    private final int delayStepMillis = 50;

    private Timer timer;
    private long tickNumber;
    private int delayMillis;
    private boolean running;

    public GameClock(int initialDelayMillis, int minimumDelayMillis, int maximumDelayMillis) {
        this.delayMillis = initialDelayMillis;
        this.minimumDelayMillis = minimumDelayMillis;
        this.maximumDelayMillis = maximumDelayMillis;
    }

    public void addListener(TickListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TickListener listener) {
        listeners.remove(listener);
    }

    public long getTickNumber() {
        return tickNumber;
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        tickNumber = 0;
        running = true;
        scheduleTicks();
    }

    public void pause() {
        running = false;
        cancelScheduledTask();
    }

    public void resume() {
        if (running) {
            return;
        }
        running = true;
        scheduleTicks();
    }

    public void reset() {
        running = false;
        cancelScheduledTask();
        tickNumber = 0;
    }

    public void faster() {
        delayMillis = Math.max(minimumDelayMillis, delayMillis - delayStepMillis);
        if (running && timer != null) {
            timer.setDelay(delayMillis);
        }
    }

    public void slower() {
        delayMillis = Math.min(maximumDelayMillis, delayMillis + delayStepMillis);
        if (running && timer != null) {
            timer.setDelay(delayMillis);
        }
    }

    public void tickNow() {
        long currentTick = ++tickNumber;
        for (TickListener listener : new ArrayList<>(listeners)) {
            listener.onTick(currentTick);
        }
    }

    public void shutdown() {
        running = false;
        cancelScheduledTask();
    }

    private void scheduleTicks() {
        cancelScheduledTask();
        timer = new Timer(delayMillis, event -> tickNow());
        timer.setCoalesce(true);
        timer.setInitialDelay(delayMillis);
        timer.start();
    }

    private void cancelScheduledTask() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
