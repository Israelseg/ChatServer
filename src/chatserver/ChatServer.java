/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import com.example.hp.groupchat.shared.ServerUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class ChatServer implements Runnable {

    // private ChatServerThread clients[] = new ChatServerThread[50];
    private ConcurrentHashMap<String, ChatServerThread> clients;
    private LinkedBlockingQueue<Message> messageStack;

    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;

    public ChatServer(int port) {

        try {

            System.out.println(getCurrentDate() + " Binding to port " + port + ", please wait  ...");
            server = new ServerSocket(port);

            System.out.println(getCurrentDate() + " Server started: " + server);
            clients = new ConcurrentHashMap<>();
            messageStack = new LinkedBlockingQueue<>();
            start();
        } catch (IOException ioe) {
            System.out.println(getCurrentDate() + " Can not bind to port " + port + ": " + ioe.getMessage());
        }
    }

    @Override
    public void run() {
        new Thread(new ServerManagerThread(this)).start();
        while (thread != null) {
            try {
                System.out.println(getCurrentDate() + " Waiting for a client ...");
                new ChatServerThread(this, server.accept()).start();
            } catch (IOException ioe) {
                System.out.println(getCurrentDate() + " Server accept error: " + ioe);
                stop();
            }
        }
    }

    /*
        Message is added  to messageStack when it is different to the type close
        For the type text only, is broadcast directly otherwise is process in 
        Server Manager.
     */
    public synchronized void handle(Message pack) throws Throwable {

        if (pack.getType() == (KeyWordSystem.CLOSE_CONNECTION)) {
            Message msg = new Message(clients.get(pack.getFrom()).getUserName(), KeyWordSystem.CLOSE_CONNECTION, KeyWordSystem.BOT_NAME + pack.getFrom() + " " + KeyWordSystem.MSG_DISCONNECTED);
            // clients.get(pack.getID()).getUserName(), KeyWordSystem.Disconnected;
            broadcastInfo(pack);
            clients.get(pack.getFrom()).send(msg);
            remove(pack.getFrom());
        } else if (pack.getType() != KeyWordSystem.TYPE_QUERY || pack.getType() != KeyWordSystem.TYPE_QUERY_RESULT) {
            messageStack.add(pack);
            ChatServerThread from = clients.get(pack.getFrom());
            Iterator<ChatServerThread> iterator = clients.values().iterator();
            while (iterator.hasNext()) {
                ChatServerThread value = iterator.next();
                if (!pack.getFrom().equals(value.getUserName())) {
                    value.send(pack);
                }
            }
        }
    }

    public synchronized void remove(String ID) {

        ChatServerThread toTerminate = clients.get(ID);
        String usr = toTerminate.getUserName();
        System.out.println(getCurrentDate() + " Removing client thread " + toTerminate.getUserName() + " at " + ID);

        clients.remove(ID);

        System.out.println(getCurrentDate() + " Current members " + Arrays.asList(clients.values().toArray()));
    }

    public String getCurrentDate() {

        return ServerUtils.dateLog();
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);

            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
    }

    public void broadcastInfo(Message pack) {
        Iterator<ChatServerThread> iterator = clients.values().iterator();
        while (iterator.hasNext()) {
            try {
                ChatServerThread value = iterator.next();
                value.send(pack);
            } catch (Throwable ex) {
                System.out.println(getCurrentDate() + " Error broadcast thread: " + ex.getMessage());
            }

        }
    }

    public ServerSocket getServer() {
        return server;
    }

    public LinkedBlockingQueue<Message> getMessageStack() {
        return messageStack;
    }

    public void addUser(ChatServerThread value) {
        clients.put(value.getUserName(), value);
    }

    public ConcurrentHashMap<String, ChatServerThread> getClients() {
        return clients;
    }
    

    public static void main(String args[]) {
        ChatServer server = null;

        server = new ChatServer((10001));

    }
}
