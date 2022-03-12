package datalayer.enumerate;

public enum FilePermissionEnum {
    READ, WRITE, READ_WRITE;

    public static FilePermissionEnum toEnum(int value) {
        return switch (value) {
            case 0 -> READ;
            case 1 -> WRITE;
            default -> READ_WRITE;
        };
    }
}