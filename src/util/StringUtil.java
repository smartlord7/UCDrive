package util;

public class StringUtil {
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

    public static String repeat(String str, int times) {
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < times; i++) {
            sb.append(str);
        }

        return sb.toString();
    }
}
