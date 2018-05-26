/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import com.example.hp.groupchat.shared.ServerUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entertainmentPlaces.PlaceEntertainment;
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
                switch (packData.getType()) {
                    case KeyWordSystem.TYPE_IMG:
                        new Thread(new saveFileRunnable(packData)).start();
                        break;
                    case KeyWordSystem.TYPE_TEXT: {
                        messageAnalyzer = new MessageAnalyzer(packData.getMsg());
                        String[] messageAction = messageAnalyzer.getAction();

                        switch (messageAction[0]) {

                            case MessageAnalyzer.TYPE_LOCATE:
                            case MessageAnalyzer.TYPE_LOCATE_ITCM:
                                Message msgMessage = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_QUERY_RESULT, messageAction[1]);
                                chatServer.broadcastInfo(msgMessage);
                                break;
                            case MessageAnalyzer.TYPE_ENTERTAINMENT:
                                String text="Escriba la opción que requiera para buscar:";
                                for(String s:MessageAnalyzer.OPTIONS_ENTERTAINMENT){
                                    text+="\n\t·"+s;
                                }
                                Message responseMessage = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_TEXT, text);
                                
                                chatServer.broadcastInfo(responseMessage);
                                break;
                            case MessageAnalyzer.TYPE_TAG_ENTERTAINMENT:
                                new PlaceEntertainment(messageAction[1],chatServer.getClients().get(packData.getFrom())).start();
                                break;
                            case MessageAnalyzer.NOTHING:
                            default:
                                Message responseServer = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_QUERY_RESULT, "Lo siento, no puedo procesar tu consulta");
                                chatServer.broadcastInfo(responseServer);
                                break;
                        }
                    }
                    break;
                    case KeyWordSystem.TYPE_LOCATION:
                        System.out.println(packData);
                        break;
                    default:
                        System.out.println(packData);
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
