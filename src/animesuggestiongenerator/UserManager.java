package animesuggestiongenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class UserManager {

    private static final String FILE = "user_data.txt";

    public static boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 4);
                if (parts.length == 4 && parts[1].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean emailExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 4);
                if (parts.length == 4 && parts[2].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 4);
                if (parts.length == 4 && parts[1].equals(username) && parts[3].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDisplayName(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 4);
                if (parts.length == 4 && parts[1].equals(username)) {
                    return parts[0];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(String name, String username,  String email, String password) {
        name     = name.trim();
        username = username.trim();
        email    = email.trim();
        password = password.trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return false;
        }
        if (userExists(username) || emailExists(email)) return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE, true))) {
            writer.println(name + ":" + username + ":" + email + ":" + password);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}