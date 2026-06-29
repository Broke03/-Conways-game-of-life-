package life.model;

import life.clock.GameClock;
import life.clock.TickListener;

import javax.swing.SwingUtilities;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LifeSimulation implements TickListener {
    private final CopyOnWriteArrayList<SimulationListener> listeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SimulationLogListener> logListeners = new CopyOnWriteArrayList<>();
    private final Object lock = new Object();
    private final GameClock gameClock;
    private GameBoard seedBoard;
    private GameBoard activeBoard;

    public LifeSimulation(int rows, int columns, CellFactory cellFactory, RuleBook ruleBook, GameClock gameClock) {
        this.seedBoard = new GameBoard(rows, columns, cellFactory, ruleBook);
        this.activeBoard = seedBoard.copy();
        this.gameClock = gameClock;
        this.gameClock.addListener(this);
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
        listener.onSimulationChanged(getSnapshot());
    }

    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    public void addLogListener(SimulationLogListener listener) {
        logListeners.add(listener);
    }

    public void removeLogListener(SimulationLogListener listener) {
        logListeners.remove(listener);
    }

    public void placeCell(int row, int column, CellType type) {
        synchronized (lock) {
            if (gameClock.isRunning()) {
                return;
            }
            seedBoard.addCell(row, column, type);
            activeBoard = seedBoard.copy();
        }
        notifyListeners();
    }

    public void removeCell(int row, int column) {
        synchronized (lock) {
            if (gameClock.isRunning()) {
                return;
            }
            seedBoard.removeCell(row, column);
            activeBoard = seedBoard.copy();
        }
        notifyListeners();
    }

    public void start() {
        synchronized (lock) {
            activeBoard = seedBoard.copy();
            gameClock.start();
        }
        log("Simulation started at " + gameClock.getDelayMillis() + " ms per tick.");
        notifyListeners();
    }

    public void pause() {
        gameClock.pause();
        log("Simulation paused on tick " + gameClock.getTickNumber() + ".");
        notifyListeners();
    }

    public void resume() {
        gameClock.resume();
        log("Simulation resumed at " + gameClock.getDelayMillis() + " ms per tick.");
        notifyListeners();
    }

    public void reset() {
        synchronized (lock) {
            gameClock.reset();
            activeBoard = seedBoard.copy();
        }
        log("Simulation reset to the original seed pattern.");
        notifyListeners();
    }

    public void speedUp() {
        gameClock.faster();
        log("Speed increased. Delay is now " + gameClock.getDelayMillis() + " ms.");
        notifyListeners();
    }

    public void slowDown() {
        gameClock.slower();
        log("Speed decreased. Delay is now " + gameClock.getDelayMillis() + " ms.");
        notifyListeners();
    }

    public SimulationSnapshot getSnapshot() {
        synchronized (lock) {
            Map<GridPosition, Cell> copiedCells = new HashMap<>(activeBoard.getCellsView());
            return new SimulationSnapshot(
                    gameClock.getTickNumber(),
                    gameClock.getDelayMillis(),
                    gameClock.isRunning(),
                    activeBoard.getCellCount(CellType.CONWAY),
                    activeBoard.getCellCount(CellType.ALTERNATIVE),
                    activeBoard.getRows(),
                    activeBoard.getColumns(),
                    copiedCells
            );
        }
    }

    public int getCenterRow() {
        synchronized (lock) {
            return activeBoard.getRows() / 2;
        }
    }

    public int getCenterColumn() {
        synchronized (lock) {
            return activeBoard.getColumns() / 2;
        }
    }

    @Override
    public void onTick(long tickNumber) {
        SimulationSnapshot snapshotAfterTick;
        synchronized (lock) {
            activeBoard = activeBoard.nextGeneration();
            snapshotAfterTick = getSnapshot();
        }

        log(String.format(
                "Tick %d | Conway: %d | Alternative: %d | Total: %d | Delay: %d ms",
                snapshotAfterTick.tickNumber(),
                snapshotAfterTick.conwayCellCount(),
                snapshotAfterTick.alternativeCellCount(),
                snapshotAfterTick.totalCellCount(),
                snapshotAfterTick.delayMillis()
        ));

        notifyListeners(snapshotAfterTick);
    }

    private void notifyListeners() {
        notifyListeners(getSnapshot());
    }

    private void notifyListeners(SimulationSnapshot snapshot) {
        SwingUtilities.invokeLater(() -> {
            for (SimulationListener listener : listeners) {
                listener.onSimulationChanged(snapshot);
            }
        });
    }

    private void log(String message) {
        System.out.println(message);
        SwingUtilities.invokeLater(() -> {
            for (SimulationLogListener listener : logListeners) {
                listener.onLogMessage(message);
            }
        });
    }
}
