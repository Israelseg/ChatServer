/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.PackData;
import com.example.hp.groupchat.shared.ServerUtils;
import com.sun.xml.internal.messaging.saaj.util.Base64;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author hp
 */
public class ServerManagerThread implements Runnable {

    private final ChatServer chatServer;
    private ArrayList<PackData> pds;
    private PackData packData;
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
                if (packData.getType().equals(KeyWordSystem.File_Transfer)) {
                    new Thread(new saveFileRunnable(packData)).start();
                } else {
                    messageAnalyzer = new MessageAnalyzer(packData.getText());
                    /* 
                    Switch: action to take
                    default nothing
                     */
                    String actionString = messageAnalyzer.getAction();
                    if (!actionString.equals(messageAnalyzer.Nothing)) {
                        System.out.println(ServerUtils.dateLog() + " " + actionString);
                        PackData responseServer = new PackData(KeyWordSystem._Bot, KeyWordSystem.Response, actionString);
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

        private final PackData pd;

        public saveFileRunnable(PackData data) {
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
