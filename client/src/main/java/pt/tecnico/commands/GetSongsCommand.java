package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import pt.tecnico.AESKeyGenerator;
import pt.tecnico.JsonProtector;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Base64;
import java.util.Scanner;

public class GetSongsCommand implements MenuCommand {

    private URL userSongsUrl;
    private String songsUrl;

    private int songId;
    private int userId;

    public GetSongsCommand(String baseUrl, int userId) throws MalformedURLException {
        userSongsUrl = new URL(baseUrl + "user/" + userId + "/songs/");
        songsUrl = baseUrl + "songs/";
        this.userId = userId;
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

                        Key userKey = AESKeyGenerator.read("src/main/resources/keys/user-key-" + userId + ".key");
                        JsonObject songJson = JsonProtector.unprotect(jsonSongResponse, userKey);
                        JsonObject songMedia = songJson.getAsJsonObject("media");
                        JsonObject songMediaInfo = songMedia.getAsJsonObject("mediaInfo");
                        JsonObject songMediaContent = songMedia.getAsJsonObject("mediaContent");

                        String filename = songMediaInfo.get("title").getAsString() + "." + songMediaInfo.get("format").getAsString();
                        JsonArray lyricsArray = songMediaContent.get("lyrics").getAsJsonArray();
                        String lyrics = "";
                        for (int i = 0; i < lyricsArray.size(); i++) {
                            lyrics += lyricsArray.get(i).getAsString() + "\n";
                        }

                        String audioBase64 = songMediaContent.get("audioBase64").getAsString();
                        audioBase64 = audioBase64.replaceAll("[^A-Za-z0-9+/=]", "");

                        byte[] audioBytes = Base64.getDecoder().decode(audioBase64);

                        // write audioBytes to file
                        File file = new File("src/main/resources/songs/" + filename);
                        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                            fileOutputStream.write(audioBytes);
                            System.out.println("\nLyrics:\n\n" + lyrics);
                            System.out.println("Song saved to " + file.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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
