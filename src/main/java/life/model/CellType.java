package life.model;

public enum CellType {
    CONWAY("Conway"),
    ALTERNATIVE("Alternative");

    private final String displayName;

    CellType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
