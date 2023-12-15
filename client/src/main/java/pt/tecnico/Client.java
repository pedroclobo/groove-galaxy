package pt.tecnico;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Client {

    private static final String URL = "http://192.168.1.1:8080";
    private static final String ENDPOINT = "/songs/";

    public static void main(String[] args) {
        try {

            Scanner scanner = new Scanner(System.in);

            while (true) {

                System.out.print("Enter the music id (-1) to exit: ");
                String musicId = scanner.nextLine().trim();
                if (musicId.equals("-1")) {
                    return;
                }
                String url = URL + ENDPOINT + musicId;
                URL obj = new URL(url);

                // Open a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                // Set the request method to GET
                connection.setRequestMethod("GET");

                // Get the response code
                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                // Read the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Print the response
                System.out.println("Response: " + response);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
