package pt.tecnico;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import pt.tecnico.commands.ListSongsCommand;
import pt.tecnico.menus.MenuInvoker;
import pt.tecnico.commands.MenuCommand;
import pt.tecnico.commands.LoginCommand;

public class Client {

    private static final String URL = "https://192.168.1.1:8443/";
    private static final String KEYSTORE = "client.p12";
    private static final String KEYSTORE_PASS = "changeme";
    private static final String TRUSTSTORE = "client-keystore.jks";
    private static final String TRUSTSTORE_PASS = "changeme";

    private static void setupClient() {
        System.setProperty("javax.net.ssl.keyStore", KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASS);
        System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASS);
    }

    public static void main(String[] args) throws Exception {
        setupClient();

        LoginCommand loginCommand = new LoginCommand(URL);
        loginCommand.execute();
        int userId = loginCommand.getUserId();

        ListSongsCommand command = new ListSongsCommand(URL, userId);
        command.execute();

        System.out.println("Song id: " + command.getSongId());
    }
}
