package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;

public class PTSession {

    private static final Scanner scanner = new Scanner(System.in);
    //MAIN PT MENU

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


    //VIEW TRAINER
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


    //VIEW TRAINER AVAILABILITY
    // ============================================================
    public static void viewTrainerAvailability() {
        System.out.print("Enter Trainer ID: ");
        int trainerId = scanner.nextInt();
        scanner.nextLine();

        String sql = """
        SELECT date, start_time, end_time
        FROM traineravailability
        WHERE trainer_id = ?
        ORDER BY date, start_time;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("\nNo availability found.\n");
                return;
            }

            System.out.println("\n===== TRAINER AVAILABILITY =====\n");

            java.sql.Date currentDate = null;

            while (rs.next()) {
                java.sql.Date date = rs.getDate("date");
                Time start = rs.getTime("start_time");
                Time end = rs.getTime("end_time");

                if (currentDate == null || !currentDate.equals(date)) {
                    currentDate = date;

                    System.out.println("Date: " + date);
                    System.out.println("------------------------------");
                    System.out.printf("%-12s %-12s\n", "Start", "End");
                }

                System.out.printf("%-12s %-12s\n", start, end);
            }

        } catch (Exception e) {
            System.out.println("Error loading availability: " + e.getMessage());
        }
    }

    private static Integer findAvailableRoom(Date date, Time start, Time end) {
        String sql = """
        SELECT room_id
        FROM room
        WHERE room_id NOT IN (
            SELECT room_id FROM ptsession
            WHERE date = ? AND (? < end_time AND ? > start_time)

            UNION

            SELECT room_id FROM class
            WHERE date = ? AND (? < end_time AND ? > start_time)
        )
        LIMIT 1;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, date);
            stmt.setTime(2, start);
            stmt.setTime(3, end);

