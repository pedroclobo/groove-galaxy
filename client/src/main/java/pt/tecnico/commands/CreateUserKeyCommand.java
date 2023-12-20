package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.util.Scanner;

import pt.tecnico.AESKeyGenerator;
import pt.tecnico.JsonProtector;

public class CreateUserKeyCommand implements MenuCommand {

    private URL url;
    private int userId;

    public CreateUserKeyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "create_user_key/" + userId);
        this.userId = userId;
    }

    @Override
    public String getDescription() {
        return "Create User Key";
    }

    @Override
    public void execute() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            int responseCode = connection.getResponseCode();
            Scanner scanner = new Scanner(System.in);

            if (responseCode != 200) {
                System.out.println("Error: " + responseCode);
            } else {
                System.out.println("Operation successful");
            }


            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            JsonObject jsonResponse = new Gson().fromJson(response.toString(), JsonObject.class);

            Key masterKey = AESKeyGenerator.read("src/main/resources/keys/aes-key-" + userId + ".key");
            Key userKey = JsonProtector.unprotectKey(jsonResponse, masterKey);
            AESKeyGenerator.write("src/main/resources/keys/user-key-" + userId + ".key", userKey);

            // Print the response
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonResponse);
            System.out.println(json);

            System.out.println("Decrypted user key and saved to file");

            System.out.println("Press enter to go back...");
            scanner.nextLine();

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
