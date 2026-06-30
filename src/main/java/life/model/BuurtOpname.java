package life.model;

import java.util.EnumMap;
import java.util.Map;

public final class BuurtOpname {
    private final Map<CelSoort, Integer> zelfdeTypeTellingen;
    private final int totaalBurenAantal;

    public BuurtOpname(Map<CelSoort, Integer> zelfdeTypeTellingen, int totaalBurenAantal) {
        this.zelfdeTypeTellingen = new EnumMap<>(CelSoort.class);
        this.zelfdeTypeTellingen.putAll(zelfdeTypeTellingen);
        this.totaalBurenAantal = totaalBurenAantal;
    }

    public int zelfdeTypeBuren(CelSoort celSoort) {
        return zelfdeTypeTellingen.getOrDefault(celSoort, 0);
    }

    public int totaalBuren() {
        return totaalBurenAantal;
    }
}
