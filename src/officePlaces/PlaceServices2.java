package officePlaces;

import com.example.hp.groupchat.shared.KeyWordSystem;
import com.example.hp.groupchat.shared.Message;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import routes.Conectar;

public class PlaceServices2 {

    // KEY https://developers.google.com/places/web-service/get-api-key 
    private static final String API_KEY = "AIzaSyCBeJ-MsPHTftIXS8vVJIxA_oQnzKT8TXk";

    public static JSONArray searchOffice(String oficinas) throws FileNotFoundException {
        ArrayList<JSONObject> resultList = new ArrayList<>();
        JsonParser parser = new JsonParser();
        FileReader fr = new FileReader(oficinas);
        JsonObject datos = parser.parse(fr).getAsJsonObject();

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(datos.toString());
            Set keySet = jsonObj.keySet();
            if (keySet.contains("results")) {
                JSONArray jsonArray = jsonObj.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    resultList.add(jsonArray.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            System.out.println("Error processing JSON results " + e);
        }

        return new JSONArray(resultList);
    }

    public static byte[] staticMap(String nombre) {
        try {
            String base = "https://maps.googleapis.com/maps/api/staticmap?";
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            StringBuilder sb = new StringBuilder(base);
            sb.append("center=").append(String.valueOf(22.254668)).append(",").append(String.valueOf(-97.848618));
            sb.append("&zoom=").append(String.valueOf(18));
            sb.append("&size=400x400");
            sb.append("&key=").append(API_KEY);
            System.out.println(sb.toString());
            URL url = new URL(sb.toString());
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            return out.toByteArray();
        } catch (MalformedURLException ex) {
            Logger.getLogger(PlaceServices2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlaceServices2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
        
        try{
        int indice = 0;
            double lat = 0.0, lng = 0.0;
            String oficina = "", descripcion = "", oficinas = "", order = "Serviciosescolares", user = "Andrés";
            
            JSONObject results = new JSONObject();
            JSONArray resultados = new JSONArray();
            
            Conectar objeto = new Conectar();
            Connection baseDatos = objeto.getConnection();
            
            Statement st = baseDatos.createStatement();
            String sql = "SELECT * FROM oficinas";
            
            ResultSet rs = st.executeQuery(sql);
            
            if (order.equals("Todaslasoficinas")) {
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
        } catch (IOException ex) {
            Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(PlaceOffices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
