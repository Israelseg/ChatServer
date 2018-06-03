/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import com.example.hp.groupchat.shared.ServerUtils;
import google.places.usage.PlacesSearch;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import officePlaces.PlaceOffices;

/**
 *
 * @author hp
 */
public class ServerManagerThread implements Runnable {

    private final ChatServer chatServer;
    private ArrayList<Message> pds;
    private Message packData;
    private MessageAnalyzer messageAnalyzer;
    private final String pathDirectory = "C:\\Users\\Andrés\\Documents\\NetBeansProjects\\ChatServer\\src\\imgUsers";

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
                                Message msgMessage = new Message(
                                        KeyWordSystem.BOT_NAME,
                                        KeyWordSystem.TYPE_QUERY_RESULT, 
                                        messageAction[1]);
                                
                                chatServer.broadcastInfo(msgMessage);
                                break;
                                
                            case MessageAnalyzer.TYPE_ENTERTAINMENT:
                                String text="Escriba la opción que requiera para buscar:";
                                
                                for(String s:MessageAnalyzer.OPTIONS_ENTERTAINMENT)
                                    text+="\n\t·"+s;
                                
                                Message responseMessage = new Message(
                                        KeyWordSystem.BOT_NAME,
                                        KeyWordSystem.TYPE_TEXT,
                                        text);          
                                
                                chatServer.broadcastInfo(responseMessage);
                                break;
                            case MessageAnalyzer.TYPE_TAG_ENTERTAINMENT:
                                new PlacesSearch(
                                        messageAction[1],
                                        chatServer.getClients().get(packData.getFrom())).start();
                                break;            
                                
                            case MessageAnalyzer.TYPE_BANKS:
                                String bankInstructionsMessage = "Estas son las opciones de busqueda:";
                                
                                for (String bankOption : MessageAnalyzer.BANKS_OPTIONS) 
                                    bankInstructionsMessage += "\n\t·" + bankOption;
                                
                                Message banksResponseMessage = new Message(
                                        KeyWordSystem.BOT_NAME, 
                                        KeyWordSystem.TYPE_TEXT, 
                                        bankInstructionsMessage);
                                
                                chatServer.broadcastInfo(banksResponseMessage);
                                break;
                            case MessageAnalyzer.TYPE_TAG_BANKS:
                                new PlacesSearch(
                                        messageAction[1], 
                                        chatServer.getClients().get(packData.getFrom())).start();
                                break;
                                
                            case MessageAnalyzer.TYPE_ATM:
                                String atmInstructionsMessage = "Estas son las opciones de busqueda:";
                                
                                for (String atmOption : MessageAnalyzer.ATM_OPTIONS) 
                                    atmInstructionsMessage += "\n\t·" +atmOption;
                                
                                Message atmResponseMessage = new Message(
                                        KeyWordSystem.BOT_NAME, 
                                        KeyWordSystem.TYPE_TEXT, 
                                        atmInstructionsMessage);
                               
                                chatServer.broadcastInfo(atmResponseMessage);
                                
                            case MessageAnalyzer.TYPE_TAG_ATM:
                                new PlacesSearch(
                                        messageAction[1], 
                                        chatServer.getClients().get(packData.getFrom())).start();
                                break;    
                                
                            case MessageAnalyzer.TYPE_OFICINAS:
                                String text2="Escriba la opción que requiera para buscar:";
                                
                                for(String s:MessageAnalyzer.OPTIONS_OFICINAS)
                                    text2+="\n\t·"+s;
                                
                                Message responseMessage2 = new Message(
                                        KeyWordSystem.BOT_NAME, 
                                        KeyWordSystem.TYPE_TEXT, 
                                        text2);
                                
                                chatServer.broadcastInfo(responseMessage2);
                                break;
                            case MessageAnalyzer.TYPE_TAG_OFICINAS:
                                Message responseMessage3 = new Message(
                                        KeyWordSystem.BOT_NAME, 
                                        KeyWordSystem.TYPE_TEXT, 
                                        messageAction[1]);
                                //chatServer.broadcastInfo(responseMessage3);
                                new PlaceOffices(
                                        messageAction[1],
                                        chatServer.getClients().get(packData.getFrom())).start();
                                break;
                                
                            case MessageAnalyzer.NOTHING:
                                
                            default:
                                Message responseServer = new Message(
                                        KeyWordSystem.BOT_NAME,
                                        KeyWordSystem.TYPE_QUERY_RESULT,
                                        "Lo siento, no puedo procesar tu consulta");
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
