package pt.tecnico.menus;

import pt.tecnico.commands.MenuCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuInvoker {
    private final Map<Integer, MenuCommand> commandMap = new HashMap<>();
    private int optionNumber = 0;

    public void addCommand(MenuCommand command) {
        commandMap.put(optionNumber++, command);
    }

    public void executeCommand(int key) {
        MenuCommand command = commandMap.get(key);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Invalid choice. Please enter a valid option.");
        }
    }

    public MenuCommand getCommand(int key) {
        return commandMap.get(key);
    }

    private void clearTerminal() {
        System.out.print("\033\143");
    }

    public void printHeader() {
        clearTerminal();
        String header = """
                  ____                           ____       _                 
                 / ___|_ __ ___   _____   _____ / ___| __ _| | __ ___  ___   _
                | |  _| '__/ _ \\ / _ \\ \\ / / _ \\ |  _ / _` | |/ _` \\ \\/ / | | |
                | |_| | | | (_) | (_) \\ V /  __/ |_| | (_| | | (_| |>  <| |_| |
                 \\____|_|  \\___/ \\___/ \\_/ \\___|\\____|\\__,_|_|\\__,_/_/\\_\\\\__, |
                                                                         |___/
                """;
        System.out.println(header);
    }

    public void displayMenu() {
        while (true) {
            printHeader();
            for (int i = 0; i < optionNumber; i++) {
                System.out.println(i + ". " + commandMap.get(i).getDescription());
            }

            System.out.print("Choose an option (enter the number): ");

            Scanner scanner = new Scanner(System.in);
            int option = scanner.nextInt();

            printHeader();
            executeCommand(option);
        }
    }
}