            stmt.setDate(4, date);
            stmt.setTime(5, start);
            stmt.setTime(6, end);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("room_id");  // Found a free room
            }

            return null; // No room free

        } catch (Exception e) {
            System.out.println("Error finding room: " + e.getMessage());
            return null;
        }
    }





    //
    //                      BOOK PT SESSION
    // ============================================================
    public static void bookSession(int memberId) {

        try (Connection conn = DBConnection.getConnection()) {

            System.out.print("Enter trainer ID: ");
            int trainerId = scanner.nextInt();


            scanner.nextLine(); // clear newline
            System.out.print("Enter date (YYYY-MM-DD): ");
            String date = scanner.nextLine();

            System.out.print("Enter start time (HH:MM): ");
            String start = scanner.nextLine();

            System.out.print("Enter end time (HH:MM): ");
            String end = scanner.nextLine();

            Date d = Date.valueOf(date);
            Time s = Time.valueOf(start + ":00");
            Time e = Time.valueOf(end + ":00");


            if (!withinAvailability(trainerId, d, s, e)) {
                System.out.println("Trainer is NOT scheduled to work at this time.");
                return;
            }

            if (!trainerAvailable(trainerId, Date.valueOf(date), Time.valueOf(start + ":00"), Time.valueOf(end + ":00"))) {
                System.out.println("Trainer is NOT available at this time.");
                return;
            }

            if (memberConflict(memberId, date, start, end)) {
                System.out.println("You have a session at this time.");
                return;
            }

            // NEW: block if member has a class at this time
            if (memberClassConflict(
                    memberId,
                    Date.valueOf(date),
                    Time.valueOf(start + ":00"),
                    Time.valueOf(end + ":00")
            )) {
                System.out.println("You have a CLASS at this time. Cannot book PT.");
                return;
            }

            Integer roomId = findAvailableRoom(
                    Date.valueOf(date),
                    Time.valueOf(start + ":00"),
                    Time.valueOf(end + ":00")
            );

            if (roomId == null) {
                System.out.println("No rooms available at this time. Choose a different time.");
                return;
            }

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

    private static boolean withinAvailability(int trainerId, Date date, Time start, Time end) {
        String sql = """
            SELECT 1 FROM traineravailability
            WHERE trainer_id = ?
              AND date = ?
              AND start_time <= ?
              AND end_time >= ?
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            System.out.println("Error checking scheduled availability: " + e.getMessage());
            return false;
        }
    }

    private static boolean memberClassConflict(int memberId, Date date, Time start, Time end) {

        String sql = """
        SELECT 1
        FROM classregistration cr
        JOIN class c ON c.class_id = cr.class_id
        WHERE cr.member_id = ?
          AND c.date = ?
          AND (? < c.end_time AND ? > c.start_time)
        LIMIT 1;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);

            return stmt.executeQuery().next();   // TRUE = conflict found

        } catch (Exception e) {
            System.out.println("Error checking class conflict: " + e.getMessage());
            return true; // safer: assume conflict if error
        }
    }



    //RESCHEDULE PT SESSION

    private static void reschedulePT(int memberId) {

        viewMySessions(memberId);

        int sessionId = askInt("\nEnter the Session ID to reschedule: ");

        //Get the data
        int trainerId = 0;
        int roomId = 0;

        String fetchSql = "SELECT trainer_id, room_id FROM PTSession WHERE session_id = ? AND member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchSql)) {

            stmt.setInt(1, sessionId);
            stmt.setInt(2, memberId);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Invalid Session ID.");
                return;
            }

            trainerId = rs.getInt("trainer_id");
            roomId = rs.getInt("room_id");

        } catch (Exception e) {
            System.out.println("Error loading session info: " + e.getMessage());
            return;
        }

        //ask for new info
        System.out.print("New Date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

        System.out.print("New Start Time (HH:MM): ");
        String startStr = scanner.nextLine();

        System.out.print("New End Time (HH:MM): ");
        String endStr = scanner.nextLine();

        Date newDate = Date.valueOf(dateStr);
        Time newStart = Time.valueOf(startStr + ":00");
        Time newEnd = Time.valueOf(endStr + ":00");

        //check conflicts
        if (memberClassConflict(memberId, newDate, newStart, newEnd)) {
            System.out.println("You have a CLASS at this time.");
            return;
        }

        if (!trainerAvailable(trainerId, newDate, newStart, newEnd)) {
            System.out.println("Trainer not available.");
            return;
        }

        if (!roomAvailable(roomId, newDate, newStart, newEnd)) {
            System.out.println("Room not available.");
            return;
        }

        // STEP 4 — Update the session
        String updateSql = """
        UPDATE PTSession
        SET date = ?, start_time = ?, end_time = ?
        WHERE session_id = ? AND member_id = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setDate(1, newDate);
            stmt.setTime(2, newStart);
            stmt.setTime(3, newEnd);
            stmt.setInt(4, sessionId);
            stmt.setInt(5, memberId);

            int updated = stmt.executeUpdate();
            if (updated > 0)
                System.out.println("Session Rescheduled!");
            else
                System.out.println("Invalid Session ID");

        } catch (Exception e) {
            System.out.println("Reschedule error: " + e.getMessage());
        }
    }

    //checking trainer availability
    private static boolean trainerAvailable(int trainerId, Date date, Time start, Time end) {
        String sql = """
            SELECT * FROM ptsession
            WHERE trainer_id = ? AND date = ?
            AND (
                (start_time < ? AND end_time > ?) OR
                (start_time < ? AND end_time > ?) OR
                (start_time >= ? AND start_time < ?)
            );
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);

            stmt.setTime(3, end);
            stmt.setTime(4, start);
            stmt.setTime(5, end);
            stmt.setTime(6, start);
            stmt.setTime(7, start);
            stmt.setTime(8, end);

            ResultSet rs = stmt.executeQuery();
            return !rs.next(); // true if no conflicts

        } catch (Exception e) {
            System.out.println("Error checking trainer availability: " + e.getMessage());
            return false;
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
                System.out.println("Invalid Session ID.");

        } catch (Exception e) {
            System.out.println("Error canceling session: " + e.getMessage());
        }
    }

    // ============================================================
    //             ROOM CONFLICT CHECK
    // ============================================================
    private static boolean roomAvailable(int roomId, Date date, Time start, Time end) {
        String sql = """
            SELECT * FROM ptsession
            WHERE room_id = ? AND date = ?
            AND (
                (start_time < ? AND end_time > ?) OR
                (start_time < ? AND end_time > ?) OR
                (start_time >= ? AND start_time < ?)
            );
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);

            stmt.setTime(3, end);
            stmt.setTime(4, start);
            stmt.setTime(5, end);
            stmt.setTime(6, start);
            stmt.setTime(7, start);
            stmt.setTime(8, end);

            ResultSet rs = stmt.executeQuery();
            return !rs.next();

        } catch (Exception e) {
            System.out.println("Error checking room availability: " + e.getMessage());
            return false;
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
