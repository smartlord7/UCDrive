package datalayer.enumerate;

public enum FilePermissionEnum {
    READ, WRITE, READ_WRITE, NONE;

    public static FilePermissionEnum toEnum(int value) {
        return switch (value) {
            case 0 -> READ;
            case 1 -> WRITE;
            case 2 -> READ_WRITE;
            default -> NONE;
        };
    }
}
