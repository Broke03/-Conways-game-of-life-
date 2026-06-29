package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlternativeRuleTest {
    private final AlternativeRule rule = new AlternativeRule();

    @Test
    void birthOccursWithExactlyFourAlternativeNeighbors() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.ALTERNATIVE, 4), 4);

        assertTrue(rule.isBorn(snapshot));
    }

    @Test
    void cellSurvivesWithFourNeighbors() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.ALTERNATIVE, 4), 4);

        assertTrue(rule.survives(snapshot));
    }

    @Test
    void cellDiesWhenItHasTooFewNeighbors() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.ALTERNATIVE, 1), 1);

        assertFalse(rule.survives(snapshot));
    }
}
