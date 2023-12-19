package pt.tecnico.menus;

import pt.tecnico.commands.MenuCommand;

import java.util.HashMap;
import java.util.Map;

public class MenuInvoker {
    private final Map<String, MenuCommand> commandMap = new HashMap<>();

    void addCommand(String key, MenuCommand command) {
        commandMap.put(key, command);
    }

    void executeCommand(String key) {
        MenuCommand command = commandMap.get(key);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Invalid choice. Please enter a valid option.");
        }
    }
}
