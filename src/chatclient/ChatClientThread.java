/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import com.example.hp.groupchat.shared.PackData;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class ChatClientThread extends Thread {

    private final Socket socket;
    private final ChatClient client;
    private ObjectInputStream streamIn;

    public ChatClientThread(ChatClient _client, Socket _socket) {
        client = _client;
        socket = _socket;
      
        start();
    }

    

    public void close() {
        try {
            if (streamIn != null) {
                streamIn.close();
            }
        } catch (IOException ioe) {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void run() {
        try {
            streamIn = new ObjectInputStream(socket.getInputStream());

        } catch (IOException ioe) {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
        boolean flag=true;
        while (flag) {
            try {
                PackData pack=(PackData) streamIn.readObject();
                client.handle(pack);
                
            } catch (IOException ioe) {
                System.out.println("Listening error: " + ioe.getMessage());
                ioe.printStackTrace();
                client.stop();                
                flag=false;
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ChatClientThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ChatClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
