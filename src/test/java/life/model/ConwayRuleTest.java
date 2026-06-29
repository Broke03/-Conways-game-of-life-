package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConwayRuleTest {
    private final ConwayRule rule = new ConwayRule();

    @Test
    void birthOccursWithExactlyThreeConwayNeighbors() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.CONWAY, 3), 3);

        assertTrue(rule.isBorn(snapshot));
    }

    @Test
    void cellSurvivesWithTwoNeighbors() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.CONWAY, 2), 2);

        assertTrue(rule.survives(snapshot));
    }

    @Test
    void cellDiesFromUnderpopulation() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.CONWAY, 1), 1);

        assertFalse(rule.survives(snapshot));
    }

    @Test
    void cellDiesFromOverpopulation() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(Map.of(CellType.CONWAY, 4), 4);

        assertFalse(rule.survives(snapshot));
    }
}
