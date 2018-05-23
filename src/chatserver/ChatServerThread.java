/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class ChatServerThread extends Thread {

    private ChatServer server;
    private Socket socket;
    private String userName;
    private ObjectInputStream streamIn;
    private ObjectOutputStream streamOut;
    private boolean statusConnection;

    public ChatServerThread(ChatServer _server, Socket _socket) {
        super();
        server = _server;
        socket = _socket;
       
        statusConnection = true;
    }

    public void send(Message msg) throws Throwable {
        try {
           // streamOut = new ObjectOutputStream(socket.getOutputStream());

            streamOut.writeObject(msg);
            //  streamOut.flush();
            System.out.println(server.getCurrentDate() + " From " + msg.getFrom() + " to " + userName);
        } catch (IOException ioe) {
            System.out.println(server.getCurrentDate() + " " + userName + " ERROR sending: " + ioe.getMessage());
            server.remove(userName);
            stop();
        }
    }

    public String getUserName() {
        return userName;
    }

    public void run() {
        System.out.println(server.getCurrentDate() + " Server Thread " + userName + " open socket.");
        try {
            open();
        } catch (IOException ex) {
            System.out.println(server.getCurrentDate() + " " + userName + " ERROR reading nick: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(server.getCurrentDate() + " Server Thread " + userName + " running.");
        while ( !socket.isClosed()) {
            try {
                Message pack = (Message) streamIn.readObject();
                server.handle(pack);
            } catch (IOException ioe) {
                try {
                    System.out.println(server.getCurrentDate() + " " + userName + " ERROR reading: " + ioe.getMessage());
                    
                    closeStreams();
                    //server.remove(userName);
                    
                    System.out.println(server.getCurrentDate() + " " + userName + " is  remove: " + ioe.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Throwable ex) {
                System.out.println(server.getCurrentDate() + " " + userName + " Error " + ex.getMessage());
            }
        }
    }

    public void inputRead(Socket socket) {
        this.socket = socket;
        this.run();
    }

    private void open() throws IOException, ClassNotFoundException {

        streamIn = new ObjectInputStream((socket.getInputStream()));
        streamOut = new ObjectOutputStream(socket.getOutputStream());
        Message pack = (Message) streamIn.readObject();
        userName = pack.getFrom();
        Message grettings = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_TEXT, KeyWordSystem.BOT_NAME + " Welcome " + userName + "!");
        streamOut.writeObject(grettings);
    }

    private void closeStreams() throws IOException {
        if (socket != null) {
            
            socket.close();
        }
        if (streamIn != null) {
            streamIn.close();
        }
        if (streamOut != null) {
            streamOut.close();
        }
    }

    public boolean getStatusConnection() {
        return this.statusConnection;
    }

    public void setStatusConnection(boolean statusConnection) throws IOException {
        this.statusConnection = statusConnection;
        if (!statusConnection) {
            this.closeStreams();
        }
    }

}
