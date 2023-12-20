package pt.tecnico.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class CreateFamilyCommand implements MenuCommand {

    private URL url;

    public CreateFamilyCommand(String baseUrl, int userId) throws MalformedURLException {
        url = new URL(baseUrl + "user/" + userId + "/create_family/");
    }

    @Override
    public String getDescription() {
        return "Create Family";
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
