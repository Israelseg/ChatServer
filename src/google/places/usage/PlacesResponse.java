/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.places.usage;

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
public class PlacesResponse extends Thread {

    private final String order;
    private final ChatServerThread user;

    public PlacesResponse(String order, ChatServerThread userThread) {
        this.order = order;
        this.user = userThread;
    }

    @Override
    public void run() {
        double lat = 22.307234, lng = -97.888767;
        int radius = 2000;
        /*
        ITCM coordenates
        double itcmLatitude = 22.256737;
        double itcmLongitude = -97.847265;      
         */

        if (user.getLocation() != null) {
            String[] split = user.getLocation().split(",");
            lat = Double.parseDouble(split[0]);
            lng = Double.parseDouble(split[1]);
        }

        JSONArray jsonArray = PlaceService.searchByType(order.toLowerCase().trim(), lat, lng, radius, false);
        JSONArray jarray = new JSONArray();
        Message response;
        String id;
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                id = jsonArray.getJSONObject(i).getString("place_id");
                jarray.put(PlaceService.placeDetails(id));
            }
            response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_MAP, "Se han encontrado "+jarray.length()+" lugare(s).");
            response.setJsonString(jarray.toString());
            response.setContent(PlaceService.staticMap(lat, lng, 14));

        }else{
            response=new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_TEXT,"No se encontraron lugares cercanos a tu ubicación" );
            
        }

        try {
            user.send(response);
        } catch (Throwable ex) {
            Logger.getLogger(PlacesResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
