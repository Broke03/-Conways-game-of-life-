package life.model;

public final class ConwayRegel implements CelRegel {
    @Override
    public CelSoort getCelSoort() {
        return CelSoort.CONWAY;
    }

    @Override
    public boolean overleeft(BuurtOpname buurtOpname) {
        int totaalBuren = buurtOpname.totaalBuren();
        return totaalBuren == 2 || totaalBuren == 3;
    }

    @Override
    public boolean wordtGeboren(BuurtOpname buurtOpname) {
        return buurtOpname.zelfdeTypeBuren(CelSoort.CONWAY) == 3;
    }
}
