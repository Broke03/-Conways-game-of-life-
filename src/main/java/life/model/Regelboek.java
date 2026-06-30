package life.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Regelboek {
    private final Map<CelSoort, CelRegel> regelsPeSoort;

    public Regelboek(Collection<CelRegel> regels) {
        regelsPeSoort = new EnumMap<>(CelSoort.class);
        for (CelRegel regel : regels) {
            regelsPeSoort.put(regel.getCelSoort(), regel);
        }
    }

    public static Regelboek maakStandaard() {
        return new Regelboek(List.of(new ConwayRegel(), new AlternatieveRegel()));
    }

    public CelRegel getRegel(CelSoort celSoort) {
        return regelsPeSoort.get(celSoort);
    }

    public Optional<CelSoort> bepaalGeboorteType(BuurtOpname buurtOpname) {
        return regelsPeSoort.values().stream()
                .filter(regel -> regel.wordtGeboren(buurtOpname))
                .map(regel -> new GeboorteKandidaat(regel.getCelSoort(), buurtOpname.zelfdeTypeBuren(regel.getCelSoort())))
                .max(Comparator.comparingInt(GeboorteKandidaat::zelfdeTypeBurenAantal)
                        .thenComparing(kandidaat -> kandidaat.celSoort() == CelSoort.CONWAY ? 1 : 0))
                .map(GeboorteKandidaat::celSoort);
    }

    private record GeboorteKandidaat(CelSoort celSoort, int zelfdeTypeBurenAantal) {
    }
}
