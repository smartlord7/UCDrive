package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

class ClientDataConnection extends Thread {
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    int thread_number;

    public ClientDataConnection(Socket aClientSocket, int number) {
        thread_number = number;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }
    public void run(){
        String resposta;
        try {
            while(true){
                //an echo server
                String data = in.readUTF();
                System.out.println("T[" + thread_number + "] Recebeu: "+data);
                resposta=data.toUpperCase();
                out.writeUTF(resposta);
            }
        } catch(EOFException e) {
            System.out.println("EOF:" + e);
        } catch(IOException e) {
            System.out.println("IO:" + e);
        }
    }
}
