/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;
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
    private int ID;
    private String userName;
    private ObjectInputStream streamIn;
    private ObjectOutputStream streamOut;
    private boolean statusConnection;

    public ChatServerThread(ChatServer _server, Socket _socket) {
        super();
        server = _server;
        socket = _socket;
       
        this.ID = socket.getPort();
        statusConnection = true;
    }

    public void send(PackData msg) throws Throwable {
        try {
           // streamOut = new ObjectOutputStream(socket.getOutputStream());

            streamOut.writeObject(msg);
            //  streamOut.flush();
            System.out.println(server.getCurrentDate() + " From " + msg.getFrom() + " to " + userName);
        } catch (IOException ioe) {
            System.out.println(server.getCurrentDate() + " " + ID + " ERROR sending: " + ioe.getMessage());
            server.remove(ID);
            stop();
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return userName;
    }

    public void run() {
        System.out.println(server.getCurrentDate() + " Server Thread " + ID + " open socket.");
        try {
            open();
        } catch (IOException ex) {
            System.out.println(server.getCurrentDate() + " " + ID + " ERROR reading nick: " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(server.getCurrentDate() + " Server Thread " + ID + " running.");
        while ( !socket.isClosed()) {
            try {
                PackData pack = (PackData) streamIn.readObject();
                pack.setPort_ID(ID);
                server.handle(pack);
            } catch (IOException ioe) {
                try {
                    System.out.println(server.getCurrentDate() + " " + ID + " ERROR reading: " + ioe.getMessage());
                    
                    closeStreams();
                    //server.remove(ID);
                    
                    System.out.println(server.getCurrentDate() + " " + ID + " is  remove: " + ioe.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(ChatServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (Throwable ex) {
                System.out.println(server.getCurrentDate() + " " + ID + " Error " + ex.getMessage());
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
        PackData pack = (PackData) streamIn.readObject();
        userName = pack.getFrom();
        PackData grettings = new PackData(KeyWordSystem._Bot, KeyWordSystem.Text_Only, KeyWordSystem._Bot + " Welcome " + userName + "!");
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
