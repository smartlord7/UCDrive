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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class that has the hashing methods.
 */

public class Hasher {

    // region Public methods

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

    // endregion Public methods

}
