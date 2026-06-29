package life.model;

public final class ConwayCell extends Cell {
    public ConwayCell(GridPosition position) {
        super(position);
    }

    @Override
    public CellType getType() {
        return CellType.CONWAY;
    }

    @Override
    public CellShape getShape() {
        return CellShape.SQUARE;
    }

    @Override
    public Cell copyTo(GridPosition position) {
        return new ConwayCell(position);
    }
}
