package lk.ijse.groupchatapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created By shamodha_s_rathnamalala
 * Date : 5/21/2023
 * Time :10:51 PM
 */

public class ClientHandler implements Runnable{
    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private String clientUserName;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            this.dataInputStream = new DataInputStream(socket.getInputStream());


            this.clientUserName = dataInputStream.readUTF();
            clientHandlers.add(this);
            broadCastTextMessage(this, "server : "+clientUserName+" has entered tha chat!");
        } catch (IOException e) {
            e.printStackTrace();
            closeEverything(socket, dataOutputStream, dataInputStream);
        }
    }


    @Override
    public void run() {
        incomingMessage();
    }

    private void incomingMessage() {
        while (socket.isConnected()){
            try {
//                String message = dataInputStream.readUTF();
                if (dataInputStream.readBoolean()){
                    broadCastImage(this);
                }else {
                    broadCastTextMessage(this, dataInputStream.readUTF());
                }
            }catch (IOException e){
                // closed
                e.printStackTrace();
                closeEverything(socket, dataOutputStream, dataInputStream);
                break;
            }
        }
    }

    private void broadCastImage(ClientHandler client) {
        for (ClientHandler clientHandler: clientHandlers){
            try {
                if (client != clientHandler){
                    String utf = clientHandler.dataInputStream.readUTF();
                    int size = clientHandler.dataInputStream.readInt();
                    byte[] bytes = new byte[size];
                    clientHandler.dataInputStream.readFully(bytes);

                    clientHandler.dataOutputStream.writeBoolean(true);
//                    clientHandler.dataOutputStream.writeUTF(utf);
                    clientHandler.dataOutputStream.writeInt(bytes.length);
                    clientHandler.dataOutputStream.write(bytes);
                    clientHandler.dataOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(socket, dataOutputStream, dataInputStream);
            }
        }
    }

    private void broadCastTextMessage(ClientHandler client, String messageToSent) {
        for (ClientHandler clientHandler: clientHandlers){
            try {
                if (client != clientHandler){
                    clientHandler.dataOutputStream.writeBoolean(false);
                    clientHandler.dataOutputStream.writeUTF(messageToSent);
                    clientHandler.dataOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                closeEverything(socket, dataOutputStream, dataInputStream);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        System.out.println("close");
    }

    private void closeEverything(Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
        removeClientHandler();
        try {
            if (socket != null){
                socket.close();
            }
            if (dataOutputStream != null){
                dataOutputStream.close();
            }
            if (dataInputStream != null){
                dataInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
