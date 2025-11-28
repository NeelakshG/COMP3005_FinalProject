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

    private static void userRegistration(String username, String password, String first_name,
                                         String last_name, String email, String phone, String gender) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO members (username, password, first_name, last_name, email, phone, gender) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');", username, password, first_name, last_name, email, phone, gender);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Registration successful======");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static int userLogin(String username, String password) {
        int member_id = 0;
        try {
            statement = connection.createStatement();
            String query = String.format("SELECT * FROM members WHERE username = '%s' AND password = '%s;", username, password);
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
        System.out.println("1. Registration");
        System.out.println("2. Login");
        System.out.println("3. Exit Program");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();
        int member_id;
        switch (choice) {
            case 1: // registration
                System.out.print("Enter username: ");
                String username = scanner.next();
                System.out.print("Enter password: ");
                String password = scanner.next();
                System.out.print("Enter first name: ");
                String first_name = scanner.next();
                System.out.print("Enter last name: ");
                String last_name = scanner.next();
                System.out.print("Enter email: ");
                String email = scanner.next();
                System.out.print("Enter phone: ");
                String phone = scanner.next();
                System.out.print("Enter gender: ");
                String gender = scanner.next();
                userRegistration(username, password, first_name, last_name, email, phone, gender);
                break;
            case 2: // login
                System.out.print("Enter username: ");
                String usernameLogin = scanner.next();
                System.out.print("Enter password: ");
                String passwordLogin = scanner.next();
                member_id = userLogin(usernameLogin, passwordLogin);
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
            case 1:
                updateInformationDisplay(member_id);
                break;
            case 2:
                break;
            case 3:

        }
    }

    // selects update profile
    private static void updateInformationDisplay(int member_id) {
        System.out.print("WHAT INFORMATION TO UPDATE\n");
        System.out.println("1. Update Username");
        System.out.println("2. Update Password");
        System.out.println("3. Update Email");
        System.out.println("4. Update Phone Number");
        System.out.println("5. Update Fitness Goals");
        System.out.println("6. Update Health Metrics");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1: // username
                System.out.print("Enter new username: ");
                String new_username = scanner.next();
                updateUsername(member_id, new_username);
                break;
            case 2: // password
                System.out.print("Enter new password: ");
                String new_password = scanner.next();
                updatePassword(member_id, new_password);
                break;
            case 3: // email
                System.out.print("Enter new email: ");
                String new_email = scanner.next();
                updateEmail(member_id, new_email);
                break;
            case 4: // phone number
                System.out.print("Enter new phone number: ");
                String new_phone = scanner.next();
                updatePhone(member_id, new_phone);
                break;
            case 5: // fitness goals
                fitnessGoals(member_id);

                break;
            case 6: // health metrics
                healthMetrics(member_id);
        }
    }

    // update username
    private static void updateUsername(int member_id, String new_username) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE members " +
                    "SET username = '%s' WHERE member_id = %d;", new_username, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Username update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update password
    private static void updatePassword(int member_id, String new_password) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE members " +
                    "SET password = '%s' WHERE member_id = %d;", new_password, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Password update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update email
    private static void updateEmail(int member_id, String new_email) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE members " +
                    "SET email = '%s' WHERE member_id = %d;", new_email, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Email update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update phone number
    private static void updatePhone(int member_id, String new_phone) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE members " +
                    "SET phone = '%s' WHERE member_id = %d;", new_phone, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Phone number update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update fitness goals
    private static void fitnessGoals(int member_id) {
        System.out.println("1. Add Fitness Goals");
        System.out.println("2. Update Fitness Goals");
        System.out.print("Select Option: ");
        int  choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: // add fitness goal
                System.out.println("Enter target weight: ");
                int target_weight = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter start date: ");
                String start_date = scanner.next();
                scanner.nextLine();
                System.out.println("Enter end_date: ");
                String end_date = scanner.next();
                scanner.nextLine();
                System.out.println("Enter status: ");
                String status = scanner.next();
                scanner.nextLine();
                addFitnessGoals(member_id, target_weight, start_date, end_date, status);
                break;
            case 2: // update fitness goal
        }
    }

    private static void addFitnessGoals(int member_id, int target_weight, String start_date, String end_date, String status) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO fitnessGoal (member_id, target_weight, start_date, end_state, status)" +
                    "VALUES ('%d', '%s', '%s', '%s', '%s');", member_id, target_weight, start_date, end_date, status);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Fitness goal insert successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateFitnessGoals() {

    }

    private static void healthMetrics(int member_id) {
        System.out.println("1. Set health metrics");
        System.out.println("2. Update health metrics");
        System.out.print("Select Option: ");
        int  choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: // set health metrics
                System.out.println("Enter weight: ");
                int weight = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter heartrate: ");
                int heartrate = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter bodyfat percentage: ");
                int bodyfat_percentage = scanner.nextInt();
                scanner.nextLine();
                setHealthMetrics(member_id, weight, heartrate, bodyfat_percentage);
                break;
            case 2: // update health metrics
        }
    }

    private static void setHealthMetrics(int member_id, int weight, int heartrate, int bodyfat_percentage) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO HealthMetric (member_id, target_weight, start_date, end_state, status)" +
                    "VALUES ('%d', '%s', '%s', '%s', '%s');", member_id, weight, heartrate, bodyfat_percentage);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Health metric set successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateHealthMetrics() {

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