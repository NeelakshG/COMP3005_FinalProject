import java.sql.*;
import java.util.Scanner;

public class Profile {
    static Connection connection;
    static Statement statement;

    static Scanner scanner;

    // selects update profile
    public static void updateInformationDisplay(int member_id) {
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
                System.out.println("Enter goal type: ");
                String goal_type = scanner.next();
                scanner.nextLine();
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
                addFitnessGoals(member_id, goal_type, target_weight, start_date, end_date, status);
                break;
            case 2: // update fitness goal
        }
    }

    private static void addFitnessGoals(int member_id, String goal_type, int target_weight, String start_date, String end_date, String status) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO fitnessGoal (member_id, goal_type, target_weight, start_date, end_state, status)" +
                    "VALUES ('%d', '%s', '%s', '%s', '%s');", member_id, goal_type, target_weight, start_date, end_date, status);
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
            String insertQuery = String.format("INSERT INTO healthMetric (member_id, target_weight, start_date, end_state, status)" +
                    "VALUES ('%d', '%s', '%s', '%s', '%s');", member_id, weight, heartrate, bodyfat_percentage);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Health metric set successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateHealthMetrics() {

    }








    public static void viewInformationDisplay(int member_id) {
        System.out.print("WHAT INFORMATION TO VIEW\n");
        System.out.println("1. View Personal Information");
        System.out.println("2. View Fitness Goals");
        System.out.println("3. View Health Metrics");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1: // personal information
                viewPersonalInformation(member_id);
                break;
            case 2: // fitness goals
                viewFitnessGoals(member_id);
                break;
            case 3:
                viewHealthMetrics(member_id);
                break;
        }



    }

    private static void viewPersonalInformation(int member_id) {
        ResultSet resultSet;
        ResultSetMetaData resultSetMetaData;

        try {
            statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM members WHERE member_id = " + member_id);
            resultSet = statement.getResultSet();
            resultSetMetaData = resultSet.getMetaData();
            int colWidth = 30;

            System.out.println("\n");
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.printf("%-" + colWidth + "s ", resultSetMetaData.getColumnName(i));
            }
            while (resultSet.next()) {
                System.out.print("\n");
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("member_id"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("first_name"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("last_name"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("email"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("phone"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("gender"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void viewFitnessGoals(int member_id) {
        ResultSet resultSet;
        ResultSetMetaData resultSetMetaData;

        try {
            statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM fitnessGoals WHERE member_id = " + member_id);
            resultSet = statement.getResultSet();
            resultSetMetaData = resultSet.getMetaData();
            int colWidth = 30;

            System.out.println("\n");
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.printf("%-" + colWidth + "s ", resultSetMetaData.getColumnName(i));
            }
            while (resultSet.next()) {
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("goal_id"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("goal_type"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("target_weight"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("start_date"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("end_state"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("status"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private static void viewHealthMetrics(int member_id) {
        ResultSet resultSet;
        ResultSetMetaData resultSetMetaData;

        try {
            statement = connection.createStatement();
            statement.executeQuery("SELECT * FROM healthMetrics WHERE member_id = " + member_id);
            resultSet = statement.getResultSet();
            resultSetMetaData = resultSet.getMetaData();
            int colWidth = 30;

            System.out.println("\n");
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.printf("%-" + colWidth + "s ", resultSetMetaData.getColumnName(i));
            }
            while (resultSet.next()) {
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("metric_id"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("weight"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("heartrate"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("bodyfat_percentage"));

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }




}
