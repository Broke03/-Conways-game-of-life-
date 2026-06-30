package life.controller;

import life.model.CelSoort;
import life.model.Rasterplaats;
import life.model.LevenSimulatie;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

public final class SimulatieBestuurder {
    private final LevenSimulatie simulatie;
    private PlaatsingsHulpmiddel geselecteerdHulpmiddel = PlaatsingsHulpmiddel.CONWAY;

    public SimulatieBestuurder(LevenSimulatie simulatie) {
        this.simulatie = simulatie;
    }

    public void setGeselecteerdHulpmiddel(PlaatsingsHulpmiddel geselecteerdHulpmiddel) {
        this.geselecteerdHulpmiddel = geselecteerdHulpmiddel;
    }

    public PlaatsingsHulpmiddel getGeselecteerdHulpmiddel() {
        return geselecteerdHulpmiddel;
    }

    public void starten() {
        simulatie.starten();
    }

    public void pauzeer() {
        simulatie.pauzeer();
    }

    public void hervat() {
        simulatie.hervat();
    }

    public void herstel() {
        simulatie.herstel();
    }

    public void versnellen() {
        simulatie.versnellen();
    }

    public void vertragen() {
        simulatie.vertragen();
    }

    public void verwerkBordInteractie(Rasterplaats positie, MouseEvent gebeurtenis) {
        if (!SwingUtilities.isRightMouseButton(gebeurtenis) && simulatie.isBezet(positie.rij(), positie.kolom())) {
            simulatie.verwijderCel(positie.rij(), positie.kolom());
            return;
        }

        if (SwingUtilities.isRightMouseButton(gebeurtenis)) {
            simulatie.plaatsCel(positie.rij(), positie.kolom(), CelSoort.ALTERNATIEF);
            return;
        }

        if (gebeurtenis.isShiftDown() || geselecteerdHulpmiddel == PlaatsingsHulpmiddel.WISSEN) {
            simulatie.verwijderCel(positie.rij(), positie.kolom());
            return;
        }

        CelSoort tePlaatsenSoort = geselecteerdHulpmiddel == PlaatsingsHulpmiddel.ALTERNATIEF
                ? CelSoort.ALTERNATIEF
                : CelSoort.CONWAY;

        simulatie.plaatsCel(positie.rij(), positie.kolom(), tePlaatsenSoort);
    }
}
