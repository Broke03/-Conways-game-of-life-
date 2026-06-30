package life.model;

public final class StandaardCelFabriek implements CelFabriek {
    @Override
    public Cel maakCel(CelSoort celSoort, Rasterplaats positie) {
        return switch (celSoort) {
            case CONWAY -> new ConwayCel(positie);
            case ALTERNATIEF -> new AlternatieveCel(positie);
        };
    }
}
