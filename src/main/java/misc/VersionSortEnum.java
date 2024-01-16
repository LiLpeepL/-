package misc;

public enum VersionSortEnum {
    DEV("dev", 10),
    X_DEV("x-dev", 11),
    A("a", 20),
    B("b", 30),
    M("m", 35),
    RC("rc", 40),
    OTHER("other", 50),
    POST("post", 60); // Added semicolon here

    private final String label;
    private final int value;

    VersionSortEnum(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
