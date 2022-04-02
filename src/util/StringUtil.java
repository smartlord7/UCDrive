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

package util;

/**
 * Class that has Util methods of string, mainly converting bytes to hexadecimal.
 */


public class StringUtil {

    // region Public methods

    /**
     * Method that converts bytes to hexadecimal.
     * @param bytes are the bytes to be converted
     * @return the converted bytes to hexadecimal.
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Method that builds the string by appending the parts of it.
     * @param str is the str to be built
     * @param times number of times needed to complete the string
     * @return the built string.
     */
    public static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < times; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static boolean isEmptyOrNull(String str) {
        return str == null || str.length() == 0;
    }

    // endregion Public methods

}
