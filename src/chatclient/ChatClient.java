/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author hp
 */
import com.example.hp.groupchat.shared.KeyWordSystem;
import static com.example.hp.groupchat.shared.KeyWordSystem.Close_Connection;
import com.example.hp.groupchat.shared.PackData;
import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;

public class ChatClient extends Applet {

    private Socket socket = null;

    private ObjectInputStream console = null;
    private ObjectOutputStream streamOut = null;
    private ChatClientThread client = null;
    private TextArea display = new TextArea();
    private TextField input = new TextField();
    private Button send = new Button("Send"), connect = new Button("Connect"),
            quit = new Button("Bye");
    private String serverName = "localhost";
    private int serverPort = 10001;
    private String user;
    private SocketAddress localSocketAddress;

    public void init() {
        user = "User-" + System.currentTimeMillis();
        Panel keys = new Panel();
        keys.setLayout(new GridLayout(1, 2));
        keys.add(quit);
        keys.add(connect);
        Panel south = new Panel();
        south.setLayout(new BorderLayout());
        south.add("West", keys);
        south.add("Center", input);
        south.add("East", send);
        Label title = new Label("Simple Chat Client Applet", Label.CENTER);
        title.setFont(new Font("Helvetica", Font.BOLD, 14));
        setLayout(new BorderLayout());
        add("North", title);
        add("Center", display);
        add("South", south);
        quit.disable();
        send.disable();
        getParameters();
    }

    public boolean action(Event e, Object o) {
        if (e.target == quit) {
            input.setText(KeyWordSystem.Close_Connection);
            send();
            quit.disable();
            send.disable();
            connect.enable();
        } else if (e.target == connect) {
            connect(serverName, serverPort);
        } else if (e.target == send) {
            send();
            input.requestFocus();
        }
        return true;
    }

    public void connect(String serverName, int serverPort) {
        println("Establishing connection. Please wait ...");
        try {
            socket = new Socket(serverName, serverPort);
            localSocketAddress = socket.getLocalSocketAddress();
            println("Connected: " + socket);
            open();
            send.enable();
            connect.disable();
            quit.enable();
        } catch (UnknownHostException uhe) {
            println("Host unknown: " + uhe.getMessage());
        } catch (IOException ioe) {
            println("Unexpected exception: " + ioe.getMessage());
        }
    }

    private void send() {
        try {
            String txt = "";
            if (input.getText().contains(Close_Connection)) {
                txt = "Bye... ";
            }

            PackData pack = new PackData(user, (!txt.isEmpty()) ? KeyWordSystem.Close_Connection : KeyWordSystem.Text_Only, input.getText());
          //  streamOut = new ObjectOutputStream(socket.getOutputStream());
            streamOut.writeObject(pack);
            //streamOut.flush();
            println((!txt.isEmpty()) ? txt : input.getText());
            input.setText("");
        } catch (IOException ioe) {
            println("Sending error: " + ioe.getMessage());
            close();
        }
    }

    public void handle(PackData msg) {
        if (msg.getType().equals(KeyWordSystem.Close_Connection)) {
            println("Good bye. Press RETURN to exit ...");
            close();
        } else {
            String txt = msg.getTime() + " - " + msg.getFrom() + ": " + msg.getText();
            println(txt);
        }
    }

    public void open() {
        try {
            streamOut = new ObjectOutputStream(socket.getOutputStream());
            PackData pack = new PackData(user, KeyWordSystem.Connected, user + KeyWordSystem.UserConnected);

            streamOut.writeObject(pack);
            client = new ChatClientThread(this, socket);
            
        } catch (IOException ioe) {
            println("Error opening output stream: " + ioe);
        }
    }

    public void close() {
        try {
            if (streamOut != null) {
                streamOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ioe) {
            println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    private void println(String msg) {

        display.append(msg + "\n");
    }

    public void getParameters() {
        serverName = serverName;
        serverPort = serverPort;
    }
}
