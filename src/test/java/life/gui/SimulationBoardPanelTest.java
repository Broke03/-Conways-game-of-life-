package life.gui;

import life.clock.GameClock;
import life.controller.SimulationController;
import life.model.DefaultCellFactory;
import life.model.LifeSimulation;
import life.model.RuleBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationBoardPanelTest {
    private static final int CELL_SIZE = 20;

    private final GameClock clock = new GameClock(40, 20, 200);
    private final LifeSimulation simulation = new LifeSimulation(
            20, 20, new DefaultCellFactory(), RuleBook.createDefault(), clock
    );
    private final SimulationController controller = new SimulationController(simulation);
    private final SimulationBoardPanel panel = new SimulationBoardPanel(controller, simulation.getSnapshot());

    @AfterEach
    void tearDown() {
        clock.shutdown();
    }

    @Test
    void mouseClickPlacesCell() {
        panel.dispatchEvent(mousePressAt(CELL_SIZE / 2, CELL_SIZE / 2, MouseEvent.BUTTON1));

        assertTrue(simulation.isOccupied(0, 0), "Left click should place a cell");
    }

    @Test
    void mouseDragDoesNotPlaceCells() {
        panel.dispatchEvent(mousePressAt(CELL_SIZE / 2, CELL_SIZE / 2, MouseEvent.BUTTON1));
        int cellCountAfterClick = simulation.getSnapshot().totalCellCount();

        panel.dispatchEvent(mouseDragAt(CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2));
        panel.dispatchEvent(mouseDragAt(2 * CELL_SIZE + CELL_SIZE / 2, CELL_SIZE / 2));

        assertEquals(cellCountAfterClick, simulation.getSnapshot().totalCellCount(),
                "Dragging the mouse should not place additional cells");
    }

    private MouseEvent mousePressAt(int x, int y, int button) {
        return new MouseEvent(
                panel,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                button == MouseEvent.BUTTON1 ? MouseEvent.BUTTON1_DOWN_MASK : 0,
                x, y, 1, false, button
        );
    }

    private MouseEvent mouseDragAt(int x, int y) {
        return new MouseEvent(
                panel,
                MouseEvent.MOUSE_DRAGGED,
                System.currentTimeMillis(),
                MouseEvent.BUTTON1_DOWN_MASK,
                x, y, 0, false, MouseEvent.NOBUTTON
        );
    }
}
