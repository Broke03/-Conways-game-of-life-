package life.model;

import life.clock.GameClock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LifeSimulationTest {
    private final GameClock clock = new GameClock(40, 20, 200);
    private final LifeSimulation simulation = new LifeSimulation(
            30,
            30,
            new DefaultCellFactory(),
            RuleBook.createDefault(),
            clock
    );

    @AfterEach
    void tearDown() {
        clock.shutdown();
    }

    @Test
    void placingCellUpdatesSnapshot() {
        simulation.placeCell(10, 10, CellType.CONWAY);

        SimulationSnapshot snapshot = simulation.getSnapshot();

        assertEquals(1, snapshot.conwayCellCount());
        assertEquals(1, snapshot.totalCellCount());
    }

    @Test
    void resetClearsAllCells() {
        simulation.placeCell(10, 10, CellType.CONWAY);
        simulation.placeCell(10, 11, CellType.CONWAY);
        simulation.placeCell(10, 12, CellType.CONWAY);

        simulation.start();
        clock.tickNow();
        simulation.reset();

        SimulationSnapshot snapshot = simulation.getSnapshot();
        assertEquals(0, snapshot.conwayCellCount());
        assertEquals(0, snapshot.totalCellCount());
        assertFalse(snapshot.cells().containsKey(new GridPosition(10, 10)));
        assertFalse(snapshot.cells().containsKey(new GridPosition(10, 11)));
        assertFalse(snapshot.cells().containsKey(new GridPosition(10, 12)));
        assertFalse(snapshot.running());
    }

    @Test
    void listenersReceiveUpdates() {
        CountDownLatch latch = new CountDownLatch(1);
        simulation.addListener(snapshot -> latch.countDown());

        simulation.placeCell(8, 8, CellType.ALTERNATIVE);

        flushEventQueue();
        assertTrue(await(latch));
    }

    @Test
    void speedChangesAreReflectedInSnapshot() {
        int originalDelay = simulation.getSnapshot().delayMillis();

        simulation.speedUp();
        int fasterDelay = simulation.getSnapshot().delayMillis();
        simulation.slowDown();
        int slowerDelay = simulation.getSnapshot().delayMillis();

        assertTrue(fasterDelay < originalDelay);
        assertTrue(slowerDelay > fasterDelay);
        assertFalse(simulation.getSnapshot().running());
    }

    private void flushEventQueue() {
        try {
            SwingUtilities.invokeAndWait(() -> {
            });
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
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
