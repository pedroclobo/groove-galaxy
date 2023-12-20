package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class AddUserToFamilyCommand implements MenuCommand {

    private URL url;
    private String addUrl;

    public AddUserToFamilyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "users/");
        addUrl = baseUrl + "user/" + userId + "/add_to_family/";
    }

    @Override
    public String getDescription() {
        return "Add User to Family";
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
                    System.out.println("Pick a user:");

                    Set<String> userIds = users.keySet();
                    List<String> userIdsList = new ArrayList<>(List.copyOf(userIds));
                    userIdsList.sort(Comparator.comparingInt(Integer::parseInt));

                    for (String userId : userIdsList) {
                        JsonObject user = users.getAsJsonObject(userId);
                        System.out.println(userId + ". " + user.get("name").getAsString());
                    }

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Choose a user (enter the number): ");
                    int userId = scanner.nextInt();

                    url = new URL(addUrl + userId);
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");

                    int responseCode = connection.getResponseCode();

                    if (responseCode != 200) {
                        System.out.println("Error: " + responseCode);
                    } else {
                        System.out.println("Operation successful");
                    }

                } else {
                    System.out.println("No users found in the response.");
                }

                Scanner scanner = new Scanner(System.in);
                System.out.println("Press enter to go back...");
                scanner.nextLine();

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
