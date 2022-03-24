package datalayer.enumerate;

public enum DirectoryPermissionEnum {
    READ, WRITE, READ_WRITE, NONE;

    public static DirectoryPermissionEnum toEnum(int value) {
        return switch (value) {
            case 0 -> READ;
            case 1 -> WRITE;
            case 2 -> READ_WRITE;
            default -> NONE;
        };
    }
}