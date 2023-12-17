package pt.tecnico;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class Client {

    private static final String URL = "https://192.168.1.1:8443/songs/";

    public static void main(String[] args) {
        try {
            System.setProperty("javax.net.ssl.keyStore", "client.p12");
            System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
            System.setProperty("javax.net.ssl.trustStore", "client-keystore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeme");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter the music id ('exit' to exit): ");
                String musicId = scanner.nextLine().trim();
                if (musicId.equalsIgnoreCase("exit")) {
                    return;
                }

                // Construct the URL
                URL url = new URL(URL + musicId);

                // Open a connection to the URL
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                // Set the request method to GET (or other HTTP methods as needed)
                connection.setRequestMethod("GET");

                // Get the response code
                int responseCode = connection.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                // Read the response
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Print the response
                    System.out.println("Response: " + response.toString());
                }

                // Close the connection
                connection.disconnect();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
