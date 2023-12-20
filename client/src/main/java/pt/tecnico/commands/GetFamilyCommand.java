package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class GetFamilyCommand implements MenuCommand {

    private URL url;

    private int songId;

    public GetFamilyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "user/" + userId + "/family/");
    }

    @Override
    public String getDescription() {
        return "Get User Family";
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

                if (jsonResponse.has("users")) {
                    JsonObject users = jsonResponse.getAsJsonObject("users");
                    System.out.println("Family:");
                    for (String userId : users.keySet()) {
                        System.out.println(userId + ". " + users.get(userId).getAsString());
                    }

                } else if (jsonResponse.has("error")) {
                    System.out.println("Error: " + jsonResponse.get("error").getAsString());
                }

                System.out.println();
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
