package lk.ijse.groupchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created By shamodha_s_rathnamalala
 * Date : 6/8/2023
 * Time : 4:33 PM
 */

public class Server {
    private final ServerSocket serverSocket;

    private Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    private void startServer(){
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }
    private void closeServerSocket(){
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket= new ServerSocket(1235);
        Server server = new Server(serverSocket);
        System.out.println("server started ..!");
        server.startServer();
    }
}
