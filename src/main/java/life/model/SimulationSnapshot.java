package life.model;

import java.util.Map;

public record SimulationSnapshot(
        long tickNumber,
        int delayMillis,
        boolean running,
        int conwayCellCount,
        int alternativeCellCount,
        int rows,
        int columns,
        Map<GridPosition, Cell> cells
) {
    public int totalCellCount() {
        return conwayCellCount + alternativeCellCount;
    }
}
