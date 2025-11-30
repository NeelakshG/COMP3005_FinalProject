package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class Profile {

    private static Scanner scanner = new Scanner(System.in);

    //======DISPLAY INFO=====

    public static void updateInformationDisplay(int member_id) {
        System.out.print("WHAT INFORMATION TO UPDATE\n");
        System.out.println("1. Update Email");
        System.out.println("2. Update Password");
        System.out.println("3. Update Phone Number");
        System.out.println("4. Update First Name");
        System.out.println("5. Update Last Name");
        System.out.println("6. Update Gender");
        System.out.println("7. Update Fitness Goals");

        System.out.print("Enter menu option: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.print("Enter new email: ");
                updateEmail(member_id, scanner.next());
                break;

            case 2:
                System.out.print("Enter new password: ");
                updatePassword(member_id, scanner.next());
                break;

            case 3:
                System.out.print("Enter new phone number: ");
                updatePhone(member_id, scanner.next());
                break;
            case 4:
                System.out.print("Enter new first name: ");
                updateFirstName(member_id, scanner.next());
                break;
            case 5:
                System.out.print("Enter new last name: ");
                updateLastName(member_id, scanner.next());
                break;
            case 6:
                System.out.print("Enter new gender: ");
                updateGender(member_id, scanner.next());
                break;
            case 7:
                fitnessGoals(member_id);
                break;
        }
    }


    //====UPDATE USER INFORMATION
    //update email
    public static void updateEmail(int member_id, String new_email) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()){

            String updateQuery = String.format("UPDATE member  " +
                    "SET email = '%s' WHERE member_id = %d;", new_email, member_id);
            stmt.executeUpdate(updateQuery);
            System.out.println("=====Email update successful======");
        } catch  (Exception e) {
            System.out.println(e + "ss");
        }
    }

    //update password
    public static void updatePassword(int member_id, String new_password) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String updateQuery = String.format("UPDATE member " +
                    "SET password = '%s' WHERE member_id = %d;", new_password, member_id);
            stmt.executeUpdate(updateQuery);
            System.out.println("=====Password update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    //update phone number
    public static void updatePhone(int member_id, String new_phone) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement())  {

            String updateQuery = String.format("UPDATE member  " +
                    "SET phone = '%s' WHERE member_id = %d;", new_phone, member_id);
            stmt.executeUpdate(updateQuery);
            System.out.println("=====Phone number update successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    //update first name
    private static void updateFirstName(int member_id, String new_first_name) {
        String sql = "UPDATE member SET first_name = ? WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, new_first_name);
            stmt.setInt(2, member_id);
            stmt.executeUpdate();

            System.out.println("===== First name updated successfully =====");

        } catch (Exception e) {
            System.out.println("Error updating first name: " + e.getMessage());
        }
    }

    //update lastName
    private static void updateLastName(int member_id, String new_last_name) {
        String sql = "UPDATE member SET last_name = ? WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, new_last_name);
            stmt.setInt(2, member_id);
            stmt.executeUpdate();

            System.out.println("===== Last name updated successfully =====");

        } catch (Exception e) {
            System.out.println("Error updating last name: " + e.getMessage());
        }
    }

    //update gender
    private static void updateGender(int member_id, String new_gender) {
        String sql = "UPDATE member SET gender = ? WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, new_gender);
            stmt.setInt(2, member_id);
            stmt.executeUpdate();

            System.out.println("===== Gender updated successfully =====");

        } catch (Exception e) {
            System.out.println("Error updating gender: " + e.getMessage());
        }
    }


    public static void fitnessGoals(int member_id) {
        System.out.println("1. Add Fitness Goals");
        System.out.println("2. Update Fitness Goals");
        System.out.print("Select Option: ");
        int  choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: // add fitness goal
                System.out.println("Enter goal id: ");
                int goal_id = scanner.nextInt();
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
                addFitnessGoals(goal_id, member_id, goal_type, target_weight, start_date, end_date, status);
                break;
            case 2: // update fitness goal
                updateFitnessGoals(member_id);
                break;
        }
    }

    private static void addFitnessGoals(int goal_id,int member_id, String goal_type, int target_weight, String start_date, String end_date, String status) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "INSERT INTO fitnessgoal (goal_id, member_id, goal_type, target_weight, start_date, end_date, status) " +
                            "VALUES ('%d','%d', '%s', '%d', '%s', '%s', '%s');",
                    goal_id, member_id, goal_type, target_weight, start_date, end_date, status
            );

            System.out.println(insertQuery);

            stmt.executeUpdate(insertQuery);
            System.out.println("=====Fitness goal insert successful======");
        } catch  (Exception e) {
            System.out.println(e);
        }
    }

    //=====UPDATE FITNESS GOALS

    private static void updateFitnessGoals(int member_id) {
        System.out.println("1. Update goal type");
        System.out.println("2. Update target weight");
        System.out.println("3. Update start date");
        System.out.println("4. Update end date");
        System.out.println("5. Update status");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.println("Enter goal type: ");
                updateGoalType(member_id, scanner.nextLine());
                break;

            case 2:
                System.out.println("Enter target weight: ");
                updateTargetWeight(member_id, scanner.nextInt());
                scanner.nextLine();
                break;

            case 3:
                System.out.println("Enter start date (YYYY-MM-DD): ");
                updateStartDate(member_id, scanner.nextLine());
                break;

            case 4:
                System.out.println("Enter end date (YYYY-MM-DD): ");
                updateEndDate(member_id, scanner.nextLine());
                break;

            case 5:
                System.out.println("Enter status: ");
                updateStatus(member_id, scanner.nextLine());
                break;
        }
    }

    private static void updateGoalType(int member_id, String goal_type) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "UPDATE fitnessgoal SET goal_type = '%s' WHERE member_id = %d;",
                    goal_type, member_id
            );

            stmt.executeUpdate(insertQuery);
            System.out.println("===== Goal type updated =====");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateTargetWeight(int member_id, int target_weight) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "UPDATE fitnessgoal SET target_weight = %d WHERE member_id = %d;",
                    target_weight, member_id
            );

            stmt.executeUpdate(insertQuery);
            System.out.println("===== Target weight updated =====");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateStartDate(int member_id, String start_date) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "UPDATE fitnessgoal SET start_date = '%s' WHERE member_id = %d;",
                    start_date, member_id
            );

            stmt.executeUpdate(insertQuery);
            System.out.println("===== Start date updated =====");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateEndDate(int member_id, String end_date) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "UPDATE fitnessgoal SET end_date = '%s' WHERE member_id = %d;",
                    end_date, member_id
            );

            stmt.executeUpdate(insertQuery);
            System.out.println("===== End date updated =====");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void updateStatus(int member_id, String status) {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String insertQuery = String.format(
                    "UPDATE fitnessgoal SET status = '%s' WHERE member_id = %d;",
                    status, member_id
            );

            stmt.executeUpdate(insertQuery);
            System.out.println("===== Status updated =====");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void healthMetrics(int member_id) {
        System.out.println("1. Log new health metrics");
        System.out.println("2. View health history");
        System.out.print("Select Option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // clean buffer

        switch (choice) {
            case 1:
                // ask user for the metric values
                System.out.print("Enter weight: ");
                int weight = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter heartrate: ");
                int heartrate = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter body fat percentage: ");
                int bodyfat = scanner.nextInt();
                scanner.nextLine();

                // now log the entry
                logNewMetrics(member_id, weight, heartrate, bodyfat);
                break;

            case 2:
                viewHealthMetrics(member_id);
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }


    private static void logNewMetrics(int member_id, int weight, int heartrate, int bodyfat_percentage) {

        String sql = "INSERT INTO healthmetrics (member_id, weight, heartrate, body_fat_percentage) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member_id);
            stmt.setInt(2, weight);
            stmt.setInt(3, heartrate);
            stmt.setInt(4, bodyfat_percentage);

            stmt.executeUpdate();
            System.out.println("===== New health metric logged =====");

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
                    "UPDATE healthmetrics SET body_fat_percentage = %d WHERE member_id = %d;",
                    bodyFat, member_id
            );

            stmt.executeUpdate(sql);
            System.out.println("===== Body Fat % updated =====");

        } catch (Exception e) {
            System.out.println("Error updating body fat: " + e.getMessage());
        }
    }


    //===view

    public static void viewHealthMetrics(int member_id) {

        String sql = "SELECT * FROM healthmetrics WHERE member_id = ? ORDER BY recorded_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member_id);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== HEALTH METRIC HISTORY =====");
            System.out.printf("%-10s %-10s %-12s %-18s %-20s\n",
                    "ID", "Weight", "Heartrate", "Body Fat %", "Recorded At");

            while (rs.next()) {
                System.out.printf("%-10d %-10d %-12d %-18d %-20s\n",
                        rs.getInt("metric_id"),
                        rs.getInt("weight"),
                        rs.getInt("heartrate"),
                        rs.getInt("body_fat_percentage"),
                        rs.getTimestamp("recorded_at"));
            }

        } catch (Exception e) {
            System.out.println("Error viewing metrics: " + e.getMessage());
        }
    }

    public static void viewDashboard(int member_id) {

        String sql = "SELECT * FROM memberdashboard WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member_id);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("No dashboard data found for this member.");
                return;
            }

            System.out.println("\n==================== MEMBER DASHBOARD ====================");
            System.out.printf("Member ID:          %d\n", rs.getInt("member_id"));
            System.out.printf("Name:               %s\n", rs.getString("member_name"));
            System.out.printf("Email:              %s\n", rs.getString("email"));
            System.out.printf("Gender:              %s\n\n", rs.getString("gender"));


            System.out.println("----- Latest Health Metrics -----");
            System.out.printf("Latest Weight:      %s\n", safeNumber(rs, "latest_weight"));
            System.out.printf("Latest Heartrate:   %s\n", safeNumber(rs, "latest_heartrate"));
            System.out.printf("Latest Body Fat %%:  %s\n\n", safeNumber(rs, "latest_bodyfat"));

            System.out.println("----- Active Fitness Goal -----");
            System.out.printf("Goal Type:          %s\n", safeString(rs, "active_goal_type"));
            System.out.printf("Target Weight:      %s\n", safeString(rs, "active_goal_target"));
            System.out.printf("Goal End Date:      %s\n\n", safeString(rs, "goal_end_date"));

            System.out.println("----- Activity Summary -----");
            System.out.printf("Past Classes:       %d\n", rs.getInt("past_class_count"));
            System.out.printf("Next PT Session:    %s\n",
                    safeTimestamp(rs, "next_pt_session_date"));

            System.out.println("===========================================================\n");

        } catch (Exception e) {
            System.out.println("Error loading dashboard: " + e.getMessage());
        }
    }

    private static String safeString(ResultSet rs, String col) throws SQLException {
        String val = rs.getString(col);
        return (val == null) ? "—" : val;
    }

    private static String safeNumber(ResultSet rs, String col) throws SQLException {
        int num = rs.getInt(col);
        return rs.wasNull() ? "—" : Integer.toString(num);
    }

    private static String safeTimestamp(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return (ts == null) ? "—" : ts.toString();
    }











}
