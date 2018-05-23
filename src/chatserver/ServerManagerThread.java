/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import com.example.hp.groupchat.shared.ServerUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hp
 */
public class ServerManagerThread implements Runnable {

    private final ChatServer chatServer;
    private ArrayList<Message> pds;
    private Message packData;
    private MessageAnalyzer messageAnalyzer;
    private final String pathDirectory = "/home/hp/NetBeansProjects/ChatServer/src/imgUsers";

    public ServerManagerThread(ChatServer chatServer) {
        this.chatServer = chatServer;
        this.pds = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println(ServerUtils.dateLog() + " Run ServerManager");
        while (true) {
            try {
                packData = chatServer.getMessageStack().take();
                if (packData.getType()==(KeyWordSystem.TYPE_IMG)) {
                    new Thread(new saveFileRunnable(packData)).start();
                } else {
                    messageAnalyzer = new MessageAnalyzer(packData.getMsg());
                    /* 
                    Switch: action to take
                    default nothing
                     */
                    String actionString = messageAnalyzer.getAction();
                    if (!actionString.equals(messageAnalyzer.Nothing)) {
                        System.out.println(ServerUtils.dateLog() + " " + actionString);
                        Message responseServer = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_QUERY_RESULT, actionString);
                        chatServer.broadcastInfo(responseServer);
                    }

                }
                pds.add(packData);
            } catch (InterruptedException ex) {
                System.out.println(chatServer.getCurrentDate() + " Error save data " + ex.getMessage());
            } catch (Throwable ex) {
                Logger.getLogger(ServerManagerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class saveFileRunnable implements Runnable {

        private final Message pd;

        public saveFileRunnable(Message data) {
            this.pd = data;
        }

        @Override
        public void run() {
            try {
                byte[] b = pd.getContent();
                Path p = Paths.get(pathDirectory + File.separator + "Img-" + pd.getFrom() + "-" + ServerUtils.simpleDate() + ".png");
                Files.write(p, b);
                System.out.println(chatServer.getCurrentDate() + " file create.");

            } catch (IOException ex) {
                Logger.getLogger(ServerManagerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
