package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    public static String hashString(String string, String alg) throws NoSuchAlgorithmException {
        return StringUtil.bytesToHex(hashBytes(string, alg));
    }

    public static byte[] hashBytes(String string, String alg) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(alg);
        return digest.digest(string.getBytes(StandardCharsets.UTF_8));
    }
}
