package life.model;

public record GridPosition(int row, int column) {
    public boolean isWithin(int rows, int columns) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }
}
