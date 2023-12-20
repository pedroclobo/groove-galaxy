package pt.tecnico.commands;

import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class ExitCommand implements MenuCommand {
    public ExitCommand() { }

    @Override
    public String getDescription() {
        return "Exit";
    }

    @Override
    public void execute() {
        System.exit(0);
    }
}