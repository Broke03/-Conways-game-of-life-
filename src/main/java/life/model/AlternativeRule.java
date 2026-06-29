package life.model;

public final class AlternativeRule implements CellRule {
    @Override
    public CellType getCellType() {
        return CellType.ALTERNATIVE;
    }

    @Override
    public boolean survives(NeighborhoodSnapshot snapshot) {
        int totalNeighbors = snapshot.totalNeighbors();
        return totalNeighbors >= 2 && totalNeighbors <= 4;
    }

    @Override
    public boolean isBorn(NeighborhoodSnapshot snapshot) {
        return snapshot.sameTypeNeighbors(CellType.ALTERNATIVE) == 4;
    }
}
