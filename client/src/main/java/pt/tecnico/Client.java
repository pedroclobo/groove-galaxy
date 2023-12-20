package pt.tecnico;

import pt.tecnico.commands.*;
import pt.tecnico.menus.MenuInvoker;

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

        MenuInvoker invoker = new MenuInvoker();
        invoker.addCommand(new LoginCommand(URL));
        invoker.printHeader();
        invoker.executeCommand(0);
        LoginCommand command = (LoginCommand) invoker.getCommand(0);
        int userId = command.getUserId();

        invoker = new MenuInvoker();
        invoker.addCommand(new ExitCommand());
        invoker.addCommand(new CreateUserKeyCommand(URL, userId));
        invoker.addCommand(new GetSongsCommand(URL, userId));
        invoker.addCommand(new CreateFamilyCommand(URL, userId));
        invoker.addCommand(new GetFamilyCommand(URL, userId));
        invoker.addCommand(new AddUserToFamilyCommand(URL, userId));
        invoker.addCommand(new GetFamilyKeyCommand(URL, userId));

        invoker.displayMenu();
    }
}
