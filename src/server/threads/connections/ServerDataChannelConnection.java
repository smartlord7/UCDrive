package server.threads.connections;

import datalayer.enumerate.FileOperationEnum;
import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import server.struct.ServerUserSession;
import util.Const;
import util.FileMetadata;
import util.FileUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import static sun.nio.ch.IOStatus.EOF;

public class ServerDataChannelConnection extends Thread {
    private final int connectionId;
    private final ServerUserSession session;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;
    private final BlockingQueue<FailoverData> dataToSync;

    public ServerDataChannelConnection(Socket aClientSocket, int connectionId, ServerUserSession session, BlockingQueue<FailoverData> dataToSync) {
        this.connectionId = connectionId;
        this.session = session;
        this.dataToSync = dataToSync;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            try {
                session.getSyncObj().wait(false);
                if (session.getFileMetadata().getOp() == FileOperationEnum.DOWNLOAD) {
                    sendFileByChunks();
                } else {
                    receiveFileByChunks();
                }
                session.getSyncObj().setActive(false);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileByChunks() throws IOException {
        int fileSize;
        int readSize;
        int read;
        byte[] buffer;
        String fileToSend;
        Path p;
        DataInputStream fileReader;

        fileToSend = session.getCurrentDir() + "\\" + session.getFileMetadata().getFileName();
        fileReader = new DataInputStream(new FileInputStream(fileToSend));
        p = Paths.get(fileToSend);
        fileSize = (int) Files.size(p);

        readSize = Math.min(fileSize, Const.UPLOAD_FILE_CHUNK_SIZE);
        buffer = new byte[readSize];
        while ((read = fileReader.read(buffer, 0, readSize)) != -1) {
            out.write(buffer);
            out.flush();
        }

        fileReader.close();
    }

    private void receiveFileByChunks() throws IOException {
        int totalRead;
        int bytesRead;
        int fileSize;
        int counter;
        byte[] buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];
        String bufferStr;
        FileMetadata fileMeta = null;
        FileOutputStream fileWriter = null;

        bytesRead = 0;
        counter = 0;
        fileSize = 0;
        totalRead = 0;

        while ((bytesRead = in.read(buffer,0, Const.UPLOAD_FILE_CHUNK_SIZE)) != EOF)
        {
            if (fileMeta == null) {
                fileMeta = session.getFileMetadata();
                if (fileMeta == null || fileMeta.getFileSize() == 0) {
                    continue;
                }

                fileSize = fileMeta.getFileSize();
                fileWriter = new FileOutputStream(session.getCurrentDir() + "\\" + fileMeta.getFileName());
            }

            byte[] finalBuffer = buffer;

            if (bytesRead > fileSize) {
                finalBuffer = FileUtil.substring(buffer, 0, fileSize);
            }

            totalRead += bytesRead;
            fileWriter.write(finalBuffer);
            bufferStr = new String(finalBuffer, StandardCharsets.UTF_8);
            dataToSync.add(new FailoverData(counter, bufferStr.length(), fileSize, session.getCurrentDir() + "\\" + fileMeta.getFileName(), null, bufferStr, FailoverDataTypeEnum.FILE));

            if (totalRead >= fileSize) {
                fileWriter.close();
                return;
            }
        }
    }
}
