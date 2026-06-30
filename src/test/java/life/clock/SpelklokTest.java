package life.clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpelklokTest {
    private final Spelklok klok = new Spelklok(40, 20, 200);

    @AfterEach
    void opruimen() {
        klok.afsluiten();
    }

    @Test
    void luisteraarOntvangtTikken() {
        CountDownLatch grendel = new CountDownLatch(1);
        klok.voegLuisteraarToe(tikNummer -> grendel.countDown());

        klok.starten();

        assertTrue(wachtOpGrendel(grendel));
    }

    @Test
    void meerdereLuisteraarsOntvangenTikken() {
        CountDownLatch grendel = new CountDownLatch(2);
        klok.voegLuisteraarToe(tikNummer -> grendel.countDown());
        klok.voegLuisteraarToe(tikNummer -> grendel.countDown());

        klok.tikNu();

        assertTrue(wachtOpGrendel(grendel));
    }

    @Test
    void tikNummerNeemtToe() {
        AtomicLong laatsteTik = new AtomicLong();
        klok.voegLuisteraarToe(laatsteTik::set);

        klok.tikNu();
        klok.tikNu();

        assertEquals(2, klok.getTikNummer());
        assertEquals(2, laatsteTik.get());
    }

    @Test
    void snellerEnLangzamerPassenVertragingAanZonderKlokTeBlokkeren() {
        int oorspronkelijkeVertraging = klok.getVertragingMillis();

        klok.sneller();
        int snellereVertraging = klok.getVertragingMillis();
        klok.langzamer();
        int langzamereVertraging = klok.getVertragingMillis();

        assertTrue(snellereVertraging < oorspronkelijkeVertraging);
        assertTrue(langzamereVertraging > snellereVertraging);
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
