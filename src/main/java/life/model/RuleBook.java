package life.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RuleBook {
    private final Map<CellType, CellRule> rulesByType;

    public RuleBook(Collection<CellRule> rules) {
        rulesByType = new EnumMap<>(CellType.class);
        for (CellRule rule : rules) {
            rulesByType.put(rule.getCellType(), rule);
        }
    }

    public static RuleBook createDefault() {
        return new RuleBook(List.of(new ConwayRule(), new AlternativeRule()));
    }

    public CellRule getRule(CellType cellType) {
        return rulesByType.get(cellType);
    }

    public Optional<CellType> determineBirthType(NeighborhoodSnapshot snapshot) {
        return rulesByType.values().stream()
                .filter(rule -> rule.isBorn(snapshot))
                .map(rule -> new BirthCandidate(rule.getCellType(), snapshot.sameTypeNeighbors(rule.getCellType())))
                .max(Comparator.comparingInt(BirthCandidate::sameTypeNeighbors)
                        .thenComparing(candidate -> candidate.cellType() == CellType.CONWAY ? 1 : 0))
                .map(BirthCandidate::cellType);
    }

    private record BirthCandidate(CellType cellType, int sameTypeNeighbors) {
    }
}
