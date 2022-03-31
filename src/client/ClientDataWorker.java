package client;

import util.Const;
import util.FileMetadata;
import util.FileUtil;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static sun.nio.ch.IOStatus.EOF;

public class ClientDataWorker implements Runnable {

    private FileMetadata fileMetadata;
    private String currLocalDir;
    private DataInputStream in;

    public ClientDataWorker(DataInputStream in, FileMetadata fileMetadata, String currLocalDir) {
        this.in = in;
        this.fileMetadata = fileMetadata;
        this.currLocalDir = currLocalDir;
    }

    @Override
    public void run() {
        try {
            readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() throws IOException {
        int totalRead = 0;
        int bytesRead;
        int readSize;
        FileOutputStream fileWriter;

        int fileSize = fileMetadata.getFileSize();
        readSize = Math.min(fileSize, Const.UPLOAD_FILE_CHUNK_SIZE);
        byte[] buffer = new byte[readSize];
        fileWriter = new FileOutputStream(currLocalDir + "\\" + fileMetadata.getFileName());

        while ((bytesRead = in.read(buffer,0, readSize)) != EOF)
        {
            byte[] finalBuffer = buffer;

            if (bytesRead > fileMetadata.getFileSize()) {
                finalBuffer = FileUtil.substring(buffer, 0, fileSize);
            }

            totalRead += bytesRead;
            fileWriter.write(finalBuffer);

            if (totalRead >= fileSize) {
                fileWriter.close();
                System.out.println("File '" + fileMetadata.getFileName() + "' downloaded");
                return;
            }
        }
    }
}
