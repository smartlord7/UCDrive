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

package server.threads.connections;

import businesslayer.Exception.ExceptionDAO;
import businesslayer.base.DAOResult;
import datalayer.enumerate.FileOperationEnum;
import datalayer.model.Exception.Exception;
import protocol.failover.redundancy.FailoverData;
import protocol.failover.redundancy.FailoverDataTypeEnum;
import server.struct.ServerUserSession;
import util.FailoverDataHelper;
import util.Const;
import util.FileMetadata;
import util.FileUtil;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static sun.nio.ch.IOStatus.EOF;

/**
 * Class that has the most methods to handle the server data channel connection.
 */

public class ServerDataChannelConnection extends Thread {

    // region Private properties

    private final int connectionId;
    private final ServerUserSession session;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket clientSocket;

    // endregion Private properties

    // region Private methods

    /**
     * Method used to send the selected upload file by chunks.
     @throws IOException - whenever an input or output operation is failed or interrupted.
     */
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

        readSize = Math.min(fileSize, Const.DOWNLOAD_FILE_CHUNK_SIZE);
        buffer = new byte[readSize];
        while ((read = fileReader.read(buffer, 0, readSize)) != -1) {
            out.write(buffer);
            out.flush();
        }

        fileReader.close();
    }

    /**
     * Method used to receive the selected download file by chunks.
     @throws IOException - whenever an input or output operation is failed or interrupted.
     */
    private void receiveFileByChunks() throws IOException {
        int totalRead;
        int bytesRead;
        int fileSize;
        int counter;
        byte[] buffer = new byte[Const.UPLOAD_FILE_CHUNK_SIZE];
        String bufferStr;
        String filePath;
        FileMetadata fileMeta = null;
        FileOutputStream fileWriter = null;

        bytesRead = 0;
        counter = 0;
        fileSize = 0;
        totalRead = 0;

        while ((bytesRead = in.read(buffer,0, Const.DOWNLOAD_FILE_CHUNK_SIZE)) != EOF)
        {
            if (fileMeta == null) {
                fileMeta = session.getFileMetadata();
                if (fileMeta == null || fileMeta.getFileSize() == 0) {
                    continue;
                }

                fileSize = fileMeta.getFileSize();
                filePath = session.getCurrentDir() + "\\" + fileMeta.getFileName();
                fileWriter = new FileOutputStream(filePath);
            }

            byte[] finalBuffer = buffer;

            if (bytesRead > fileSize) {
                finalBuffer = FileUtil.substring(buffer, 0, fileSize);
            }

            totalRead += bytesRead;
            fileWriter.write(finalBuffer);
            session.getDataToSync().add(new FailoverData(counter, finalBuffer.length, fileSize, session.getCurrentDir() + "\\" + fileMeta.getFileName(), null, finalBuffer.clone(), FailoverDataTypeEnum.FILE));

            if (totalRead >= fileSize) {
                fileWriter.close();
                return;
            }

            counter++;
        }
    }

    /**
     * Method used to print and log the exception into the database.
     * @param e is the exception.
     */
    private void logException(java.lang.Exception e) {
        DAOResult result = null;
        try {
            result = ExceptionDAO.create(new Exception(e, session.getUserId(),
                    "DATA CHANNEL @" + clientSocket.getLocalSocketAddress(),
                    clientSocket.getInetAddress().toString()));
        } catch (SQLException | NoSuchMethodException ex) {
            System.out.println("Error: could not log exception.");
            ex.printStackTrace();
        }

        try {
            FailoverDataHelper.sendDMLFailoverData(session, result);
        } catch (IOException ex) {
            System.out.println("Error: Exception failed to be sent to secondary server.");
            ex.printStackTrace();
        }
    }

    // endregion Private methods

    // region Constructors

    /**
     * Constructor method.
     * @param aClientSocket is the client socket.
     * @param connectionId is the connection Id.
     * @param session is the current session.
     */
    public ServerDataChannelConnection(Socket aClientSocket, int connectionId, ServerUserSession session) {
        this.connectionId = connectionId;
        this.session = session;
        this.session.setUserId(-1);
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            logException(e);
        }
    }

    // endregion Constructors

    // region Public methods

    /**
     * Method that awaits the command thread notification in order to perform an upload/download operation.
     */
    @Override
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
            } catch (java.lang.Exception e) {
                Class<? extends java.lang.Exception> eClass = e.getClass();
                logException(e);

                if (eClass == SocketException.class || eClass == EOFException.class) {
                    return;
                }
            }
        }
    }

    // endregion Public methods

}
