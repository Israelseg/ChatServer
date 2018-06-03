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
import org.json.JSONObject;

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
        JSONArray jsonArray = PlaceService.searchByType(order.toLowerCase().trim(), lat, lng, radius);

        JSONObject jsonObject;
        JSONObject location;
        String name;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObject = jsonArray.getJSONObject(i);
            name = jsonObject.getString("name");
            location = jsonObject.getJSONObject("geometry").getJSONObject("location");
            lat = location.getDouble("lat");
            lng = location.getDouble("lng");
            builder.append(name).append("#").append(lat).append("#").append(lng).append("\n");
        }
        Message response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_MAP, builder.toString());
        response.setContent(PlaceService.staticMap(lat, lng, 14));
        try {
            user.send(response);
        } catch (Throwable ex) {
            Logger.getLogger(PlaceEntertainment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
