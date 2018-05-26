/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entertainmentPlaces;

import chatserver.ChatServerThread;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;

/**
 *
 * @author hp
 */
public class PlaceEntertainment extends Thread {

    private final String order;
    private final ChatServerThread user;

    public PlaceEntertainment(String order, ChatServerThread userThread) {
        this.order = order;
        this.user = userThread;
    }

    @Override
    public void run() {
        double lat = 22.307234, lng = -97.888767;
        int radius = 2000;
        if (user.getLocation() != null) {
            String[] split = user.getLocation().split(",");
            lat = Double.parseDouble(split[0]);
            lng = Double.parseDouble(split[1]);
        }
        JSONArray searchByType = PlaceService.searchByType(order.toLowerCase().trim(), lat, lng,  radius);
        System.out.println(searchByType.length());
        Message response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_MAP, searchByType.toString());
        response.setContent(PlaceService.staticMap(lat, lng, 14));
        try {
            user.send(response);
        } catch (Throwable ex) {
            Logger.getLogger(PlaceEntertainment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
