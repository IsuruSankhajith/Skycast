import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {

    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        if (locationData != null && locationData.size() > 0) {
            JSONObject location = (JSONObject) locationData.get(0);
            double latitude = (double) location.get("latitude");
            double longitude = (double) location.get("longitude");

            String urlString = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=" + latitude + "&longitude=" + longitude +
                    "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2/Los_Angeles";
            try {
                HttpURLConnection conn = fetchApiResponses(urlString);

                if (conn.getResponseCode() != 200) {
                    System.out.println("Error: Could not connect to the API");
                } else {
                    StringBuilder resultJson = new StringBuilder();
                    Scanner scanner = new Scanner(conn.getInputStream());
                    while (scanner.hasNext()) {
                        resultJson.append(scanner.nextLine());
                    }
                    scanner.close();
                    conn.disconnect();

                    JSONParser parser = new JSONParser();
                    JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                    JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

                    JSONArray time = (JSONArray) hourly.get("time");
                    int index = findIndexOfCurrentTime(time);

                    JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                    double temperature = (double) temperatureData.get(index);

                    JSONArray weathercode = (JSONArray) hourly.get("weathercode");
                    String weatherCondition = convertWeatherCode((long) weathercode.get(index));

                    JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
                    long humidity = (long) relativeHumidity.get(index);

                    JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
                    double windspeed = (double) windspeedData.get(index);

                    JSONObject weatherData = new JSONObject();
                    weatherData.put("temperature", temperature);
                    weatherData.put("weather Condition", weatherCondition);
                    weatherData.put("humidity", humidity);
                    weatherData.put("windspeed", windspeed);

                    return weatherData;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponses(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to the API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponses(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).equals(currentTime)) {
                return i;
            }
        }
        return -1; // Handle not found
    }

    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";
        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L && weatherCode > 0L) {
            weatherCondition = "Cloudy";
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain";
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}
