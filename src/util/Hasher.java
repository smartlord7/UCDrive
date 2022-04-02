package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
    /**
     * Method that encrypts the string.
     * @param string the string to be encrypted.
     * @param alg the encryption algorithm.
     * @return the encrypted string in a string.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static String hashString(String string, String alg) throws NoSuchAlgorithmException {
        return StringUtil.bytesToHex(hashBytes(string, alg));
    }

    /**
     * Method that hashes the bytes.
     * @param string the string which the bytes are to be hashed.
     * @param alg the encryption algorithm.
     * @return the encrypted string in byte array.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public static byte[] hashBytes(String string, String alg) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(alg);
        return digest.digest(string.getBytes(StandardCharsets.UTF_8));
    }
}
