import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class Profile {
    static Connection connection;
    static Statement statement;

    static Scanner scanner;


    //================== UPDATE ==================


    // selects update profile
    public static void updateInformationDisplay(int member_id) {
        System.out.print("WHAT INFORMATION TO UPDATE\n");
        System.out.println("1. Update Email");
        System.out.println("2. Update Password");
        System.out.println("3. Update Phone Number");
        System.out.println("4. Update Fitness Goals");
        System.out.println("5. Update Health Metrics");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();

        switch (choice) {

            case 1: // email
                System.out.print("Enter new email: ");
                String new_email = scanner.next();
                updateEmail(member_id, new_email);
                break;
            case 2: // password
                System.out.print("Enter new password: ");
                String new_password = scanner.next();
                updatePassword(member_id, new_password);
                break;
            case 3: // phone number
                System.out.print("Enter new phone number: ");
                String new_phone = scanner.next();
                updatePhone(member_id, new_phone);
                break;
            case 4: // fitness goals
                fitnessGoals(member_id);
                break;
            case 5: // health metrics
                healthMetrics(member_id);
                break;
        }
    }

    // update email
    public static void updateEmail(int member_id, String new_email) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE member " +
                    "SET email = '%s' WHERE member_id = %d;", new_email, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Email update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update password
    private static void updatePassword(int member_id, String new_password) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE member " +
                    "SET password = '%s' WHERE member_id = %d;", new_password, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Password update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    // update phone number
    private static void updatePhone(int member_id, String new_phone) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE member " +
                    "SET phone = '%s' WHERE member_id = %d;", new_phone, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Phone number update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }



    //================== FITNESS GOALS ==================

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
                updateFitnessGoals(member_id);
                break;
        }
    }

    private static void addFitnessGoals(int member_id, String goal_type, int target_weight, String start_date, String end_date, String status) {
        try {
            statement = connection.createStatement();
            String insertQuery = String.format("INSERT INTO fitnessgoal (member_id, goal_type, target_weight, start_date, end_state, status)" +
                    "VALUES ('%d', '%s', '%s', '%s', '%s');", member_id, goal_type, target_weight, start_date, end_date, status);
            statement.executeUpdate(insertQuery);
            System.out.println("=====Fitness goal insert successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateFitnessGoals(int member_id) {
        System.out.println("1. Update goal type");
        System.out.println("2. Update target weight");
        System.out.println("3. Update start date");
        System.out.println("4. Update end date");
        System.out.println("5. Update status");
        int  choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("Enter goal type: ");
                String goal_type = scanner.next();
                scanner.nextLine();
                updateGoalType(member_id, goal_type);
                break;
            case 2:
                System.out.println("Enter target weight: ");
                int target_weight = scanner.nextInt();
                scanner.nextLine();
                updateTargetWeight(member_id, target_weight);
                break;
            case 3:
                System.out.println("Enter start date: ");
                String start_date = scanner.next();
                scanner.nextLine();
                updateStartDate(member_id, start_date);
                break;
            case 4:
                System.out.println("Enter end date: ");
                String end_date = scanner.next();
                scanner.nextLine();
                updateEndDate(member_id, end_date);
                break;
            case 5:
                System.out.println("Enter status: ");
                String status = scanner.next();
                scanner.nextLine();
                updateStatus(member_id, status);
                break;
        }
    }
    private static void updateGoalType(int member_id, String goal_type) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE fitnessgoal " +
                    "SET goal_type = '%s' WHERE member_id = %d;", goal_type, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Goal type update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateTargetWeight(int member_id, int target_weight) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE fitnessgoal " +
                    "SET target_weight = '%d' WHERE member_id = %d;", target_weight, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Target weight update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateStartDate(int member_id, String start_date) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE fitnessgoal " +
                    "SET start_date = '%s' WHERE member_id = %d;", start_date, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Start date update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateEndDate(int member_id, String end_date) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE fitnessgoal " +
                    "SET end_date = '%s' WHERE member_id = %d;", end_date, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====End date update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateStatus(int member_id, String status) {
        try {
            statement = connection.createStatement();
            String updateQuery = String.format("UPDATE fitnessgoal " +
                    "SET status = '%s' WHERE member_id = %d;", status, member_id);
            statement.executeUpdate(updateQuery);
            System.out.println("=====Status update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }




    //================== HEALTH METRICS ==================


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
                updateHealthMetrics(member_id);
                break;
        }
    }

    private static void setHealthMetrics(int member_id, int weight, int heartrate, int bodyfat_percentage) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = String.format(
                    "INSERT INTO healthmetrics (member_id, weight, heartrate, body_fat_percent) " +
                            "VALUES (%d, %d, %d, %d);",
                    member_id, weight, heartrate, bodyfat_percentage
            );

            stmt.executeUpdate(sql);
            System.out.println("===== Health metrics inserted successfully =====");

        } catch (Exception e) {
            System.out.println("Error inserting health metrics: " + e.getMessage());
        }
    }

    private static void updateHealthMetrics(int member_id) {
        System.out.println("WHAT HEALTH METRIC DO YOU WANT TO UPDATE?");
        System.out.println("1. Update weight");
        System.out.println("2. Update heartrate");
        System.out.println("3. Update body fat %");
        System.out.print("Select Option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter new weight: ");
                int newWeight = scanner.nextInt();
                scanner.nextLine();
                updateWeight(member_id, newWeight);
                break;

            case 2:
                System.out.print("Enter new heartrate: ");
                int newHeartrate = scanner.nextInt();
                scanner.nextLine();
                updateHeartrate(member_id, newHeartrate);
                break;

            case 3:
                System.out.print("Enter new body fat %: ");
                int newBodyFat = scanner.nextInt();
                scanner.nextLine();
                updateBodyFat(member_id, newBodyFat);
                break;
        }
    }

    private static void updateWeight(int member_id, int weight) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = String.format(
                    "UPDATE healthmetrics SET weight = %d WHERE member_id = %d;",
                    weight, member_id
            );

            stmt.executeUpdate(sql);
            System.out.println("===== Weight updated =====");

        } catch (Exception e) {
            System.out.println("Error updating weight: " + e.getMessage());
        }
    }

    private static void updateHeartrate(int member_id, int heartrate) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = String.format(
                    "UPDATE healthmetrics SET heartrate = %d WHERE member_id = %d;",
                    heartrate, member_id
            );

            stmt.executeUpdate(sql);
            System.out.println("===== Heartrate updated =====");

        } catch (Exception e) {
            System.out.println("Error updating heartrate: " + e.getMessage());
        }
    }

    private static void updateBodyFat(int member_id, int bodyFat) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = String.format(
                    "UPDATE healthmetrics SET body_fat_percent = %d WHERE member_id = %d;",
                    bodyFat, member_id
            );

            stmt.executeUpdate(sql);
            System.out.println("===== Body Fat % updated =====");

        } catch (Exception e) {
            System.out.println("Error updating body fat: " + e.getMessage());
        }
    }





    //================== VIEW ==================



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
            statement.executeQuery("SELECT * FROM member WHERE member_id = " + member_id);
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
            statement.executeQuery("SELECT * FROM fitnessgoal WHERE member_id = " + member_id);
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
        ResultSetMetaData meta;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            resultSet = stmt.executeQuery(
                    "SELECT * FROM healthmetrics WHERE member_id = " + member_id
            );
            meta = resultSet.getMetaData();

            int colWidth = 25;

            System.out.println("\n");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                System.out.printf("%-" + colWidth + "s ", meta.getColumnName(i));
            }

            while (resultSet.next()) {
                System.out.println();
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("metric_id"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("weight"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("heartrate"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getInt("body_fat_percent"));
                System.out.printf("%-" + colWidth + "s ", resultSet.getString("recorded_at"));
            }

        } catch (Exception e) {
            System.out.println("Error viewing metrics: " + e.getMessage());
        }
    }





}
