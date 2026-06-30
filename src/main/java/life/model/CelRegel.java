package life.model;

public interface CelRegel {
    CelSoort getCelSoort();

    boolean overleeft(BuurtOpname buurtOpname);

    boolean wordtGeboren(BuurtOpname buurtOpname);
}
