package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ListSongsCommand implements MenuCommand {

    private URL url;

    private int songId;

    public ListSongsCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "user/" + userId + "/songs/");
    }

    public int getSongId() {
        return songId;
    }

    @Override
    public void execute() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);
                if (jsonResponse.has("songs")) {
                    JsonObject songs = jsonResponse.getAsJsonObject("songs");
                    System.out.println("GrooveGalaxy");
                    System.out.println("Songs:");
                    for (String songId : songs.keySet()) {
                        System.out.println(songId + ". " + songs.get(songId).getAsString());
                    }

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Pick a song (enter the number): ");
                    songId = scanner.nextInt();
                } else {
                    System.out.println("No songs found in the response.");
                }
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
