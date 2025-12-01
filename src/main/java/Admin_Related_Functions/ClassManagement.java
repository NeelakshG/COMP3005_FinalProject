package Admin_Related_Functions;

import db.DBConnection;

import java.sql.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Scanner;

public class ClassManagement {

    public static Scanner sc = new Scanner(System.in);

    /* ============================================================
                        MAIN CLASS MANAGEMENT MENU
       ============================================================ */
    public static void classManagementMenu() {

        while (true) {
            System.out.println("\n===== CLASS MANAGEMENT =====");
            System.out.println("1. Create New Class");
            System.out.println("2. Assign Trainer");
            System.out.println("3. Assign Room");
            System.out.println("4. Update Schedule");
            System.out.println("5. View All Classes");
            System.out.println("6. Back");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: createClass(); break;
                case 2: assignTrainerToClass(); break;
                case 3: assignRoomToClass(); break;
                case 4: updateClassSchedule(); break;
                case 5: viewAllClasses(); break;
                case 6: return;
                default: System.out.println("Invalid.");
            }
        }
    }

    /* ============================================================
                            VIEW ALL CLASSES
       ============================================================ */
    private static void viewAllClasses() {
        String sql = "SELECT * FROM ClassOverview";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== ALL CLASSES =====");

            while (rs.next()) {
                System.out.println(
                        "Class ID: " + rs.getInt("class_id") +
                                " | Name: " + rs.getString("class_name") +
                                " | Date: " + rs.getDate("date") +
                                " | Time: " + rs.getTime("start_time") + " - " + rs.getTime("end_time") +
                                " | Trainer: " + rs.getString("trainer_name") +
                                " | Room: " + rs.getString("room_name") +
                                " | Capacity: " + rs.getInt("capacity")
                );
            }

        } catch (Exception e) {
            System.out.println("Error viewing classes: " + e.getMessage());
        }
    }



    /* ============================================================
                        ASSIGN TRAINER TO CLASS
       ============================================================ */
    public static void assignTrainerToClass() {
        System.out.println("\n===== ASSIGN TRAINER TO CLASS =====");

        System.out.print("Enter Class ID: ");
        int classId = sc.nextInt();
        sc.nextLine();

        // Load schedule
        String fetchSql = """
            SELECT date, start_time, end_time 
            FROM class 
            WHERE class_id = ?
        """;

        Date date;
        Time start;
        Time end;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchSql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Class not found.");
                return;
            }

            date = rs.getDate("date");
            start = rs.getTime("start_time");
            end = rs.getTime("end_time");

        } catch (Exception e) {
            System.out.println("Error loading class: " + e.getMessage());
            return;
        }

        System.out.print("Enter Trainer ID: ");
        int trainerId = sc.nextInt();
        sc.nextLine();

        if (!trainerExists(trainerId)) {
            System.out.println("Trainer does not exist.");
            return;
        }

        // Prevent trainer double-booking
        if (!trainerAvailableForClass(trainerId, date, start, end)) {
            System.out.println("Trainer is NOT available during that time.");
            return;
        }

        // Assign trainer
        String updateSql = "UPDATE class SET trainer_id = ? WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, classId);
            stmt.executeUpdate();

            System.out.println("Trainer assigned!");

        } catch (Exception e) {
            System.out.println("Error assigning trainer: " + e.getMessage());
        }
    }

    /* Check Trainer Exists */
    private static boolean trainerExists(int trainerId) {
        String sql = "SELECT 1 FROM trainer WHERE trainer_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            return stmt.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }


    /* ============================================================
                            CREATE CLASS
       ============================================================ */
    public static void createClass() {

        System.out.println("\n===== CREATE NEW CLASS =====");

        System.out.print("Class Name: ");
        String name = sc.nextLine();

        System.out.print("Trainer ID: ");
        int trainerId = sc.nextInt();
        sc.nextLine();

        System.out.print("Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        Date date = Date.valueOf(sc.nextLine());

        System.out.print("Start Time (HH:MM): ");
        Time start = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("End Time (HH:MM): ");
        Time end = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("Capacity: ");
        int capacity = sc.nextInt();
        sc.nextLine();

        // TIME VALIDATION
        if (!start.before(end)) {
            System.out.println("Invalid: Start time must be BEFORE end time.");
            return;
        }

        // TRAINER FREE?
        if (!trainerAvailableForClass(trainerId, date, start, end)) {
            System.out.println("Trainer is NOT available.");
            return;
        }

        // ROOM FREE?
        if (!roomAvailableForClass(roomId, date, start, end)) {
            System.out.println("Room is NOT available.");
            return;
        }

        // INSERT INTO DB
        String sql = """
        INSERT INTO class (name, trainer_id, room_id, date, start_time, end_time, capacity)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, roomId);
            stmt.setDate(4, date);
            stmt.setTime(5, start);
            stmt.setTime(6, end);
            stmt.setInt(7, capacity);

            stmt.executeUpdate();
            System.out.println("✔ Class created!");

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.out.println("ERROR: Class conflict (same trainer/room/date/time).");
            } else {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    /* ============================================================
                        CHECK TRAINER AVAILABILITY
       ============================================================ */
    private static boolean trainerAvailableForClass(int trainerId, Date date, Time start, Time end) {

        String sql = """
            SELECT 1 FROM (
                SELECT date, start_time, end_time FROM ptsession WHERE trainer_id = ?
                UNION ALL
                SELECT date, start_time, end_time FROM class WHERE trainer_id = ?
            ) AS schedule
            WHERE date = ?
            AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, trainerId);
            stmt.setDate(3, date);

            stmt.setTime(4, start);  // CORRECT ORDER
            stmt.setTime(5, end);    // CORRECT ORDER

            return !stmt.executeQuery().next();  // true = trainer free

        } catch (Exception e) {
            return false;
        }
    }

    /* ============================================================
                        CHECK ROOM AVAILABILITY (CREATE)
       ============================================================ */
    private static boolean roomAvailableForClass(int roomId, Date date, Time start, Time end) {

        String sql = """
            SELECT 1 FROM (
                SELECT room_id, date, start_time, end_time FROM ptsession
                UNION ALL
                SELECT room_id, date, start_time, end_time FROM class
            ) AS booking
            WHERE room_id = ?
            AND date = ?
            AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);

            return !stmt.executeQuery().next();  // true = roomFree

        } catch (Exception e) {
            return false;
        }
    }


    /* ============================================================
                            ASSIGN ROOM
       ============================================================ */
    public static void assignRoomToClass() {
        System.out.println("\n===== ASSIGN ROOM TO CLASS =====");

        System.out.print("Enter Class ID: ");
        int classId = sc.nextInt();
        sc.nextLine();

        String fetchSql = """
            SELECT date, start_time, end_time 
            FROM class
            WHERE class_id = ?
        """;

        Date date;
        Time start;
        Time end;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchSql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Class not found.");
                return;
            }

            date = rs.getDate("date");
            start = rs.getTime("start_time");
            end = rs.getTime("end_time");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        System.out.print("Enter Room ID: ");
        int roomId = sc.nextInt();
        sc.nextLine();

        if (!roomExists(roomId)) {
            System.out.println("Room does not exist.");
            return;
        }

        if (!roomAvailableForClass(roomId, date, start, end)) {
            System.out.println("Room is NOT available.");
            return;
        }

        String updateSql = "UPDATE class SET room_id = ? WHERE class_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, roomId);
            stmt.setInt(2, classId);
            stmt.executeUpdate();

            System.out.println("✔ Room assigned!");

        } catch (Exception e) {
            System.out.println("Error assigning room: " + e.getMessage());
        }
    }

    private static boolean roomExists(int roomId) {
        String sql = "SELECT 1 FROM room WHERE room_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            return stmt.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }


    /* ============================================================
                        UPDATE CLASS SCHEDULE
       ============================================================ */
    public static void updateClassSchedule() {

        System.out.println("\n===== UPDATE CLASS SCHEDULE =====");

        System.out.print("Enter Class ID: ");
        int classId = sc.nextInt();
        sc.nextLine();

        String fetchSql = """
            SELECT name, trainer_id, room_id, date, start_time, end_time
            FROM class
            WHERE class_id = ?
        """;

        String oldName;
        int trainerId;
        int roomId;
        Date oldDate;
        Time oldStart;
        Time oldEnd;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(fetchSql)) {

            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Class not found.");
                return;
            }

            oldName = rs.getString("name");
            trainerId = rs.getInt("trainer_id");
            roomId = rs.getInt("room_id");
            oldDate = rs.getDate("date");
            oldStart = rs.getTime("start_time");
            oldEnd = rs.getTime("end_time");

        } catch (Exception e) {
            System.out.println("Error loading class: " + e.getMessage());
            return;
        }

        // Name update
        System.out.print("New Class Name (Enter to keep '" + oldName + "'): ");
        String newName = sc.nextLine().trim();
        if (newName.isEmpty()) newName = oldName;

        // New schedule
        System.out.print("New Date (YYYY-MM-DD): ");
        Date newDate = Date.valueOf(sc.nextLine());

        System.out.print("New Start Time (HH:MM): ");
        Time newStart = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("New End Time (HH:MM): ");
        Time newEnd = Time.valueOf(sc.nextLine() + ":00");

        // Overlap checks
        if (trainerClassOverlap(trainerId, newDate, newStart, newEnd, classId)) {
            System.out.println("Trainer busy with another class.");
            return;
        }

        if (trainerPTOverlap(trainerId, newDate, newStart, newEnd)) {
            System.out.println("Trainer has a PT session.");
            return;
        }

        if (!roomAvailableForClass(roomId, newDate, newStart, newEnd, classId)) {
            System.out.println("Room not free.");
            return;
        }



        // UPDATE
        String updateSql = """
            UPDATE class
            SET name = ?, date = ?, start_time = ?, end_time = ?
            WHERE class_id = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, newName);
            stmt.setDate(2, newDate);
            stmt.setTime(3, newStart);
            stmt.setTime(4, newEnd);
            stmt.setInt(5, classId);

            stmt.executeUpdate();
            System.out.println("✔ Class updated!");

        } catch (Exception e) {
            System.out.println("Error updating class: " + e.getMessage());
        }
    }

    /* ============================================================
                        TRAINER CLASS OVERLAP
       ============================================================ */
    private static boolean trainerClassOverlap(int trainerId, Date date, Time start, Time end, int classId) {

        String sql = """
            SELECT 1 FROM class
            WHERE trainer_id = ?
            AND date = ?
            AND class_id <> ?
            AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);
            stmt.setInt(3, classId);
            stmt.setTime(4, start);
            stmt.setTime(5, end);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            return true;
        }
    }

    /* ============================================================
                        ROOM AVAILABILITY (UPDATE)
       ============================================================ */
    private static boolean roomAvailableForClass(int roomId, Date date, Time start, Time end, int classId) {

        String sql = """
            SELECT 1 FROM class
            WHERE room_id = ?
            AND date = ?
            AND class_id <> ?
            AND (? < end_time AND ? > start_time)

            UNION

            SELECT 1 FROM ptsession
            WHERE room_id = ?
            AND date = ?
            AND (? < end_time AND ? > start_time)
            LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);
            stmt.setDate(2, date);
            stmt.setInt(3, classId);
            stmt.setTime(4, start);
            stmt.setTime(5, end);

            stmt.setInt(6, roomId);
            stmt.setDate(7, date);
            stmt.setTime(8, start);
            stmt.setTime(9, end);

            return !stmt.executeQuery().next();

        } catch (Exception e) {
            return false;
        }
    }

    /* ============================================================
                        TRAINER PT OVERLAP
       ============================================================ */
    private static boolean trainerPTOverlap(int trainerId, Date date, Time start, Time end) {

        String sql = """
            SELECT 1 FROM ptsession
            WHERE trainer_id = ?
            AND date = ?
            AND (? < end_time AND ? > start_time)
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
            return true;
        }
    }

    /* ============================================================
                        DUPLICATE CLASS CHECK
       ============================================================ */
    private static boolean classDuplicate(String name, int trainerId, Date date, Time start, Time end, Integer excludeClassId) {

        String sql = """
            SELECT 1
            FROM class
            WHERE name = ?
            AND trainer_id = ?
            AND date = ?
            AND (? < end_time AND ? > start_time)
        """ +
                (excludeClassId != null ? " AND class_id <> ?" : "") +
                " LIMIT 1;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, trainerId);
            stmt.setDate(3, date);
            stmt.setTime(4, start);
            stmt.setTime(5, end);

            if (excludeClassId != null) {
                stmt.setInt(6, excludeClassId);
            }

            return stmt.executeQuery().next();

        } catch (Exception e) {
            return true;
        }
    }
}
