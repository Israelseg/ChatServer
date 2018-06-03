package officePlaces;

import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.chart.PieChart;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        double lat = 22.25475, lng = -97.84932;
        JSONObject results;
        JSONArray resultados = new JSONArray();

        results = new JSONObject();

        for (int i = 0; i < 3; i++) {
            resultados.put(i, new JSONObject().put("geometry", new JSONObject().put("location", new JSONObject().put("lat", lat).put("lng", lng))).put("name", "Centro computo"));
        }

        results.put("results", resultados);
        
        System.out.println(results.toString());
        
        File archivoTemporal = File.createTempFile("Oficina" + "Centrocomputo", ".json");
        archivoTemporal.deleteOnExit();
        String path = archivoTemporal.getAbsolutePath().replace("\\", "/");
        
        System.out.println(path);

        FileWriter fileWriter = new FileWriter(path);
        BufferedWriter data = new BufferedWriter(fileWriter);
        data.write(results.toString());
        
        data.close();
        fileWriter.close();
        
        //PieChart.Data data = new Gson().fromJson(json, PieChart.Data.class);
        JSONArray searchByType = PlaceServices2.searchOffice(path);
        JSONObject jsonObject = searchByType.getJSONObject(0);
        System.out.println(jsonObject.getString("name"));
        JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
        System.out.println(location.getDouble("lat"));
        System.out.println(location.getDouble("lng"));
    }
}
