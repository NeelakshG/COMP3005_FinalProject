package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class PTSession {

    private static final Scanner scanner = new Scanner(System.in);

    // ============================================================
    //                     MAIN PT MENU
    // ============================================================
    public static void managePTSessions(int memberId) {

        while (true) {
            System.out.println("\n===== PERSONAL TRAINING SESSIONS =====");
            System.out.println("1. View Available Trainers");
            System.out.println("2. View Trainer Availability");
            System.out.println("3. Book PT Session");
            System.out.println("4. Reschedule PT Session");
            System.out.println("5. View My PT Sessions");
            System.out.println("6. Cancel PT Session");
            System.out.println("7. Back");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: viewTrainers(); break;
                case 2: viewTrainerAvailability(); break;
                case 3: bookSession(memberId); break;
                case 4: reschedulePT(memberId); break;
                case 5: viewMySessions(memberId); break;
                case 6: cancelPT(memberId); break;
                case 7: return;
                default: System.out.println("invalid choice");
            }
        }
    }

    // ============================================================
    //                  VIEW TRAINERS
    // ============================================================
    private static void viewTrainers() {
        String sql = "SELECT trainer_id, name, specialization FROM Trainer";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== TRAINERS =====");
            System.out.printf("%-10s %-20s %-20s\n", "ID", "Name", "Specialization");

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s\n",
                        rs.getInt("trainer_id"),
                        rs.getString("name"),
                        rs.getString("specialization"));
            }

        } catch (Exception e) {
            System.out.println("Error loading trainers: " + e.getMessage());
        }
    }

    // ============================================================
    //               VIEW TRAINER AVAILABILITY
    // ============================================================
    private static void viewTrainerAvailability() {

        System.out.print("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();
        scanner.nextLine();

        String sql = """
            SELECT day, start_time, end_time
            FROM TrainerAvailability
            WHERE trainer_id = ?
            ORDER BY start_time;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== TRAINER AVAILABILITY =====");
            System.out.printf("%-12s %-10s %-10s\n", "Day", "Start", "End");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-12s %-10s %-10s\n",
                        rs.getString("day"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"));
            }

            if (!found)
                System.out.println("No availability found.");

        } catch (Exception e) {
            System.out.println("Error loading availability: " + e.getMessage());
        }
    }

    // ============================================================
    //                      BOOK PT SESSION
    // ============================================================
    public static void bookSession(int memberId) {

        try (Connection conn = DBConnection.getConnection()) {

            System.out.print("Enter trainer ID: ");
            int trainerId = scanner.nextInt();

            System.out.print("Enter room ID: ");
            int roomId = scanner.nextInt();

            scanner.nextLine(); // clear newline
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            System.out.print("Enter start time (HH:MM): ");
            String start = scanner.nextLine();

            System.out.print("Enter end time (HH:MM): ");
            String end = scanner.nextLine();

            String sql = """
                INSERT INTO PTSession (member_id, trainer_id, room_id, date, start_time, end_time)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, memberId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, roomId);
            stmt.setDate(4, Date.valueOf(date));
            stmt.setTime(5, Time.valueOf(start + ":00"));
            stmt.setTime(6, Time.valueOf(end + ":00"));

            stmt.executeUpdate();
            System.out.println("PT session booked!");

        } catch (SQLException e) {
            System.out.println("Could not book session: " + e.getMessage());
        }
    }


    // ============================================================
    //                   RESCHEDULE PT SESSION
    // ============================================================
    private static void reschedulePT(int memberId) {

        viewMySessions(memberId);

        int sessionId = askInt("\nEnter the Session ID to reschedule: ");

        System.out.print("New Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

        System.out.print("New Start Time (HH:MM): ");
        String startStr = scanner.nextLine();

        System.out.print("New End Time (HH:MM): ");
        String endStr = scanner.nextLine();

        // Check conflicts
        if (memberConflict(memberId, dateStr, startStr, endStr)) {
            System.out.println("❌ You already have a session during this time.");
            return;
        }

        // update
        String sql = """
            UPDATE PTSession
            SET date = ?, start_time = ?, end_time = ?
            WHERE session_id = ? AND member_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(dateStr));
            stmt.setTime(2, Time.valueOf(startStr + ":00"));
            stmt.setTime(3, Time.valueOf(endStr + ":00"));
            stmt.setInt(4, sessionId);
            stmt.setInt(5, memberId);

            int updated = stmt.executeUpdate();
            if (updated > 0)
                System.out.println("✅ Session Rescheduled!");
            else
                System.out.println("❌ Invalid Session ID");

        } catch (Exception e) {
            System.out.println("Reschedule error: " + e.getMessage());
        }
    }

    // ============================================================
    //           VIEW MEMBER PT SESSIONS
    // ============================================================
    public static void viewMySessions(int memberId) {

        String sql = """
            SELECT session_id, date, start_time, end_time,
                   t.name AS trainer, r.name AS room
            FROM PTSession ps
            JOIN Trainer t ON t.trainer_id = ps.trainer_id
            JOIN Room r ON r.room_id = ps.room_id
            WHERE member_id = ?
            ORDER BY date, start_time;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== MY PT SESSIONS =====");
            System.out.printf("%-10s %-12s %-10s %-10s %-20s %-20s\n",
                    "ID", "Date", "Start", "End", "Trainer", "Room");

            while (rs.next()) {
                System.out.printf("%-10d %-12s %-10s %-10s %-20s %-20s\n",
                        rs.getInt("session_id"),
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("trainer"),
                        rs.getString("room"));
            }

        } catch (Exception e) {
            System.out.println("Error loading sessions: " + e.getMessage());
        }
    }

    // ============================================================
    //                  CANCEL SESSION
    // ============================================================
    private static void cancelPT(int memberId) {

        viewMySessions(memberId);

        int sessionId = askInt("\nEnter the Session ID to cancel: ");

        String sql = "DELETE FROM PTSession WHERE session_id = ? AND member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);

            if (stmt.executeUpdate() > 0)
                System.out.println("🗑 Session cancelled.");
            else
                System.out.println("❌ Invalid Session ID.");

        } catch (Exception e) {
            System.out.println("Error canceling session: " + e.getMessage());
        }
    }

    // ============================================================
    //             ROOM CONFLICT CHECK
    // ============================================================
    private static boolean roomConflict(int roomId, String date, String start, String end) {

        String sql = """
            SELECT 1 FROM PTSession
            WHERE room_id = ? AND date = ?
              AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(start + ":00"));
            stmt.setTime(4, Time.valueOf(end + ":00"));

            return stmt.executeQuery().next();

        } catch (Exception e) {
            System.out.println("Room conflict error: " + e.getMessage());
            return true;
        }
    }

    // ============================================================
    //           MEMBER TIME CONFLICT CHECK
    // ============================================================
    private static boolean memberConflict(int memberId, String date, String start, String end) {

        String sql = """
            SELECT 1 FROM PTSession
            WHERE member_id = ?
            AND date = ?
            AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(start + ":00"));
            stmt.setTime(4, Time.valueOf(end + ":00"));

            return stmt.executeQuery().next();

        } catch (Exception e) {
            System.out.println("Conflict check failed: " + e.getMessage());
            return true;
        }
    }

    // ============================================================
    //                      UTIL
    // ============================================================
    private static int askInt(String msg) {
        System.out.print(msg);
        int num = scanner.nextInt();
        scanner.nextLine();
        return num;
    }
}
