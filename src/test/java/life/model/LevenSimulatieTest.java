package life.model;

import life.clock.Spelklok;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevenSimulatieTest {
    private final Spelklok klok = new Spelklok(40, 20, 200);
    private final LevenSimulatie simulatie = new LevenSimulatie(
            30,
            30,
            new StandaardCelFabriek(),
            Regelboek.maakStandaard(),
            klok
    );

    @AfterEach
    void opruimen() {
        klok.afsluiten();
    }

    @Test
    void plaatsenVanCelWerktOpnameBij() {
        simulatie.plaatsCel(10, 10, CelSoort.CONWAY);

        SimulatieOpname opname = simulatie.getOpname();

        assertEquals(1, opname.conwayAantalCellen());
        assertEquals(1, opname.totaalAantalCellen());
    }

    @Test
    void herstellenWistAlleCellen() {
        simulatie.plaatsCel(10, 10, CelSoort.CONWAY);
        simulatie.plaatsCel(10, 11, CelSoort.CONWAY);
        simulatie.plaatsCel(10, 12, CelSoort.CONWAY);

        simulatie.starten();
        klok.tikNu();
        simulatie.herstel();

        SimulatieOpname opname = simulatie.getOpname();
        assertEquals(0, opname.conwayAantalCellen());
        assertEquals(0, opname.totaalAantalCellen());
        assertFalse(opname.cellen().containsKey(new Rasterplaats(10, 10)));
        assertFalse(opname.cellen().containsKey(new Rasterplaats(10, 11)));
        assertFalse(opname.cellen().containsKey(new Rasterplaats(10, 12)));
        assertFalse(opname.actief());
    }

    @Test
    void luisteraarsOntvangenUpdates() {
        CountDownLatch grendel = new CountDownLatch(1);
        simulatie.voegLuisteraarToe(opname -> grendel.countDown());

        simulatie.plaatsCel(8, 8, CelSoort.ALTERNATIEF);

        verwerkGebeurtenissenWachtrij();
        assertTrue(wachtOpGrendel(grendel));
    }

    @Test
    void snelheidswijzigingenZijnZichtbaarInOpname() {
        int oorspronkelijkeVertraging = simulatie.getOpname().vertragingMillis();

        simulatie.versnellen();
        int snellereVertraging = simulatie.getOpname().vertragingMillis();
        simulatie.vertragen();
        int langzamereVertraging = simulatie.getOpname().vertragingMillis();

        assertTrue(snellereVertraging < oorspronkelijkeVertraging);
        assertTrue(langzamereVertraging > snellereVertraging);
        assertFalse(simulatie.getOpname().actief());
    }

    private void verwerkGebeurtenissenWachtrij() {
        try {
            SwingUtilities.invokeAndWait(() -> {
            });
        } catch (Exception uitzondering) {
            throw new RuntimeException(uitzondering);
        }
    }

    private boolean wachtOpGrendel(CountDownLatch grendel) {
        try {
            return grendel.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException uitzondering) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
