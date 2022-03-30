package server;

import util.Const;
import util.FileMetadata;
import util.FileUtil;

import java.io.*;
import java.net.Socket;

import static sun.nio.ch.IOStatus.EOF;

class ClientDataConnection extends Thread {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket clientSocket;
    private int number;
    private final UserSession session;

    public ClientDataConnection(Socket aClientSocket, int number, UserSession session) {
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
        try {
           receiveFileByChunks(in, session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void receiveFileByChunks(InputStream in, UserSession session) throws IOException {
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
                fileMeta = null;
            }
        }
    }
}
