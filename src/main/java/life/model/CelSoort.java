package life.model;

public enum CelSoort {
    CONWAY("Conway"),
    ALTERNATIEF("Alternatief");

    private final String weergavenaam;

    CelSoort(String weergavenaam) {
        this.weergavenaam = weergavenaam;
    }

    public String getWeergavenaam() {
        return weergavenaam;
    }
}
