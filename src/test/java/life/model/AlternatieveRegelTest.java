package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlternatieveRegelTest {
    private final AlternatieveRegel regel = new AlternatieveRegel();

    @Test
    void geboorteMetExactVierAlternatieveBuren() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.ALTERNATIEF, 4), 4);

        assertTrue(regel.wordtGeboren(buurtOpname));
    }

    @Test
    void celOverleeftMetVierBuren() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.ALTERNATIEF, 4), 4);

        assertTrue(regel.overleeft(buurtOpname));
    }

    @Test
    void celSterftBijTeWeinigBuren() {
        BuurtOpname buurtOpname = new BuurtOpname(Map.of(CelSoort.ALTERNATIEF, 1), 1);

        assertFalse(regel.overleeft(buurtOpname));
    }
}
