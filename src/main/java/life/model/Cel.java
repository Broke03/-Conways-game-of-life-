package life.model;

public abstract class Cel {
    private final Rasterplaats positie;

    protected Cel(Rasterplaats positie) {
        this.positie = positie;
    }

    public Rasterplaats getPositie() {
        return positie;
    }

    public abstract CelSoort getSoort();

    public abstract CelVorm getVorm();

    public abstract Cel kopieerNaar(Rasterplaats positie);
}
