package ladysnake.masquerade;

public enum Masques {
    NONE(0),
    FOX(1),
    CLOWN(2),
    SPLINTER(3);

    public final int id;

    Masques(int i) {
        this.id = i;
    }

    public static Masques fromID(int id) {
        if (id >= values().length)
            return NONE;
        return values()[id];
    }
}
