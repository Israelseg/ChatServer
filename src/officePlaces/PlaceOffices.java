package officePlaces;

import chatserver.ChatServerThread;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import entertainmentPlaces.PlaceEntertainment;
import entertainmentPlaces.PlaceService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceOffices extends Thread{
    
    private final String order;
    private final ChatServerThread user;

    public PlaceOffices(String order, ChatServerThread userThread) {
        this.order = order;
        this.user = userThread;
    }
    
    @Override
    public void run() {
        double lat = 22.25476, lng = -97.8492;
        StringBuilder centrocomputo = new StringBuilder();
            centrocomputo.append(order).append("#").append(lat).append("#").append(lng).append("\n");
       
        Message response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_MAP, centrocomputo.toString());
        System.out.println(response.toString());
        response.setContent(PlaceService.staticMap(lat, lng, 14));
        try {
            user.send(response);
        } catch (Throwable ex) {
            Logger.getLogger(PlaceEntertainment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
