package life.clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameClockTest {
    private final GameClock clock = new GameClock(40, 20, 200);

    @AfterEach
    void tearDown() {
        clock.shutdown();
    }

    @Test
    void subscriberReceivesTicks() {
        CountDownLatch latch = new CountDownLatch(1);
        clock.addListener(tickNumber -> latch.countDown());

        clock.start();

        assertTrue(await(latch));
    }

    @Test
    void multipleSubscribersReceiveTicks() {
        CountDownLatch latch = new CountDownLatch(2);
        clock.addListener(tickNumber -> latch.countDown());
        clock.addListener(tickNumber -> latch.countDown());

        clock.tickNow();

        assertTrue(await(latch));
    }

    @Test
    void tickNumberIncreases() {
        AtomicLong lastTick = new AtomicLong();
        clock.addListener(lastTick::set);

        clock.tickNow();
        clock.tickNow();

        assertEquals(2, clock.getTickNumber());
        assertEquals(2, lastTick.get());
    }

    @Test
    void fasterAndSlowerAdjustDelayWithoutFreezingClock() {
        int originalDelay = clock.getDelayMillis();

        clock.faster();
        int fasterDelay = clock.getDelayMillis();
        clock.slower();
        int slowerDelay = clock.getDelayMillis();

        assertTrue(fasterDelay < originalDelay);
        assertTrue(slowerDelay > fasterDelay);
    }

    private boolean await(CountDownLatch latch) {
        try {
            return latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
