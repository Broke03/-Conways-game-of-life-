package life.model;

import java.util.Map;

public record SimulatieOpname(
        long tikNummer,
        int vertragingMillis,
        boolean actief,
        int conwayAantalCellen,
        int alternatiefAantalCellen,
        int rijen,
        int kolommen,
        Map<Rasterplaats, Cel> cellen
) {
    public int totaalAantalCellen() {
        return conwayAantalCellen + alternatiefAantalCellen;
    }
}
