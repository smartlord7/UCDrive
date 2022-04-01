package util;

public class StringUtil {
    /**
     * Method that converts bytes to hexadecimal.
     * @param bytes are the bytes to be converted
     * @return the converted bytes to hex.
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
}
