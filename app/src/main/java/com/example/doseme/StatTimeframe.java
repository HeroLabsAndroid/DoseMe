package com.example.doseme;

public enum StatTimeframe {
    WEEKLY, MONTHLY, YEARLY;

    public static StatTimeframe fromOrdinal(int ord) {
        switch (ord) {
            case 0:
                return WEEKLY;
            case 1:
                return MONTHLY;
            case 2:
                return YEARLY;
            default:
                return null;
        }
    }

    public int toOrdinal() {
        switch (this) {
            case WEEKLY:
                return 0;
            case MONTHLY:
                return 1;
            case YEARLY:
                return 2;
            default:
                return -1;
        }
    }

    public StatTimeframe cycle() {
        switch (this) {
            case WEEKLY:
                return MONTHLY;
            case MONTHLY:
                return YEARLY;
            case YEARLY:
                return WEEKLY;
            default:
                return null;
        }
    }
}
