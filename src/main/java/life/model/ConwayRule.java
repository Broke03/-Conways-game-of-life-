package life.model;

public final class ConwayRule implements CellRule {
    @Override
    public CellType getCellType() {
        return CellType.CONWAY;
    }

    @Override
    public boolean survives(NeighborhoodSnapshot snapshot) {
        int totalNeighbors = snapshot.totalNeighbors();
        return totalNeighbors == 2 || totalNeighbors == 3;
    }

    @Override
    public boolean isBorn(NeighborhoodSnapshot snapshot) {
        return snapshot.sameTypeNeighbors(CellType.CONWAY) == 3;
    }
}
