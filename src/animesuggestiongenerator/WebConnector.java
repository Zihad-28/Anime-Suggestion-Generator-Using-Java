package animesuggestiongenerator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class WebConnector {

    protected String extractData(String genreId, String searchWord) {

        StringBuilder finalResponse = new StringBuilder();

        try {
            String encodedSearch = URLEncoder.encode(searchWord, StandardCharsets.UTF_8);

            for (int page = 1; page <= 2; page++) {

                String urlString;

                if (genreId.equals("0")) {
                    urlString = "https://api.jikan.moe/v4/anime?q=" + encodedSearch + "&limit=25&page=" + page;
                } else {
                    urlString = "https://api.jikan.moe/v4/anime?genres=" + genreId + "&order_by=score&sort=desc&limit=25&page=" + page;
                }
                String pageData = readFromApi(urlString);

                if (pageData.equals("none")) {
                    if (finalResponse.length() == 0) {
                        return "none";
                    }
                    break;
                }

                finalResponse.append(pageData);

                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        } catch (Exception e) {
            System.out.println("CONNECTION ERROR: " + e.getMessage());
            return "none";
        }

        return finalResponse.toString();
    }

    protected String extractData(String searchWord) {
        return extractData("0", searchWord);
    }

    private String readFromApi(String urlString) {

        StringBuilder response = new StringBuilder();

        try {
            URL url = URI.create(urlString).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
             + "AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/124.0.0.0 Safari/537.36");

            connection.setRequestProperty("Accept", "application/json");

            int code = connection.getResponseCode();

            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();
                return response.toString();

            } else {
                System.out.println("CONNECTION ERROR: API response code " + code);
                connection.disconnect();
                return "none";
            }

        } catch (Exception e) {
            System.out.println("CONNECTION ERROR: " + e.getMessage());
            return "none";
        }
    }
}