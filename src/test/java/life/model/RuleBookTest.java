package life.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleBookTest {
    private final RuleBook ruleBook = RuleBook.createDefault();

    @Test
    void returnsCorrectRuleForCellType() {
        assertEquals(CellType.CONWAY, ruleBook.getRule(CellType.CONWAY).getCellType());
        assertEquals(CellType.ALTERNATIVE, ruleBook.getRule(CellType.ALTERNATIVE).getCellType());
    }

    @Test
    void prefersAlternativeBirthWhenBothBirthRulesMatch() {
        NeighborhoodSnapshot snapshot = new NeighborhoodSnapshot(
                Map.of(
                        CellType.CONWAY, 3,
                        CellType.ALTERNATIVE, 4
                ),
                7
        );

        assertTrue(ruleBook.determineBirthType(snapshot).isPresent());
        assertEquals(CellType.ALTERNATIVE, ruleBook.determineBirthType(snapshot).orElseThrow());
    }
}
