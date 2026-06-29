package life.model;

public abstract class Cell {
    private final GridPosition position;

    protected Cell(GridPosition position) {
        this.position = position;
    }

    public GridPosition getPosition() {
        return position;
    }

    public abstract CellType getType();

    public abstract CellShape getShape();

    public abstract Cell copyTo(GridPosition position);
}
