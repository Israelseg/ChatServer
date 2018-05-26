/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entertainmentPlaces;

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
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author saxman
 */
public class PlaceService {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";

    private static final String TYPE_SEARCH = "/nearbysearch";

    private static final String OUT_JSON = "/json";

    // KEY https://developers.google.com/places/web-service/get-api-key 
    private static final String API_KEY = "AIzaSyCBeJ-MsPHTftIXS8vVJIxA_oQnzKT8TXk";


    /*
        A Nearby Search lets you search for places within a specified area.
        You can refine your search request by supplying keywords or specifying 
        the type of place you are searching for.
        @param type Restricts the results to places matching the specified type. 
        @param lat  The latitude around which to retrieve place information.
        @param lng  The longitude around which to retrieve place information.
        @param radius  Defines the distance (in meters) within which to return place results. 
        @return List of places nearby in format json
     */
    public static JSONArray searchByType(String type, double lat, double lng, int radius) {
        ArrayList<JSONObject> resultList = new ArrayList<>();

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        StringBuilder sb = new StringBuilder(PLACES_API_BASE);
        try {
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            // sb.append("?sensor=false");

            sb.append("?location=").append(String.valueOf(lat)).append(",").append(String.valueOf(lng));
            sb.append("&radius=").append(String.valueOf(radius));
            if (type != null) {
                sb.append("&type=").append(type);
            }
            sb.append("&key=" + API_KEY);
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            in.close();
        } catch (MalformedURLException e) {
            System.out.println("Error processing Places API URL " + e);
            return new JSONArray(resultList);
        } catch (IOException e) {
            System.out.println("Error connecting to Places API " + e);
            return new JSONArray(resultList);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        System.out.println(sb.toString());
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            Set keySet = jsonObj.keySet();
            if (keySet.contains("results")) {
                JSONArray jsonArray = jsonObj.getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    resultList.add(jsonArray.getJSONObject(i));
                }

            }
            while (keySet.contains("next_page_token")) {
                String hasNextPage = jsonObj.getString("next_page_token");
                StringBuilder sb2 = new StringBuilder(PLACES_API_BASE);

                sb2.append(TYPE_SEARCH);
                sb2.append(OUT_JSON);
                // sb.append("?sensor=false");
                sb2.append("?pagetoken=").append(URLEncoder.encode(hasNextPage, "utf8"));

                sb2.append("&key=" + API_KEY);

                URL url;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println("Thread sleep : " + ex);
                }
                try {
                    url = new URL(sb2.toString());
                    System.out.println(sb2.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    InputStreamReader in = new InputStreamReader(conn.getInputStream());

                    int read;
                    char[] buff = new char[1024];
                    jsonResults = new StringBuilder();
                    while ((read = in.read(buff)) != -1) {
                        jsonResults.append(buff, 0, read);
                    }
                    in.close();
                } catch (MalformedURLException ex) {
                    System.out.println("Error processing Places API URL " + ex);
                } catch (IOException ex) {
                    System.out.println("Error connecting to Places API " + ex);
                }
                jsonObj = new JSONObject(jsonResults.toString());
                if (keySet.contains("results")) {
                    JSONArray jsonArray = jsonObj.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        resultList.add(jsonArray.getJSONObject(i));

                    }
                }
                keySet = jsonObj.keySet();
            }

        } catch (JSONException e) {
            System.out.println("Error processing JSON results " + e);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PlaceService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new JSONArray(resultList);
    }

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
            Logger.getLogger(PlaceService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PlaceService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void main(String[] args) {
        double lat = 22.307234, lng = -97.888767;

        JSONArray searchByType = PlaceService.searchByType(PlaceService.TYPE_BAR, lat, lng, 2000);
        JSONObject jsonObject = searchByType.getJSONObject(0);
        System.out.println(jsonObject.getString("name"));
        JSONObject location = jsonObject.getJSONObject("geometry").getJSONObject("location");
        System.out.println(location.getDouble("lat"));
        System.out.println(location.getDouble("lng"));

    }
    /*
    Table 1: Types supported in place search and addition
     */
 /*
    AQUARIUM
    ART_GALLERY
    BAR
    BEAUTY_SALON
    CAFE
    CASINO
    MUSEUM
    MOVIE_THEATER
    NIGHT_CLUB
    PARK
    SPA
    ZOO
    POINT_OF_INTEREST
     */
    public static final String TYPE_ACCOUNTING = "accounting";
    public static final String TYPE_AIRPORT = "airport";
    public static final String TYPE_AMUSEMENT_PARK = "amusement_park";
    public static final String TYPE_AQUARIUM = "aquarium";
    public static final String TYPE_ART_GALLERY = "art_gallery";
    public static final String TYPE_ATM = "atm";
    public static final String TYPE_BAKERY = "bakery";
    public static final String TYPE_BANK = "bank";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_BEAUTY_SALON = "beauty_salon";
    public static final String TYPE_BICYCLE_STORE = "bicycle_store";
    public static final String TYPE_BOOK_STORE = "book_store";
    public static final String TYPE_BOWLING_ALLEY = "bowling_alley";
    public static final String TYPE_BUS_STATION = "bus_station";
    public static final String TYPE_CAFE = "cafe";
    public static final String TYPE_CAMPGROUND = "campground";
    public static final String TYPE_CAR_DEALER = "car_dealer";
    public static final String TYPE_CAR_RENTAL = "car_rental";
    public static final String TYPE_CAR_REPAIR = "car_repair";
    public static final String TYPE_CAR_WASH = "car_wash";
    public static final String TYPE_CASINO = "casino";
    public static final String TYPE_CEMETERY = "cemetery";
    public static final String TYPE_CHURCH = "church";
    public static final String TYPE_CITY_HALL = "city_hall";
    public static final String TYPE_CLOTHING_STORE = "clothing_store";
    public static final String TYPE_CONVENIENCE_STORE = "convenience_store";
    public static final String TYPE_COURTHOUSE = "courthouse";
    public static final String TYPE_DENTIST = "dentist";
    public static final String TYPE_DEPARTMENT_STORE = "department_store";
    public static final String TYPE_DOCTOR = "doctor";
    public static final String TYPE_ELECTRICIAN = "electrician";
    public static final String TYPE_ELECTRONICS_STORE = "electronics_store";
    public static final String TYPE_EMBASSY = "embassy";
    public static final String TYPE_FIRE_STATION = "fire_station";
    public static final String TYPE_FLORIST = "florist";
    public static final String TYPE_FUNERAL_HOME = "funeral_home";
    public static final String TYPE_FURNITURE_STORE = "furniture_store";
    public static final String TYPE_GAS_STATION = "gas_station";
    public static final String TYPE_GYM = "gym";
    public static final String TYPE_HAIR_CARE = "hair_care";
    public static final String TYPE_HARDWARE_STORE = "hardware_store";
    public static final String TYPE_HINDU_TEMPLE = "hindu_temple";
    public static final String TYPE_HOME_GOODS_STORE = "home_goods_store";
    public static final String TYPE_HOSPITAL = "hospital";
    public static final String TYPE_INSURANCE_AGENCY = "insurance_agency";
    public static final String TYPE_JEWELRY_STORE = "jewelry_store";
    public static final String TYPE_LAUNDRY = "laundry";
    public static final String TYPE_LAWYER = "lawyer";
    public static final String TYPE_LIBRARY = "library";
    public static final String TYPE_LIQUOR_STORE = "liquor_store";
    public static final String TYPE_LOCAL_GOVERNMENT_OFFICE = "local_government_office";
    public static final String TYPE_LOCKSMITH = "locksmith";
    public static final String TYPE_LODGING = "lodging";
    public static final String TYPE_MEAL_DELIVERY = "meal_delivery";
    public static final String TYPE_MEAL_TAKEAWAY = "meal_takeaway";
    public static final String TYPE_MOSQUE = "mosque";
    public static final String TYPE_MOVIE_RENTAL = "movie_rental";
    public static final String TYPE_MOVIE_THEATER = "movie_theater";
    public static final String TYPE_MOVING_COMPANY = "moving_company";
    public static final String TYPE_MUSEUM = "museum";
    public static final String TYPE_NIGHT_CLUB = "night_club";
    public static final String TYPE_PAINTER = "painter";
    public static final String TYPE_PARK = "park";
    public static final String TYPE_PARKING = "parking";
    public static final String TYPE_PET_STORE = "pet_store";
    public static final String TYPE_PHARMACY = "pharmacy";
    public static final String TYPE_PHYSIOTHERAPIST = "physiotherapist";
    public static final String TYPE_PLUMBER = "plumber";
    public static final String TYPE_POLICE = "police";
    public static final String TYPE_POST_OFFICE = "post_office";
    public static final String TYPE_REAL_ESTATE_AGENCY = "real_estate_agency";
    public static final String TYPE_RESTAURANT = "restaurant";
    public static final String TYPE_ROOFING_CONTRACTOR = "roofing_contractor";
    public static final String TYPE_RV_PARK = "rv_park";
    public static final String TYPE_SCHOOL = "school";
    public static final String TYPE_SHOE_STORE = "shoe_store";
    public static final String TYPE_SHOPPING_MALL = "shopping_mall";
    public static final String TYPE_SPA = "spa";
    public static final String TYPE_STADIUM = "stadium";
    public static final String TYPE_STORAGE = "storage";
    public static final String TYPE_STORE = "store";
    public static final String TYPE_SUBWAY_STATION = "subway_station";
    public static final String TYPE_SUPERMARKET = "supermarket";
    public static final String TYPE_SYNAGOGUE = "synagogue";
    public static final String TYPE_TAXI_STAND = "taxi_stand";
    public static final String TYPE_TRAIN_STATION = "train_station";
    public static final String TYPE_TRANSIT_STATION = "transit_station";
    public static final String TYPE_TRAVEL_AGENCY = "travel_agency";
    public static final String TYPE_VETERINARY_CARE = "veterinary_care";
    public static final String TYPE_ZOO = "zoo";
    /*
    Table 2: Additional types returned by the Places service
    Note: The types below are not supported in the type filter of a place 
    search, or in the types property when adding a place.
     */
    public static final String TYPE_ADMINISTRATIVE_AREA_LEVEL_1 = "administrative_area_level_1";
    public static final String TYPE_ADMINISTRATIVE_AREA_LEVEL_2 = "administrative_area_level_2";
    public static final String TYPE_ADMINISTRATIVE_AREA_LEVEL_3 = "administrative_area_level_3";
    public static final String TYPE_ADMINISTRATIVE_AREA_LEVEL_4 = "administrative_area_level_4";
    public static final String TYPE_ADMINISTRATIVE_AREA_LEVEL_5 = "administrative_area_level_5";
    public static final String TYPE_COLLOQUIAL_AREA = "colloquial_area";
    public static final String TYPE_COUNTRY = "country";
    public static final String TYPE_ESTABLISHMENT = "establishment";
    public static final String TYPE_FINANCE = "finance";
    public static final String TYPE_FLOOR = "floor";
    public static final String TYPE_FOOD = "food";
    public static final String TYPE_GENERAL_CONTRACTOR = "general_contractor";
    public static final String TYPE_GEOCODE = "geocode";
    public static final String TYPE_HEALTH = "health";
    public static final String TYPE_INTERSECTION = "intersection";
    public static final String TYPE_LOCALITY = "locality";
    public static final String TYPE_NATURAL_FEATURE = "natural_feature";
    public static final String TYPE_NEIGHBORHOOD = "neighborhood";
    public static final String TYPE_PLACE_OF_WORSHIP = "place_of_worship";
    public static final String TYPE_POLITICAL = "political";
    public static final String TYPE_POINT_OF_INTEREST = "point_of_interest";
    public static final String TYPE_POST_BOX = "post_box";
    public static final String TYPE_POSTAL_CODE = "postal_code";
    public static final String TYPE_POSTAL_CODE_PREFIX = "postal_code_prefix";
    public static final String TYPE_POSTAL_CODE_SUFFIX = "postal_code_suffix";
    public static final String TYPE_POSTAL_TOWN = "postal_town";
    public static final String TYPE_PREMISE = "premise";
    public static final String TYPE_ROOM = "room";
    public static final String TYPE_ROUTE = "route";
    public static final String TYPE_STREET_ADDRESS = "street_address";
    public static final String TYPE_STREET_NUMBER = "street_number";
    public static final String TYPE_SUBLOCALITY = "sublocality";
    public static final String TYPE_SUBLOCALITY_LEVEL_4 = "sublocality_level_4";
    public static final String TYPE_SUBLOCALITY_LEVEL_5 = "sublocality_level_5";
    public static final String TYPE_SUBLOCALITY_LEVEL_3 = "sublocality_level_3";
    public static final String TYPE_SUBLOCALITY_LEVEL_2 = "sublocality_level_2";
    public static final String TYPE_SUBLOCALITY_LEVEL_1 = "sublocality_level_1";
    public static final String TYPE_SUBPREMISE = "subpremise";
}
