package protocol.failover.redundancy;

import util.Const;
import util.Hasher;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.StringJoiner;

public class FailoverData implements Serializable {
    private int id;
    private int size;
    private int totalSize;
    private String name;
    private byte[] checksum;
    private String content;
    private FailoverDataTypeEnum type;

    public FailoverData() {
    }

    public FailoverData(int id, int size, int totalSize, String name, byte[] checksum, String content, FailoverDataTypeEnum type) {
        this.id = id;
        this.size = size;
        this.totalSize = totalSize;
        this.name = name;
        this.checksum = checksum;
        this.content = content;
        this.type = type;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
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

    public boolean verifyChecksum() throws NoSuchAlgorithmException {
        return Arrays.equals(this.checksum, Hasher.hashBytes(this.content, Const.FILE_CONTENT_CHECKSUM_ALGORITHM));
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

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
}
