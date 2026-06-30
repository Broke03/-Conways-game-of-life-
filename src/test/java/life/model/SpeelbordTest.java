package life.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeelbordTest {
    private Speelbord bord;

    @BeforeEach
    void instellen() {
        bord = new Speelbord(20, 20, new StandaardCelFabriek(), Regelboek.maakStandaard());
    }

    @Test
    void teltBuurtcellenOverBeideSoorten() {
        bord.voegCelToe(10, 10, CelSoort.CONWAY);
        bord.voegCelToe(10, 11, CelSoort.ALTERNATIEF);
        bord.voegCelToe(11, 10, CelSoort.CONWAY);

        BuurtOpname buurtOpname = bord.telBuurt(new Rasterplaats(11, 11));

        assertEquals(3, buurtOpname.totaalBuren());
        assertEquals(2, buurtOpname.zelfdeTypeBuren(CelSoort.CONWAY));
        assertEquals(1, buurtOpname.zelfdeTypeBuren(CelSoort.ALTERNATIEF));
    }

    @Test
    void voegtCelToeAanBord() {
        bord.voegCelToe(5, 5, CelSoort.CONWAY);

        assertNotNull(bord.getCelOp(5, 5));
        assertEquals(1, bord.getTotaalAantalCellen());
    }

    @Test
    void verwijdertCelVanBord() {
        bord.voegCelToe(5, 5, CelSoort.ALTERNATIEF);

        bord.verwijderCel(5, 5);

        assertNull(bord.getCelOp(5, 5));
        assertEquals(0, bord.getTotaalAantalCellen());
    }

    @Test
    void berekentVolgendeGeneratie() {
        bord.voegCelToe(4, 5, CelSoort.CONWAY);
        bord.voegCelToe(5, 5, CelSoort.CONWAY);
        bord.voegCelToe(6, 5, CelSoort.CONWAY);

        Speelbord volgendeGeneratie = bord.volgendeGeneratie();

        assertTrue(volgendeGeneratie.isBezet(5, 4));
        assertTrue(volgendeGeneratie.isBezet(5, 5));
        assertTrue(volgendeGeneratie.isBezet(5, 6));
        assertFalse(volgendeGeneratie.isBezet(4, 5));
        assertFalse(volgendeGeneratie.isBezet(6, 5));
    }
}
