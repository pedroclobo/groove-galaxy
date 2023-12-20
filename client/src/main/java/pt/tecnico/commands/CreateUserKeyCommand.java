package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class CreateUserKeyCommand implements MenuCommand {

    private URL url;

    public CreateUserKeyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "create_user_key/" + userId);
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
            System.out.println("Press enter to go back...");
            scanner.nextLine();

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
