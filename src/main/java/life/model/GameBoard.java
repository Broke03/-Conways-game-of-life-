package life.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GameBoard {
    private static final int[] OFFSETS = {-1, 0, 1};

    private final int rows;
    private final int columns;
    private final CellFactory cellFactory;
    private final RuleBook ruleBook;
    private final Map<GridPosition, Cell> cells;

    public GameBoard(int rows, int columns, CellFactory cellFactory, RuleBook ruleBook) {
        this(rows, columns, cellFactory, ruleBook, new HashMap<>());
    }

    private GameBoard(int rows, int columns, CellFactory cellFactory, RuleBook ruleBook, Map<GridPosition, Cell> cells) {
        this.rows = rows;
        this.columns = columns;
        this.cellFactory = cellFactory;
        this.ruleBook = ruleBook;
        this.cells = cells;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public void addCell(int row, int column, CellType type) {
        GridPosition position = new GridPosition(row, column);
        ensureInBounds(position);
        cells.put(position, cellFactory.createCell(type, position));
    }

    public void removeCell(int row, int column) {
        cells.remove(new GridPosition(row, column));
    }

    public Cell getCellAt(int row, int column) {
        return cells.get(new GridPosition(row, column));
    }

    public boolean isOccupied(int row, int column) {
        return getCellAt(row, column) != null;
    }

    public int getCellCount(CellType type) {
        return (int) cells.values().stream()
                .filter(cell -> cell.getType() == type)
                .count();
    }

    public int getTotalCellCount() {
        return cells.size();
    }

    public Map<GridPosition, Cell> getCellsView() {
        return Collections.unmodifiableMap(cells);
    }

    public GameBoard copy() {
        Map<GridPosition, Cell> copiedCells = new HashMap<>();
        for (Cell cell : cells.values()) {
            copiedCells.put(cell.getPosition(), cell.copyTo(cell.getPosition()));
        }
        return new GameBoard(rows, columns, cellFactory, ruleBook, copiedCells);
    }

    public NeighborhoodSnapshot countNeighborhood(GridPosition position) {
        Map<CellType, Integer> typeCounts = new EnumMap<>(CellType.class);
        int totalNeighbors = 0;

        for (int rowOffset : OFFSETS) {
            for (int columnOffset : OFFSETS) {
                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                GridPosition neighbor = new GridPosition(position.row() + rowOffset, position.column() + columnOffset);
                if (!neighbor.isWithin(rows, columns)) {
                    continue;
                }

                Cell neighborCell = cells.get(neighbor);
                if (neighborCell != null) {
                    totalNeighbors++;
                    typeCounts.merge(neighborCell.getType(), 1, Integer::sum);
                }
            }
        }

        return new NeighborhoodSnapshot(typeCounts, totalNeighbors);
    }

    public GameBoard nextGeneration() {
        Map<GridPosition, Cell> nextGeneration = new HashMap<>();
        Set<GridPosition> candidates = collectCandidatePositions();

        for (GridPosition position : candidates) {
            Cell currentCell = cells.get(position);
            NeighborhoodSnapshot snapshot = countNeighborhood(position);

            if (currentCell != null) {
                CellRule rule = ruleBook.getRule(currentCell.getType());
                if (rule.survives(snapshot)) {
                    nextGeneration.put(position, currentCell.copyTo(position));
                }
                continue;
            }

            ruleBook.determineBirthType(snapshot)
                    .ifPresent(cellType -> nextGeneration.put(position, cellFactory.createCell(cellType, position)));
        }

        return new GameBoard(rows, columns, cellFactory, ruleBook, nextGeneration);
    }

    private Set<GridPosition> collectCandidatePositions() {
        Set<GridPosition> candidates = new HashSet<>();
        for (Cell cell : new ArrayList<>(cells.values())) {
            GridPosition position = cell.getPosition();
            candidates.add(position);

            for (int rowOffset : OFFSETS) {
                for (int columnOffset : OFFSETS) {
                    GridPosition neighbor = new GridPosition(position.row() + rowOffset, position.column() + columnOffset);
                    if (neighbor.isWithin(rows, columns)) {
                        candidates.add(neighbor);
                    }
                }
            }
        }
        return candidates;
    }

    private void ensureInBounds(GridPosition position) {
        if (!position.isWithin(rows, columns)) {
            throw new IllegalArgumentException("Position is outside the board: " + position);
        }
    }
}
