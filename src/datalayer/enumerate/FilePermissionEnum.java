/**------------ucDrive: REPOSITÓRIO DE FICHEIROS NA UC------------
 University of Coimbra
 Degree in Computer Science and Engineering
 Sistemas Distribuidos
 3rd year, 2nd semester
 Authors:
 Sancho Amaral Simões, 2019217590, uc2019217590@student.uc.pt
 Tiago Filipe Santa Ventura, 2019243695, uc2019243695@student.uc.pt
 Coimbra, 2nd April 2022
 ---------------------------------------------------------------------------*/

package datalayer.enumerate;

// region Public enum

/**
 * This enum represents the file permission with the following designations:
 * READ - Read permission only.
 * WRITE - Write permission only.
 * READ_WRITE - Read and Write permission.
 * NONE - No permissions.
 */
public enum FilePermissionEnum {
    READ, WRITE, READ_WRITE, NONE;

    // region Public methods

    /**
     * Method that returns the permission.
     * @param value is the value equivalent to the permission.
     * @return the file permission.
     */
    public static FilePermissionEnum toEnum(int value) {
        return switch (value) {
            case 0 -> READ;
            case 1 -> WRITE;
            case 2 -> READ_WRITE;
            default -> NONE;
        };
    }

    // endregion Public methods

}

// endregion Public enum
