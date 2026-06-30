package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConwayRegelTest {
    private final ConwayRegel regel = new ConwayRegel();

    @Test
    void geboorteMetExactDrieConwayBuren() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.CONWAY, 3), 3);

        assertTrue(regel.wordtGeboren(buurtOpname));
    }

    @Test
    void celOverleeftMetTweeBuren() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.CONWAY, 2), 2);

        assertTrue(regel.overleeft(buurtOpname));
    }

    @Test
    void celSterftDoorOnderbevolking() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.CONWAY, 1), 1);

        assertFalse(regel.overleeft(buurtOpname));
    }

    @Test
    void celSterftDoorOverbevolking() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.CONWAY, 4), 4);

        assertFalse(regel.overleeft(buurtOpname));
    }
}
