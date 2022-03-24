package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hasher {
    public static String hash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(string.getBytes(StandardCharsets.UTF_8));

        return StringUtil.bytesToHex(hash);
    }
}
