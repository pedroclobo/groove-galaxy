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
import java.util.*;

public class RemoveUserFromFamilyCommand implements MenuCommand {

    private URL url;
    private String addUrl;

    public RemoveUserFromFamilyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "user/" + userId + "/family/");
        addUrl = baseUrl + "user/" + userId + "/remove_from_family/";
    }

    @Override
    public String getDescription() {
        return "Remove user from family";
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
                        System.out.println(userId + ". " + users.get(userId).getAsString());
                    }

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("Choose a user (enter the number): ");
                    int userId = scanner.nextInt();

                    URL url2 = new URL(addUrl + userId);
                    connection = (HttpsURLConnection) url2.openConnection();
                    connection.setRequestMethod("POST");

                    int responseCode = connection.getResponseCode();

                    if (responseCode != 200) {
                        System.out.println("Error: " + responseCode);
                    } else {
                        System.out.println("Operation successful");
                    }


                    // BufferedReader reader2 = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    // String line2;
                    // StringBuilder response2 = new StringBuilder();

                    // while ((line2 = reader2.readLine()) != null) {
                    //     response2.append(line2);
                    // }

                    // reader.close();

                    // JsonObject jsonResponse2 = new Gson().fromJson(response2.toString(), JsonObject.class);
                    // Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    // String json = gson.toJson(jsonResponse2);
                    // System.out.println(json);
                    
                    // Key masterKey = AESKeyGenerator.read("src/main/resources/keys/aes-key-" + userId + ".key");
                    // Key userKey = JsonProtector.unprotectKey(jsonResponse2, masterKey);
                    // AESKeyGenerator.write("src/main/resources/keys/user-key-" + userId + ".key", userKey);
                    

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
