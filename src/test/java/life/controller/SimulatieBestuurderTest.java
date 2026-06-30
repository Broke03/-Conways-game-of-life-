package life.controller;

import life.clock.Spelklok;
import life.model.CelSoort;
import life.model.StandaardCelFabriek;
import life.model.Rasterplaats;
import life.model.LevenSimulatie;
import life.model.Regelboek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.JPanel;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatieBestuurderTest {
    private final Spelklok klok = new Spelklok(40, 20, 200);
    private final LevenSimulatie simulatie = new LevenSimulatie(
            20,
            20,
            new StandaardCelFabriek(),
            Regelboek.maakStandaard(),
            klok
    );
    private final SimulatieBestuurder bestuurder = new SimulatieBestuurder(simulatie);

    @AfterEach
    void opruimen() {
        klok.afsluiten();
    }

    @Test
    void klikkenOpLegeCelPlaatstCel() {
        bestuurder.verwerkBordInteractie(new Rasterplaats(5, 5), linkerKlik());

        assertTrue(simulatie.isBezet(5, 5));
    }

    @Test
    void klikkenOpBezetteCelVerwijdertCel() {
        simulatie.plaatsCel(5, 5, CelSoort.CONWAY);

        bestuurder.verwerkBordInteractie(new Rasterplaats(5, 5), linkerKlik());

        assertFalse(simulatie.isBezet(5, 5));
    }

    @Test
    void rechtsKlikOpBezetteCelBehoudtCelEnPastAlternatieveSoortToe() {
        Rasterplaats positie = new Rasterplaats(5, 5);
        simulatie.plaatsCel(positie.rij(), positie.kolom(), CelSoort.CONWAY);

        bestuurder.verwerkBordInteractie(positie, rechterKlik());

        assertTrue(simulatie.isBezet(positie.rij(), positie.kolom()));
        assertEquals(CelSoort.ALTERNATIEF, simulatie.getOpname().cellen().get(positie).getSoort());
    }

    private MouseEvent linkerKlik() {
        return new MouseEvent(
                new JPanel(),
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                5,
                5,
                1,
                false,
                MouseEvent.BUTTON1
        );
    }

    private MouseEvent rechterKlik() {
        return new MouseEvent(
                new JPanel(),
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                0,
                5,
                5,
                1,
                false,
                MouseEvent.BUTTON3
        );
    }
}
