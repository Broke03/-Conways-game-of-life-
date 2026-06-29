package life.model;

public final class DefaultCellFactory implements CellFactory {
    @Override
    public Cell createCell(CellType type, GridPosition position) {
        return switch (type) {
            case CONWAY -> new ConwayCell(position);
            case ALTERNATIVE -> new AlternativeCell(position);
        };
    }
}
