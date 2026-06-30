package life.model;

public final class AlternatieveRegel implements CelRegel {
    @Override
    public CelSoort getCelSoort() {
        return CelSoort.ALTERNATIEF;
    }

    @Override
    public boolean overleeft(BuurtOpname buurtOpname) {
        int totaalBuren = buurtOpname.totaalBuren();
        return totaalBuren >= 2 && totaalBuren <= 4;
    }

    @Override
    public boolean wordtGeboren(BuurtOpname buurtOpname) {
        return buurtOpname.zelfdeTypeBuren(CelSoort.ALTERNATIEF) == 4;
    }
}
