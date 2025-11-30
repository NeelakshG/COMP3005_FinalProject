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


    //do not ask for the goal id cuz thats serialized, the database keeps track of the numbers
    public static void fitnessGoals(int member_id) {
        System.out.println("\n===== FITNESS GOALS =====");
        System.out.println("1. Add or Replace Fitness Goal");
        System.out.println("2. Update Existing Fitness Goal");
        System.out.print("Enter choice: ");

        int choice = scanner.nextInt();

        if (choice == 1) {
            addOrReplaceFitnessGoal(member_id);
        } else if (choice == 2) {
            updateFitnessGoals(member_id);
        } else {
            System.out.println("Invalid choice.");
        }
    }


    public static void addOrReplaceFitnessGoal(int member_id) {
        scanner.nextLine(); // clean buffer

        System.out.print("Enter goal type (e.g., cutting, bulking): ");
        String goalType = scanner.nextLine();

        System.out.print("Enter target weight: ");
        String targetWeight = scanner.nextLine();

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = scanner.nextLine();

        System.out.print("Enter status (e.g., active, paused): ");
        String status = scanner.nextLine();

        String sql = """
        INSERT INTO fitnessgoal (member_id, goal_type, target_weight, start_date, end_date, status)
        VALUES (?, ?, ?, ?, ?, ?)
        ON CONFLICT (member_id)
        DO UPDATE SET 
            goal_type = EXCLUDED.goal_type,
            target_weight = EXCLUDED.target_weight,
            start_date = EXCLUDED.start_date,
            end_date = EXCLUDED.end_date,
            status = EXCLUDED.status;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member_id);
            stmt.setString(2, goalType);
            stmt.setInt(3, Integer.parseInt(targetWeight));
            stmt.setDate(4, java.sql.Date.valueOf(startDate));
            stmt.setDate(5, java.sql.Date.valueOf(endDate));
            stmt.setString(6, status);

            stmt.executeUpdate();

            System.out.println("===== Fitness Goal Saved Successfully =====");

        } catch (Exception e) {
            System.out.println("Error saving fitness goal: " + e.getMessage());
        }
    }

    //=====UPDATE FITNESS GOALS

    public static void updateFitnessGoals(int member_id) {
        System.out.println("\n===== UPDATE FITNESS GOAL =====");
        System.out.println("1. Update goal type");
        System.out.println("2. Update target weight");
        System.out.println("3. Update start date");
        System.out.println("4. Update end date");
        System.out.println("5. Update status");
        System.out.print("Select option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Enter new goal type: ");
                updateGoalField(member_id, "goal_type", scanner.nextLine());
                break;

            case 2:
                System.out.print("Enter new target weight: ");
                updateGoalField(member_id, "target_weight", scanner.nextLine());
                break;

            case 3:
                System.out.print("Enter new start date (YYYY-MM-DD): ");
                updateGoalField(member_id, "start_date", scanner.nextLine());
                break;

            case 4:
                System.out.print("Enter new end date (YYYY-MM-DD): ");
                updateGoalField(member_id, "end_date", scanner.nextLine());
                break;

            case 5:
                System.out.print("Enter new status: ");
                updateGoalField(member_id, "status", scanner.nextLine());
                break;

            default:
                System.out.println("Invalid option.");
        }
    }

    private static void updateGoalField(int member_id, String column, String value) {
        String sql = "UPDATE fitnessgoal SET " + column + " = ? WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (column.equalsIgnoreCase("target_weight")) {
                stmt.setInt(1, Integer.parseInt(value));
            }
            else if (column.equalsIgnoreCase("start_date") || column.equalsIgnoreCase("end_date")) {
                stmt.setDate(1, java.sql.Date.valueOf(value));
            }
            else {
                stmt.setString(1, value);
            }

            stmt.setInt(2, member_id);
            stmt.executeUpdate();

            System.out.println("===== Fitness Goal Updated =====");

        } catch (Exception e) {
            System.out.println("Error updating goal: " + e.getMessage());
        }
    }



    private static void viewFitnessGoal(int member_id) {
        String sql = "SELECT * FROM fitnessgoal WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member_id);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== CURRENT FITNESS GOAL =====");

            if (rs.next()) {
                System.out.printf("Goal Type:        %s\n", rs.getString("goal_type"));
                System.out.printf("Target Weight:    %s\n", rs.getString("target_weight"));
                System.out.printf("Start Date:       %s\n", rs.getString("start_date"));
                System.out.printf("End Date:         %s\n", rs.getString("end_date"));
                System.out.printf("Status:           %s\n", rs.getString("status"));
            } else {
                System.out.println("No fitness goal found.");
            }

        } catch (Exception e) {
            System.out.println("Error viewing fitness goal: " + e.getMessage());
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

    //classReg
    public static void classRegistrationMenu(int member_id) {
        System.out.println("\n===== CLASS REGISTRATION =====");
        System.out.println("1. View Available Classes");
        System.out.println("2. Register For a Class");
        System.out.println("3. View my upcoming classes");

        System.out.print("Choose: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) ClassRegistration.viewAvailableClasses();
        else if (choice == 2) ClassRegistration.registerForClass(member_id);
        else if (choice == 3) ClassRegistration.viewMyClasses(member_id);
        else System.out.println("Invalid option.");
    }


    //using the view made inthe schema to make the dashboard
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
            System.out.printf("Gender:             %s\n\n", safeString(rs, "gender"));

            System.out.println("----- Latest Health Metrics -----");
            System.out.printf("Latest Weight:      %s\n", safeNumber(rs, "latest_weight"));
            System.out.printf("Latest Heartrate:   %s\n", safeNumber(rs, "latest_heartrate"));
            System.out.printf("Latest Body Fat %%:  %s\n\n", safeNumber(rs, "latest_bodyfat"));

            System.out.println("----- Active Fitness Goal -----");
            System.out.printf("Goal Type:          %s\n", safeString(rs, "active_goal_type"));
            System.out.printf("Target Weight:      %s\n", safeString(rs, "active_goal_target"));
            System.out.printf("Goal Start Date:    %s\n", safeString(rs, "goal_start_date"));
            System.out.printf("Goal End Date:      %s\n", safeString(rs, "goal_end_date"));
            System.out.printf("Goal Status:        %s\n\n", safeString(rs, "goal_status"));

            System.out.println("----- Activity Summary -----");
            System.out.printf("Past Classes:       %d\n", rs.getInt("past_class_count"));
            System.out.printf("Upcoming Classes:   %d\n", rs.getInt("upcoming_class_count"));
            System.out.printf("Total Classes:      %d\n", rs.getInt("total_class_count"));
            System.out.printf("Next Class Date:    %s\n", safeDate(rs, "next_class_date"));
            System.out.printf("Next PT Session:    %s\n", safeDate(rs, "next_pt_session_date"));

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

    private static String safeDate(ResultSet rs, String col) throws SQLException {
        Date d = rs.getDate(col);
        return (d == null) ? "—" : d.toString();
    }











}
