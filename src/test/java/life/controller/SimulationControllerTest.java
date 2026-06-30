package life.controller;

import life.clock.GameClock;
import life.model.CellType;
import life.model.DefaultCellFactory;
import life.model.GridPosition;
import life.model.LifeSimulation;
import life.model.RuleBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationControllerTest {
    private final GameClock clock = new GameClock(40, 20, 200);
    private final LifeSimulation simulation = new LifeSimulation(
            20,
            20,
            new DefaultCellFactory(),
            RuleBook.createDefault(),
            clock
    );
    private final SimulationController controller = new SimulationController(simulation);

    @AfterEach
    void tearDown() {
        clock.shutdown();
    }

    @Test
    void clickingEmptyCellPlacesCell() {
        controller.handleBoardInteraction(new GridPosition(5, 5), leftClick());

        assertTrue(simulation.isOccupied(5, 5));
    }

    @Test
    void clickingOccupiedCellRemovesCell() {
        simulation.placeCell(5, 5, CellType.CONWAY);

        controller.handleBoardInteraction(new GridPosition(5, 5), leftClick());

        assertFalse(simulation.isOccupied(5, 5));
    }

    private MouseEvent leftClick() {
        return new MouseEvent(
                new JPanel(),
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                5,
                5,
                1,
                false,
                MouseEvent.BUTTON1
        );
    }
}
