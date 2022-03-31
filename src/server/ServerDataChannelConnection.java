package server;

import protocol.RequestMethodEnum;
import protocol.Response;
import protocol.ResponseStatusEnum;
import util.Const;
import util.FileMetadata;
import util.FileUtil;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static sun.nio.ch.IOStatus.EOF;

class ServerDataChannelConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private int number;
    private final ServerUserSession session;

    public ServerDataChannelConnection(Socket aClientSocket, int number, ServerUserSession session) {
        this.number = number;
        this.session = session;
        try {
            clientSocket = aClientSocket;
            in = new ObjectInputStream(new DataInputStream(clientSocket.getInputStream()));
            out = new ObjectOutputStream(new DataOutputStream(clientSocket.getOutputStream()));
            out.flush();
            out.reset();
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
                out.flush();
                out.reset();
                session.getSyncObj().setActive(false);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileByChunks() throws IOException {
        int fileSize;
        int readSize;
        byte[] buffer;
        String fileToSend;
        Path p;
        DataInputStream fileReader;

        fileToSend = session.getCurrentDir() + "\\" + session.getFileMetadata().getFileName();
        fileReader = new DataInputStream(new FileInputStream(fileToSend));
        p = Paths.get(fileToSend);
        fileSize = (int) Files.size(p);

        buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];
        readSize = Math.min(fileSize, Const.UPLOAD_FILE_CHUNK_SIZE);

        while (fileReader.read(buffer, 0, readSize) != -1) {
            out.write(buffer);
        }

        fileReader.close();
    }

    private void receiveFileByChunks() throws IOException {
        int totalRead = 0;
        int bytesRead;
        int fileSize = 0;
        byte[] buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];
        FileMetadata fileMeta = null;
        FileOutputStream fileWriter = null;

        bytesRead = 0;
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

            if (bytesRead > fileMeta.getFileSize()) {
                finalBuffer = FileUtil.substring(buffer, 0, fileSize);
            }

            totalRead += bytesRead;
            fileWriter.write(finalBuffer);

            if (totalRead >= fileSize) {
                fileWriter.close();
                return;
            }
        }
    }
}
