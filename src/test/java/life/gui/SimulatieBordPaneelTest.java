package life.gui;

import life.clock.Spelklok;
import life.controller.SimulatieBestuurder;
import life.model.StandaardCelFabriek;
import life.model.LevenSimulatie;
import life.model.Regelboek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatieBordPaneelTest {
    private static final int CEL_GROOTTE = 20;

    private final Spelklok spelklok = new Spelklok(40, 20, 200);
    private final LevenSimulatie simulatie = new LevenSimulatie(
            20, 20, new StandaardCelFabriek(), Regelboek.maakStandaard(), spelklok
    );
    private final SimulatieBestuurder bestuurder = new SimulatieBestuurder(simulatie);
    private final SimulatieBordPaneel paneel = new SimulatieBordPaneel(bestuurder, simulatie.getOpname());

    @AfterEach
    void tearDown() {
        spelklok.afsluiten();
    }

    @Test
    void muisKlikPlaatsCel() {
        paneel.dispatchEvent(muisIndrukkenOp(CEL_GROOTTE / 2, CEL_GROOTTE / 2, MouseEvent.BUTTON1));

        assertTrue(simulatie.isBezet(0, 0), "Linker klik zou een cel moeten plaatsen");
    }

    @Test
    void muisSleepPlaatseGeenCellen() {
        paneel.dispatchEvent(muisIndrukkenOp(CEL_GROOTTE / 2, CEL_GROOTTE / 2, MouseEvent.BUTTON1));
        int aantalCellenNaKlik = simulatie.getOpname().totaalAantalCellen();

        paneel.dispatchEvent(muisSlepenOp(CEL_GROOTTE + CEL_GROOTTE / 2, CEL_GROOTTE / 2));
        paneel.dispatchEvent(muisSlepenOp(2 * CEL_GROOTTE + CEL_GROOTTE / 2, CEL_GROOTTE / 2));

        assertEquals(aantalCellenNaKlik, simulatie.getOpname().totaalAantalCellen(),
                "Slepen van de muis zou geen extra cellen mogen plaatsen");
    }

    private MouseEvent muisIndrukkenOp(int x, int y, int knop) {
        return new MouseEvent(
                paneel,
                MouseEvent.MOUSE_PRESSED,
                System.currentTimeMillis(),
                knop == MouseEvent.BUTTON1 ? MouseEvent.BUTTON1_DOWN_MASK : 0,
                x, y, 1, false, knop
        );
    }

    private MouseEvent muisSlepenOp(int x, int y) {
        return new MouseEvent(
                paneel,
                MouseEvent.MOUSE_DRAGGED,
                System.currentTimeMillis(),
                MouseEvent.BUTTON1_DOWN_MASK,
                x, y, 0, false, MouseEvent.NOBUTTON
        );
    }
}
