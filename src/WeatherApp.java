import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;

public class WeatherApp {
    public  static JSObject get weatherData(String locationName){
        JSONArray locationData = getLocationData(locationName)
    }

    private static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName+ "&count=10&language=en&format=json";
        
        try{
            HttpURLConnection conn = fetchApiResponses(urlString);

            if (conn.getResponseCode() != 200){
                System.out.println("Error: Colud not conncet to API");
                return null;
            }else{
                StringBuilder resultJason = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()){
                    resultJason.append(scanner.nextLine());
                }
                scanner.closer();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSObject resultsJsonObj = (JSObject)  parser. parse()

            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static  HttpURLConnection fetchApiResponses(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;

        }catch(IOException e){
            e.printStackTrace();

        }

        return null;
    }
}










