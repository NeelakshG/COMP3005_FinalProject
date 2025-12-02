package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class ClassRegistration {

    private static Scanner scanner = new Scanner(System.in);

    //show all classes that are not full and not in the past
    public static void viewAvailableClasses() {

        //sqlquery to pull classes with capacity count
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

            //loopthroughresults
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

    //register a member for a class
    public static void registerForClass(int member_id) {

        System.out.print("Enter class ID to register: ");
        int class_id = scanner.nextInt();

        //sqlquery to check class capacity before inserting
        String capacityCheck = """
            SELECT capacity,
                   (SELECT COUNT(*) FROM classregistration WHERE class_id = ?) AS enrolled
            FROM class WHERE class_id = ?;
        """;

        //sqlinsert to register class
        String insertSql = """
            INSERT INTO classregistration (member_id, class_id)
            VALUES (?, ?);
        """;

        try (Connection conn = DBConnection.getConnection()) {

            //check if class exists and has space
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

                //classfullcheck
                if (enrolled >= capacity) {
                    System.out.println("Class is full. Cannot register.");
                    return;
                }
            }

            //insertregistration
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setInt(1, member_id);
                stmt.setInt(2, class_id);
                stmt.executeUpdate();

                System.out.println("Successfully registered for class!");

            } catch (SQLException ex) {

                //checkforduplicateentry
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

    //view all classes a member is registered for
    public static void viewMyClasses(int memberId) {

        //sqlquery to get user classes joined with trainer and room
        String sql = """
        SELECT 
            c.class_id,
            c.name AS class_name,
            c.date,
            c.start_time,
            c.end_time,
            t.name AS trainer,
            r.name AS room
        FROM classregistration cr
        JOIN class c ON c.class_id = cr.class_id
        JOIN trainer t ON t.trainer_id = c.trainer_id
        JOIN room r ON r.room_id = c.room_id
        WHERE cr.member_id = ?
        ORDER BY c.date, c.start_time;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== MY REGISTERED CLASSES =====");
            System.out.printf("%-5s %-20s %-12s %-10s %-10s %-15s %-15s\n",
                    "ID", "Class", "Date", "Start", "End", "Trainer", "Room");

            //loopthrough results and print class info
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-12s %-10s %-10s %-15s %-15s\n",
                        rs.getInt("class_id"),
                        rs.getString("class_name"),
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("trainer"),
                        rs.getString("room"));
            }

        } catch (Exception e) {
            System.out.println("Error loading classes: " + e.getMessage());
        }
    }

}
