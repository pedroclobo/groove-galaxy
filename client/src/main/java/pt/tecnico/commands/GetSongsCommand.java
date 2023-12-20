package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class GetSongsCommand implements MenuCommand {

    private URL userSongsUrl;
    private String songsUrl;

    private int songId;

    public GetSongsCommand(String baseUrl, int userId) throws MalformedURLException {
        userSongsUrl = new URL(baseUrl + "user/" + userId + "/songs/");
        songsUrl = baseUrl + "songs/";
    }

    public int getSongId() {
        return songId;
    }

    @Override
    public String getDescription() {
        return "Get User Song";
    }

    @Override
    public void execute() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) userSongsUrl.openConnection();
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
                    System.out.println("Songs:");
                    for (String songId : songs.keySet()) {
                        System.out.println(songId + ". " + songs.get(songId).getAsString());
                    }

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Pick a song (enter the number): ");
                    songId = scanner.nextInt();

                    URL url = new URL(songsUrl + songId);
                    HttpsURLConnection songConnection = (HttpsURLConnection) url.openConnection();
                    songConnection.setRequestMethod("GET");

                    // print formatted song response
                    try (BufferedReader songReader = new BufferedReader(new InputStreamReader(songConnection.getInputStream()))) {
                        StringBuilder songResponse = new StringBuilder();
                        String songLine;

                        while ((songLine = songReader.readLine()) != null) {
                            songResponse.append(songLine);
                        }

                        JsonObject jsonSongResponse = new Gson().fromJson(songResponse.toString(), JsonObject.class);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        String json = gson.toJson(jsonSongResponse);
                        System.out.println(json);
                        System.out.println();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println("No songs found in the response.");
                }

                Scanner scanner = new Scanner(System.in);
                System.out.println("Press enter to go back...");
                scanner.nextLine();
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
