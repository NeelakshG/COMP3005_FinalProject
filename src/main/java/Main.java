import java.sql.*;
import java.util.Scanner;

public class Main {
    // postgresql user credentials
    static String url = "jdbc:postgresql://localhost:5432/FinalProject";
    static String user = "postgres";
    static String password = "d56988d6";

    static Connection connection;
    static Statement statement;

    static Scanner scanner;

    static Profile profile;
    static Registration registration;

    private static void userRegistration(String first_name, String last_name, String email, String phone,
                                         String password, String gender) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO Member (first_name, last_name, email, phone, password, gender) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", first_name, last_name, email, phone, password, gender);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Registration successful======");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static int userLogin(String email, String password) {
        int member_id = 0;
        try {
            statement = connection.createStatement();
            String query = String.format("SELECT * FROM Member WHERE email = '%s' AND password = '%s;", email, password);
            ResultSet resultSet = statement.executeQuery(query);
            member_id = resultSet.getInt("member_id");
            System.out.println("=====Login successful======");
        } catch (Exception e) {
            System.out.println(e);
        }
        return member_id;
    }

    private static void menuOptions() {
        System.out.print("MENU OPTIONS\n");
        System.out.println("1. New Profile Registration");
        System.out.println("2. Login");
        System.out.println("3. Exit Program");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();
        int member_id;
        switch (choice) {
            case 1: // new profile registration
                System.out.print("Enter first name: ");
                String first_name = scanner.next();
                System.out.print("Enter last name: ");
                String last_name = scanner.next();
                System.out.print("Enter email: ");
                String email = scanner.next();
                System.out.print("Enter phone: ");
                String phone = scanner.next();
                System.out.print("Enter password: ");
                String password = scanner.next();
                System.out.print("Enter gender: ");
                String gender = scanner.next();
                userRegistration(first_name, last_name, email, phone, password, gender);
                break;
            case 2: // login
                System.out.print("Enter email: ");
                String emailLogin = scanner.next();
                System.out.print("Enter password: ");
                String passwordLogin = scanner.next();
                member_id = userLogin(emailLogin, passwordLogin);
                profileOptions(member_id);
                break;
            case 3: // exit
                return;
        }
    }

    // logged in options
    private static void profileOptions(int member_id) {
        System.out.print("PROFILE OPTIONS\n");
        System.out.println("1. Update Profile Information");
        System.out.println("2. View Profile Information");
        System.out.println("3. Logout");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1: // update information
                profile.updateInformationDisplay(member_id);
                break;
            case 2: // view information
                profile.viewInformationDisplay(member_id);
                break;
            case 3: // logout
                break;

        }
    }




    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            if(connection != null) {
                System.out.print("Connected to the database");
            } else {
                System.out.print("Failed to make connection to the database");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        scanner = new Scanner(System.in);
        menuOptions();

    }
}