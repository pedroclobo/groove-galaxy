package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import pt.tecnico.AESKeyGenerator;
import pt.tecnico.JsonProtector;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Scanner;

public class GetFamilyKeyCommand implements MenuCommand {

    private URL url;

    private int songId;
    private int userId;

    public GetFamilyKeyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "user/" + userId + "/get_family_key/");
        this.userId = userId;
    }

    @Override
    public String getDescription() {
        return "Get User Family Key";
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
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(jsonResponse);
                System.out.println(json);

                System.out.println();

                Key masterKey = AESKeyGenerator.read("src/main/resources/keys/aes-key-" + userId + ".key");
                Key userKey = JsonProtector.unprotectKey(jsonResponse, masterKey);
                AESKeyGenerator.write("src/main/resources/keys/user-key-" + userId + ".key", userKey);
            }

            Scanner scanner = new Scanner(System.in);
            System.out.print("Press enter to go back...");
            scanner.nextLine();

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
