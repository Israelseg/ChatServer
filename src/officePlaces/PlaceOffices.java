package officePlaces;

import chatserver.ChatServerThread;
import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import routes.Conectar;

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
            int indice = 0;
            double lat = 0.0, lng = 0.0;
            String oficina = "", descripcion = "", oficinas = "";
            
            JSONObject results = new JSONObject();
            JSONArray resultados = new JSONArray();
            
            Conectar objeto = new Conectar();
            Connection baseDatos = objeto.getConnection();
            
            Statement st = baseDatos.createStatement();
            String sql = "SELECT * FROM oficinas";
            
            ResultSet rs = st.executeQuery(sql);
            
            if (order.equals("Todaslasoficinas ")) {
                while (rs.next()) {
                    
                    oficina = rs.getString(2);
                    lat = Double.parseDouble(rs.getString(3));
                    lng = Double.parseDouble(rs.getString(4));
                    descripcion = rs.getString(5);
                        
                    resultados.put(indice, new JSONObject().put("geometry", new JSONObject().put("location", new JSONObject()
                    .put("lat", lat).put("lng", lng))).put("name", oficina).put("formatted_address", descripcion));
                    
                    indice++;
                }
                results.put("results", resultados);
            } else {
                while (rs.next()) {
                    if (rs.getString(2).equals(order)) {
                        oficina = rs.getString(2);
                        lat = Double.parseDouble(rs.getString(3));
                        lng = Double.parseDouble(rs.getString(4));
                        descripcion = rs.getString(5);
                    }
                }

                resultados.put(0, new JSONObject().put("geometry", new JSONObject().put("location", new JSONObject()
                    .put("lat", lat).put("lng", lng))).put("name", oficina).put("formatted_address", descripcion));
        
                results.put("results", resultados);
            }
            System.out.println(order);
            System.out.println(results.toString());
            
            File archivoTemporal = File.createTempFile("Oficina" + order + user, ".json");
            archivoTemporal.deleteOnExit();
            oficinas = archivoTemporal.getAbsolutePath().replace("\\", "/");

            FileWriter fileWriter = new FileWriter(oficinas);
            BufferedWriter data = new BufferedWriter(fileWriter);
            data.write(results.toString());

            data.close();
            fileWriter.close();

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
        } catch (SQLException ex) {
            Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
