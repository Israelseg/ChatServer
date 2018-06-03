package officePlaces;

import chatserver.ChatServerThread;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlaceOffices extends Thread {

    private final String order;
    private final ChatServerThread user;

    public PlaceOffices(String order, ChatServerThread userThread) {
        this.order = order;
        this.user = userThread;
    }

    @Override
    public void run() {
        try {
            String oficinas = null;
            System.out.println(order);
            if (order.equals("TODASLASOFICINAS ")) {
                oficinas = "C:\\Users\\Andrés\\Documents\\Andrés\\ITCM\\VIII Semestre\\Topicos Selectos de Supercomputo\\Oficinas.json";
            } else {
                double lat = 22.25475, lng = -97.84932;
                JSONObject geometry, locationes, results, loc;
                JSONArray resultados = new JSONArray();

                locationes = new JSONObject();
                loc = new JSONObject();
                geometry = new JSONObject();
                results = new JSONObject();

                locationes.put("lat", lat);
                locationes.put("lng", lng);

                loc.put("location", locationes);

                geometry.put("geometry", loc);
                geometry.put("name", order);

                resultados.put(0, geometry);

                results.put("results", resultados);

                File archivoTemporal = File.createTempFile("Oficina" + order + user, ".json");
                archivoTemporal.deleteOnExit();
                oficinas = archivoTemporal.getAbsolutePath().replace("\\", "/");

                FileWriter fileWriter = new FileWriter(oficinas);
                BufferedWriter data = new BufferedWriter(fileWriter);
                data.write(results.toString());

                data.close();
                fileWriter.close();
            }

            System.out.println(oficinas);

            JSONArray searchByType = PlaceServices2.searchOffice(oficinas);
            Message response;
            //System.out.println(response.toString());
            response = new Message(KeyWordSystem.BOT_NAME, KeyWordSystem.TYPE_MAP, "Se han encontrado " + searchByType.length() + " lugare(s).");
            response.setJsonString(searchByType.toString());
            response.setContent(PlaceServices2.staticMap(oficinas));
            try {
                user.send(response);
            } catch (Throwable ex) {
                Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
