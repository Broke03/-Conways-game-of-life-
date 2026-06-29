package life.model;

public final class AlternativeCell extends Cell {
    public AlternativeCell(GridPosition position) {
        super(position);
    }

    @Override
    public CellType getType() {
        return CellType.ALTERNATIVE;
    }

    @Override
    public CellShape getShape() {
        return CellShape.CIRCLE;
    }

    @Override
    public Cell copyTo(GridPosition position) {
        return new AlternativeCell(position);
    }
}
