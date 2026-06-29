package life.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard(20, 20, new DefaultCellFactory(), RuleBook.createDefault());
    }

    @Test
    void countsNeighborCellsAcrossBothTypes() {
        board.addCell(10, 10, CellType.CONWAY);
        board.addCell(10, 11, CellType.ALTERNATIVE);
        board.addCell(11, 10, CellType.CONWAY);

        NeighborhoodSnapshot snapshot = board.countNeighborhood(new GridPosition(11, 11));

        assertEquals(3, snapshot.totalNeighbors());
        assertEquals(2, snapshot.sameTypeNeighbors(CellType.CONWAY));
        assertEquals(1, snapshot.sameTypeNeighbors(CellType.ALTERNATIVE));
    }

    @Test
    void addsCellToBoard() {
        board.addCell(5, 5, CellType.CONWAY);

        assertNotNull(board.getCellAt(5, 5));
        assertEquals(1, board.getTotalCellCount());
    }

    @Test
    void removesCellFromBoard() {
        board.addCell(5, 5, CellType.ALTERNATIVE);

        board.removeCell(5, 5);

        assertNull(board.getCellAt(5, 5));
        assertEquals(0, board.getTotalCellCount());
    }

    @Test
    void computesNewGeneration() {
        board.addCell(4, 5, CellType.CONWAY);
        board.addCell(5, 5, CellType.CONWAY);
        board.addCell(6, 5, CellType.CONWAY);

        GameBoard nextGeneration = board.nextGeneration();

        assertTrue(nextGeneration.isOccupied(5, 4));
        assertTrue(nextGeneration.isOccupied(5, 5));
        assertTrue(nextGeneration.isOccupied(5, 6));
        assertFalse(nextGeneration.isOccupied(4, 5));
        assertFalse(nextGeneration.isOccupied(6, 5));
    }
}
