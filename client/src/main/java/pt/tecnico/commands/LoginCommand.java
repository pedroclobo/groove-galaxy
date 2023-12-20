package pt.tecnico.commands;

import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class LoginCommand implements MenuCommand {
    private URL url;
    private int userId;

    public LoginCommand(String baseUrl) throws MalformedURLException {
        url = new URL(baseUrl + "users/");
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String getDescription() {
        return "Login";
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
                    System.out.println("Login as a user:");

                    Set<String> userIds = users.keySet();
                    List<String> userIdsList = new ArrayList<>(List.copyOf(userIds));
                    userIdsList.sort(Comparator.comparingInt(Integer::parseInt));

                    for (String userId : userIdsList) {
                        JsonObject user = users.getAsJsonObject(userId);
                        System.out.println(userId + ". " + user.get("name").getAsString());
                    }

                    // prompt the user to choose a user
                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Choose a user (enter the number): ");
                    userId = scanner.nextInt();
                } else {
                    System.out.println("No users found in the response.");
                }
            }

            // Close the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}