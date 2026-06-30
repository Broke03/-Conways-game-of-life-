package life.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Speelbord {
    private static final int[] VERSCHUIVINGEN = {-1, 0, 1};

    private final int rijen;
    private final int kolommen;
    private final CelFabriek celFabriek;
    private final Regelboek regelboek;
    private final Map<Rasterplaats, Cel> cellen;

    public Speelbord(int rijen, int kolommen, CelFabriek celFabriek, Regelboek regelboek) {
        this(rijen, kolommen, celFabriek, regelboek, new HashMap<>());
    }

    private Speelbord(int rijen, int kolommen, CelFabriek celFabriek, Regelboek regelboek, Map<Rasterplaats, Cel> cellen) {
        this.rijen = rijen;
        this.kolommen = kolommen;
        this.celFabriek = celFabriek;
        this.regelboek = regelboek;
        this.cellen = cellen;
    }

    public int getRijen() {
        return rijen;
    }

    public int getKolommen() {
        return kolommen;
    }

    public void voegCelToe(int rij, int kolom, CelSoort celSoort) {
        Rasterplaats positie = new Rasterplaats(rij, kolom);
        controleerBinnenGrenzen(positie);
        cellen.put(positie, celFabriek.maakCel(celSoort, positie));
    }

    public void verwijderCel(int rij, int kolom) {
        cellen.remove(new Rasterplaats(rij, kolom));
    }

    public Cel getCelOp(int rij, int kolom) {
        return cellen.get(new Rasterplaats(rij, kolom));
    }

    public boolean isBezet(int rij, int kolom) {
        return getCelOp(rij, kolom) != null;
    }

    public int getAantalCellen(CelSoort celSoort) {
        return (int) cellen.values().stream()
                .filter(cel -> cel.getSoort() == celSoort)
                .count();
    }

    public int getTotaalAantalCellen() {
        return cellen.size();
    }

    public Map<Rasterplaats, Cel> getCellenWeergave() {
        return Collections.unmodifiableMap(cellen);
    }

    public void wissen() {
        cellen.clear();
    }

    public Speelbord kopieer() {
        Map<Rasterplaats, Cel> gekopieerdeCellen = new HashMap<>();
        for (Cel cel : cellen.values()) {
            gekopieerdeCellen.put(cel.getPositie(), cel.kopieerNaar(cel.getPositie()));
        }
        return new Speelbord(rijen, kolommen, celFabriek, regelboek, gekopieerdeCellen);
    }

    public BuurtOpname telBuurt(Rasterplaats positie) {
        Map<CelSoort, Integer> soortTellingen = new EnumMap<>(CelSoort.class);
        int totaalBuren = 0;

        for (int rijVerschuiving : VERSCHUIVINGEN) {
            for (int kolomVerschuiving : VERSCHUIVINGEN) {
                if (rijVerschuiving == 0 && kolomVerschuiving == 0) {
                    continue;
                }

                Rasterplaats buurPositie = new Rasterplaats(
                        positie.rij() + rijVerschuiving,
                        positie.kolom() + kolomVerschuiving
                );
                if (!buurPositie.isBinnen(rijen, kolommen)) {
                    continue;
                }

                Cel buurCel = cellen.get(buurPositie);
                if (buurCel != null) {
                    totaalBuren++;
                    soortTellingen.merge(buurCel.getSoort(), 1, Integer::sum);
                }
            }
        }

        return new BuurtOpname(soortTellingen, totaalBuren);
    }

    public Speelbord volgendeGeneratie() {
        Map<Rasterplaats, Cel> volgendeGeneratie = new HashMap<>();
        Set<Rasterplaats> kandidaatPosities = verzamelKandidaatPosities();

        for (Rasterplaats positie : kandidaatPosities) {
            Cel huidigeCel = cellen.get(positie);
            BuurtOpname buurtOpname = telBuurt(positie);

            if (huidigeCel != null) {
                CelRegel regel = regelboek.getRegel(huidigeCel.getSoort());
                if (regel.overleeft(buurtOpname)) {
                    volgendeGeneratie.put(positie, huidigeCel.kopieerNaar(positie));
                }
                continue;
            }

            regelboek.bepaalGeboorteType(buurtOpname)
                    .ifPresent(celSoort -> volgendeGeneratie.put(positie, celFabriek.maakCel(celSoort, positie)));
        }

        return new Speelbord(rijen, kolommen, celFabriek, regelboek, volgendeGeneratie);
    }

    private Set<Rasterplaats> verzamelKandidaatPosities() {
        Set<Rasterplaats> kandidaatPosities = new HashSet<>();
        for (Cel cel : new ArrayList<>(cellen.values())) {
            Rasterplaats positie = cel.getPositie();
            kandidaatPosities.add(positie);

            for (int rijVerschuiving : VERSCHUIVINGEN) {
                for (int kolomVerschuiving : VERSCHUIVINGEN) {
                    Rasterplaats buurPositie = new Rasterplaats(
                            positie.rij() + rijVerschuiving,
                            positie.kolom() + kolomVerschuiving
                    );
                    if (buurPositie.isBinnen(rijen, kolommen)) {
                        kandidaatPosities.add(buurPositie);
                    }
                }
            }
        }
        return kandidaatPosities;
    }

    private void controleerBinnenGrenzen(Rasterplaats positie) {
        if (!positie.isBinnen(rijen, kolommen)) {
            throw new IllegalArgumentException("Positie ligt buiten het bord: " + positie);
        }
    }
}
