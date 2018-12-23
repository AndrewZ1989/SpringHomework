package domainModel;

public enum EventRating {

    LOW(0),

    MID(1),

    HIGH(2);

    private final int value;
    EventRating(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}

