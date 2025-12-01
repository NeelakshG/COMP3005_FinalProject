package Admin_Related_Functions;
import db.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class Admin {

    public static Scanner sc = new Scanner(System.in);

    public static void roomBookingMenu(int adminId) {

        while (true) {
            System.out.println("\n===== ROOM BOOKING MENU =====");
            System.out.println("1. View All Rooms");
            System.out.println("2. Check Room Availability");
            System.out.println("3. Assign Room to Class");
            System.out.println("4. Assign Room to PT Session");
            System.out.println("5. Back");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewAllRooms();
                case 2 -> checkRoomAvailability();
                case 3 -> assignRoomToClass();
                case 4 -> assignRoomToPT();
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void viewAllRooms() {
        String sql = "SELECT room_id, name, capacity FROM room ORDER BY room_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== ALL ROOMS =====");
            System.out.printf("%-10s %-20s %-10s\n", "ID", "Name", "Capacity");

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-10d\n",
                        rs.getInt("room_id"),
                        rs.getString("name"),
                        rs.getInt("capacity"));
            }

        } catch (Exception e) {
            System.out.println("Error loading rooms: " + e.getMessage());
        }
    }

    static void assignRoomToClass() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Class ID: ");
        int classId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Date (YYYY-MM-DD): ");
        Date date = Date.valueOf(sc.nextLine());

        System.out.print("Start Time (HH:MM): ");
        Time start = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("End Time (HH:MM): ");
        Time end = Time.valueOf(sc.nextLine() + ":00");

        if (roomOverlap(roomId, date, start, end)) {
            System.out.println("Room is already booked at that time.");
            return;
        }

        String sql = """
        UPDATE class 
        SET room_id = ?, date = ?, start_time = ?, end_time = ?
        WHERE class_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);
            stmt.setInt(5, classId);
            stmt.executeUpdate();

            System.out.println("✔ Room assigned to class!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    private static void checkRoomAvailability() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter date (YYYY-MM-DD): ");
        Date date = Date.valueOf(sc.nextLine());

        System.out.println("\n===== ROOM SCHEDULE =====");
        String sql = """
        SELECT date, start_time, end_time, 
        CONCAT('Class: ', name) AS type
        FROM class
        WHERE room_id = ? AND date = ?
        
        UNION ALL
        
        SELECT date, start_time, end_time,
        'PT Session' AS type
        FROM ptsession
        WHERE room_id = ? AND date = ?
        
        ORDER BY start_time
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setInt(3, roomId);
            stmt.setDate(4, date);

            ResultSet rs = stmt.executeQuery();

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.printf(
                        "%s  %s - %s  | %s\n",
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("type")
                );
            }

            if (empty) {
                System.out.println("Room is FREE all day!");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Returns true if the room is already booked by ANY class or PT
// on that date in the [start, end) interval.
    private static boolean roomOverlap(int roomId, Date date, Time start, Time end) {
        String sql = """
        SELECT 1
        FROM (
            SELECT room_id, date, start_time, end_time FROM class
            UNION ALL
            SELECT room_id, date, start_time, end_time FROM ptsession
        ) AS s
        WHERE s.room_id = ?
          AND s.date = ?
          AND (? < s.end_time AND ? > s.start_time)
        LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // new interval [start, end) overlaps existing when:
            // start < existing_end AND end > existing_start
            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setTime(3, start); // ? < s.end_time
            stmt.setTime(4, end);   // ? > s.start_time

            return stmt.executeQuery().next();   // true = conflict exists

        } catch (Exception e) {
            System.out.println("Error checking room overlap: " + e.getMessage());
            // safer to assume conflict on error
            return true;
        }
    }


    private static void assignRoomToPT() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter PT Session ID: ");
        int ptId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Date (YYYY-MM-DD): ");
        Date date = Date.valueOf(sc.nextLine());

        System.out.print("Start Time (HH:MM): ");
        Time start = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("End Time (HH:MM): ");
        Time end = Time.valueOf(sc.nextLine() + ":00");

        if (roomOverlap(roomId, date, start, end)) {
            System.out.println("Room is already booked at that time.");
            return;
        }

        String sql = """
        UPDATE ptsession 
        SET room_id = ?, date = ?, start_time = ?, end_time = ?
        WHERE session_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);
            stmt.setInt(5, ptId);
            stmt.executeUpdate();

            System.out.println("✔ Room assigned to PT session!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void equipmentMaintenanceMenu(int adminId) {
        while (true) {
            System.out.println("\n===== EQUIPMENT MAINTENANCE MENU =====");
            System.out.println("1. View Equipment Menu");
            System.out.println("2. View All Maintenance Issue Log");
            System.out.println("3. Log Maintenance Issue");
            System.out.println("4. Update Maintenance Operational Status");
            System.out.println("5. Back");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> Equipment.equipmentMenu();
                case 2 -> viewAllMaintenanceIssueLog();
                case 3 -> logMaintenanceIssue(adminId);
                case 4 -> updateMaintenanceStatus(adminId);
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    public static void viewAllMaintenanceIssueLog() {
        String sql = "SELECT log_id, equipment_id, admin_id, issue_reported, report_date, resolve_date, log_status FROM MaintenanceLog ORDER BY log_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== ALL MAINTENANCE LOGS =====");
            System.out.printf("%-10s %-20s %-10s %-20s %-20s %-20s %-10s\n", "Log Id", "Equipment Id", "Admin Id", "Issue Reported", "Report Date",
                    "Resolve Date", "Status");

            while (rs.next()) {
                System.out.printf("%-10s %-20s %-10s %-20s %-20s %-20s %-10s\n",
                        rs.getInt("log_id"),
                        rs.getInt("equipment_id"),
                        rs.getInt("admin_id"),
                        rs.getString("issue_reported"),
                        rs.getString("report_date"),
                        rs.getString("resolve_date"),
                        rs.getString("log_status")
                );
            }

        } catch (Exception e) {
            System.out.println("Error loading maintenance logs: " + e.getMessage());
        }
    }

    public static void logMaintenanceIssue(int adminId) {
        System.out.println("\n===== LOG MAINTENANCE ISSUE =====");

        System.out.print("Equipment ID: ");
        int equipmentId = sc.nextInt();
        sc.nextLine();

        System.out.print("Description of issue: ");
        String description = sc.nextLine();

        System.out.print("Status (OPEN, IN_PROGRESS, COMPLETED): ");
        String logStatus = sc.nextLine();

        String sql = "INSERT INTO MaintenanceLog (equipment_id, admin_id, issue_reported, log_status) "
                + "VALUES (?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipmentId);
            stmt.setInt(2, adminId);
            stmt.setString(3, description);
            stmt.setString(4, logStatus);
            stmt.executeUpdate();

            System.out.println("Maintenance issue added!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateMaintenanceStatus(int adminId) {
        System.out.println("\n===== UPDATE MAINTENANCE STATUS =====");

        System.out.print("Maintenance Log ID: ");
        int maintenanceId = sc.nextInt();
        sc.nextLine();

        System.out.print("New status (OPEN, IN_PROGRESS, COMPLETED): ");
        String new_status = sc.nextLine().toUpperCase();

        String sql = """
                UPDATE MaintenanceLog SET log_status = ?,
                                          admin_id = ?,
                                          resolve_date = CASE
                                          WHEN ? = 'COMPLETED' THEN CURRENT_DATE
                                          ELSE NULL
                                          END
                                          WHERE log_id = ?;""";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, new_status);
            stmt.setInt(2, adminId);
            stmt.setString(3, new_status);
            stmt.setInt(4, maintenanceId);

            int updated = stmt.executeUpdate();
            if (updated == 0) {
                System.out.println("Maintenance record not found.");
            } else {
                System.out.println("Status updated successfully!");
            }

        } catch (Exception e) {
            System.out.println("Error updating maintenance: " + e.getMessage());
        }
    }

}

