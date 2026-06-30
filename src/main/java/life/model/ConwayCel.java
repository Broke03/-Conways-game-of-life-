package life.model;

public final class ConwayCel extends Cel {
    public ConwayCel(Rasterplaats positie) {
        super(positie);
    }

    @Override
    public CelSoort getSoort() {
        return CelSoort.CONWAY;
    }

    @Override
    public CelVorm getVorm() {
        return CelVorm.VIERKANT;
    }

    @Override
    public Cel kopieerNaar(Rasterplaats positie) {
        return new ConwayCel(positie);
    }
}
