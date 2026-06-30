package life.model;

public final class AlternatieveCel extends Cel {
    public AlternatieveCel(Rasterplaats positie) {
        super(positie);
    }

    @Override
    public CelSoort getSoort() {
        return CelSoort.ALTERNATIEF;
    }

    @Override
    public CelVorm getVorm() {
        return CelVorm.ALTERNATIEF;
    }

    @Override
    public Cel kopieerNaar(Rasterplaats positie) {
        return new AlternatieveCel(positie);
    }
}
