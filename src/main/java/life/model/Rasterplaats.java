package life.model;

public record Rasterplaats(int rij, int kolom) {
    public boolean isBinnen(int rijen, int kolommen) {
        return rij >= 0 && rij < rijen && kolom >= 0 && kolom < kolommen;
    }
}
