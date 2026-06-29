package life.model;

public interface CellFactory {
    Cell createCell(CellType type, GridPosition position);
}
