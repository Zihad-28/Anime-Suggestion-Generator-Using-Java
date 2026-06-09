package animesuggestiongenerator;

import java.util.Scanner;

public class TerminalApp {

    public static void run() {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n====================================");
            System.out.println("      ANIME SUGGESTION GENERATOR");
            System.out.println("====================================");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Select Option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    login(sc);
                    break;

                case "2":
                    signup(sc);
                    break;

                case "3":
                    System.out.println("\nThank you for using the program.");
                    sc.close();
                    return;

                default:
                    System.out.println("\nInvalid option.");
            }
        }
    }

    private static void login(Scanner sc) {

        System.out.print("\nUsername: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        if (UserManager.validateLogin(username, password)) {
            System.out.println("\nLogin Successful!");
            animeMenu(sc, username);
        } else {
            System.out.println("\nInvalid Username or Password.");
        }
    }

    private static void signup(Scanner sc) {

        System.out.print("\nFull Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("\nAll fields are required.");
            return;
        }

        if (UserManager.userExists(username)) {
            System.out.println("\nUsername already exists.");
            return;
        }

        if (UserManager.emailExists(email)) {
            System.out.println("\nEmail already exists.");
            return;
        }

        boolean success = UserManager.registerUser(name, username, email, password);

        if (success) {
            System.out.println("\nAccount Created Successfully.");
        } else {
            System.out.println("\nRegistration Failed.");
        }
    }
    private static void animeMenu(Scanner sc, String username) {
        AnimeEngine engine = new AnimeEngine();
        while (true) {

            System.out.println("\n====================================");
            System.out.println("Welcome " + UserManager.getDisplayName(username));
            System.out.println("====================================");
            System.out.println("1. Search Anime");
            System.out.println("2. Logout");
            System.out.print("Select Option: ");

            String option = sc.nextLine().trim();

            if (option.equals("2")) {
                System.out.println("\nLogged Out.");
                break;
            }

            if (!option.equals("1")) {
                System.out.println("\nInvalid option.");
                continue;
            }

            System.out.print("\nEnter Anime Title or Genre: ");
            String query = sc.nextLine().trim();

            if (query.isEmpty()) {
                System.out.println("Search field cannot be empty.");
                continue;
            }
            System.out.print("Year Filter (Any Year / 2025 / 2024 ...): ");
            String year = sc.nextLine().trim();
            if (year.isEmpty()) year = "Any Year";

            System.out.print("Type Filter (Any Type / TV / Movie / OVA / ONA / Special / Music): ");
            String type = sc.nextLine().trim();
            if (type.isEmpty()) type = "Any Type";

            System.out.print("Origin Filter (Any Origin / Japan / China / South Korea): ");
            String origin = sc.nextLine().trim();
            if (origin.isEmpty()) origin = "Any Origin";

            System.out.println("\nSearching...");
            System.out.println("------------------------------------");
            engine.showSuggestions(query, year, type, origin);
            System.out.println("------------------------------------");
        }
    }
}