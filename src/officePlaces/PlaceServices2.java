package officePlaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlaceServices2 {
    
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_SEARCH = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    // KEY https://developers.google.com/places/web-service/get-api-key 
    private static final String API_KEY = "AIzaSyCBeJ-MsPHTftIXS8vVJIxA_oQnzKT8TXk";
    
    public static byte[] staticMap(double lat, double lng, int zoom) {
        try {
            String base = "https://maps.googleapis.com/maps/api/staticmap?";
            HttpURLConnection conn = null;
            StringBuilder jsonResults = new StringBuilder();
            StringBuilder sb = new StringBuilder(base);
            sb.append("center=").append(String.valueOf(lat)).append(",").append(String.valueOf(lng));
            sb.append("&zoom=").append(String.valueOf(zoom));
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
    
}
