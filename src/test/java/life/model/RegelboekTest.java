package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegelboekTest {
    private final Regelboek regelboek = Regelboek.maakStandaard();

    @Test
    void geeftJuisteRegelVoorCelSoort() {
        assertEquals(CelSoort.CONWAY, regelboek.getRegel(CelSoort.CONWAY).getCelSoort());
        assertEquals(CelSoort.ALTERNATIEF, regelboek.getRegel(CelSoort.ALTERNATIEF).getCelSoort());
    }

    @Test
    void verkiestAlternatieveGeboorteWanneerBeideGeboorteregelsKloppen() {
        BuurtOpname buurtOpname = new BuurtOpname(
                Map.of(
                        CelSoort.CONWAY, 3,
                        CelSoort.ALTERNATIEF, 4
                ),
                7
        );

        assertTrue(regelboek.bepaalGeboorteType(buurtOpname).isPresent());
        assertEquals(CelSoort.ALTERNATIEF, regelboek.bepaalGeboorteType(buurtOpname).orElseThrow());
    }
}
