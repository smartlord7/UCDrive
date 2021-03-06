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

package protocol.failover.redundancy;

import util.Const;
import util.Hasher;
import util.StringUtil;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Class that holds the data about a failover object.
 */

public class FailoverData implements Serializable {

    // region Private properties

    private int id;
    private int size;
    private int totalSize;
    private String name;
    private byte[] checksum;
    private byte[] content;
    private FailoverDataTypeEnum type;

    // endregion Private properties

    // region Constructors

    /**
     * Constructor method.
     * @param id is the data id.
     * @param size is the partial size being sent.
     * @param totalSize is the total size to be sent.
     * @param name is the name of the packet.
     * @param checksum is the checksum array.
     * @param content is the content that is being sent.
     * @param type is the data type.
     */
    public FailoverData(int id, int size, int totalSize, String name, byte[] checksum, byte[] content, FailoverDataTypeEnum type) {
        this.id = id;
        this.size = size;
        this.totalSize = totalSize;
        this.name = name;
        this.checksum = checksum;
        this.content = content;
        this.type = type;
    }

    // endregion Constructors

    // region Public methods

    /**
     * Method to verify the check sum.
     * @return if the check sum is equal or not.
     * @throws NoSuchAlgorithmException - when a particular cryptographic algorithm is requested but is not available in the environment.
     */
    public boolean verifyChecksum() throws NoSuchAlgorithmException {
        return Arrays.equals(this.checksum, Hasher.hashBytes(StringUtil.bytesToHex(this.content), Const.FILE_CONTENT_CHECKSUM_ALGORITHM));
    }

    /**
     * To String method.
     * @return the string to print the failover data.
     */
    @Override
    public String toString() {
        return new StringJoiner(", ", FailoverData.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("size=" + size)
                .add("totalSize=" + totalSize)
                .add("name='" + name + "'")
                .add("checksum=" + Arrays.toString(checksum))
                .add("type=" + type)
                .toString();
    }

    // endregion Public methods

    // region Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public FailoverDataTypeEnum getType() {
        return type;
    }

    public void setType(FailoverDataTypeEnum type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    // endregion Getters and Setters

}
