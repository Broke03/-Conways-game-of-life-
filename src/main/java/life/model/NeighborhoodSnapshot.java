package life.model;

import java.util.EnumMap;
import java.util.Map;

public final class NeighborhoodSnapshot {
    private final Map<CellType, Integer> sameTypeCounts;
    private final int totalNeighbors;

    public NeighborhoodSnapshot(Map<CellType, Integer> sameTypeCounts, int totalNeighbors) {
        this.sameTypeCounts = new EnumMap<>(CellType.class);
        this.sameTypeCounts.putAll(sameTypeCounts);
        this.totalNeighbors = totalNeighbors;
    }

    public int sameTypeNeighbors(CellType cellType) {
        return sameTypeCounts.getOrDefault(cellType, 0);
    }

    public int totalNeighbors() {
        return totalNeighbors;
    }
}
