package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class ClassRegistration {

    private static Scanner scanner = new Scanner(System.in);

    // Show all classes not full & in the future
    public static void viewAvailableClasses() {
        String sql = """
            SELECT c.class_id, c.name, c.date, c.start_time, c.end_time,
                   r.name AS room_name, c.capacity,
                   (SELECT COUNT(*) FROM classregistration cr WHERE cr.class_id = c.class_id) AS enrolled
            FROM class c
            JOIN room r ON c.room_id = r.room_id
            WHERE c.date >= CURRENT_DATE
            ORDER BY c.date, c.start_time;
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n===== AVAILABLE CLASSES =====");
            System.out.printf("%-5s %-15s %-12s %-10s %-10s %-12s %-8s %-10s\n",
                    "ID", "Name", "Date", "Start", "End", "Room", "Cap", "Enrolled");

            while (rs.next()) {
                System.out.printf("%-5d %-15s %-12s %-10s %-10s %-12s %-8d %-10d\n",
                        rs.getInt("class_id"),
                        rs.getString("name"),
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("room_name"),
                        rs.getInt("capacity"),
                        rs.getInt("enrolled"));
            }

        } catch (Exception e) {
            System.out.println("Error loading classes: " + e.getMessage());
        }
    }

    // Register for class
    public static void registerForClass(int member_id) {

        System.out.print("Enter class ID to register: ");
        int class_id = scanner.nextInt();

        String capacityCheck = """
            SELECT capacity,
                   (SELECT COUNT(*) FROM classregistration WHERE class_id = ?) AS enrolled
            FROM class WHERE class_id = ?;
        """;

        String insertSql = """
            INSERT INTO classregistration (member_id, class_id)
            VALUES (?, ?);
        """;

        try (Connection conn = DBConnection.getConnection()) {

            // Check capacity
            try (PreparedStatement stmt = conn.prepareStatement(capacityCheck)) {
                stmt.setInt(1, class_id);
                stmt.setInt(2, class_id);

                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Invalid class ID.");
                    return;
                }

                int capacity = rs.getInt("capacity");
                int enrolled = rs.getInt("enrolled");

                if (enrolled >= capacity) {
                    System.out.println("Class is full. Cannot register.");
                    return;
                }
            }

            // Try registering
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, member_id);
                stmt.setInt(2, class_id);
                stmt.executeUpdate();

                System.out.println("Successfully registered for class!");

            } catch (SQLException ex) {
                if (ex.getMessage().contains("unique_class_signup")) {
                    System.out.println("You are already registered for this class.");
                } else {
                    System.out.println("Error registering: " + ex.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewMyClasses(int memberId) {
        String sql = """
        SELECT 
            cr.registration_id,
            c.name AS class_name,
            c.date,
            c.start_time,
            c.end_time,
            t.name AS trainer_name,
            r.name AS room_name,
            cr.registered_date
        FROM classregistration cr
        JOIN class c ON cr.class_id = c.class_id
        JOIN trainer t ON c.trainer_id = t.trainer_id
        JOIN room r ON c.room_id = r.room_id
        WHERE cr.member_id = ?
        ORDER BY c.date, c.start_time;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== YOUR REGISTERED CLASSES =====\n");
            System.out.printf("%-5s %-18s %-12s %-10s %-10s %-15s %-10s\n",
                    "ID", "Class Name", "Date", "Start", "End", "Trainer", "Room");

            while (rs.next()) {
                System.out.printf("%-18s %-12s %-10s %-10s %-15s %-10s\n",
                        rs.getString("class_name"),
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("trainer_name"),
                        rs.getString("room_name"));
            }

        } catch (Exception e) {
            System.out.println("Error viewing your classes: " + e.getMessage());
        }
    }


}
