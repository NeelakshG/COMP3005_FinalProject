package Trainer_Related_Functions;

import db.DBConnection;
import java.util.Scanner;

import java.sql.*;

public class Trainer {
    public static Scanner scanner = new Scanner(System.in);



    public static void setAvailability(int trainerId) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = sc.nextLine();
        Date date = Date.valueOf(dateStr);

        System.out.print("Start time (HH:MM): ");
        Time start = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("End time (HH:MM): ");
        Time end = Time.valueOf(sc.nextLine() + ":00");

        // Check overlap with existing availability
        if (availabilityOverlap(trainerId, date, start, end)) {
            System.out.println("❌ Overlaps with existing availability.");
            return;
        }

        // Check overlap with PT sessions
        if (trainerPTOverlap(trainerId, date, start, end)) {
            System.out.println("❌ Trainer already has a PT session then.");
            return;
        }

        // Check overlap with classes
        if (trainerClassOverlap(trainerId, date, start, end)) {
            System.out.println("❌ Trainer has a class scheduled at that time.");
            return;
        }

        // Insert
        String sql = """
        INSERT INTO traineravailability (trainer_id, date, start_time, end_time)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);
            stmt.setTime(3, start);
            stmt.setTime(4, end);
            stmt.executeUpdate();

            System.out.println("✔ Availability added!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

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
            stmt.setTime(3, end);
            stmt.setTime(4, start);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            return true;
        }
    }

    private static boolean availabilityOverlap(int trainerId, Date date, Time start, Time end) {
        String sql = """
        SELECT 1 FROM traineravailability
        WHERE trainer_id = ? AND date = ?
        AND (? < end_time AND ? > start_time)
        LIMIT 1;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);
            stmt.setTime(3, end);
            stmt.setTime(4, start);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            return true;
        }
    }

    private static boolean trainerClassOverlap(int trainerId, Date date, Time start, Time end) {
        String sql = """
        SELECT 1 FROM class
        WHERE trainer_id = ?
        AND date = ?
        AND (? < end_time AND ? > start_time)
        LIMIT 1;
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setDate(2, date);
            stmt.setTime(3, end);
            stmt.setTime(4, start);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            return true;
        }
    }





    public static void viewTrainerSchedule(int trainerId) {
        String sql = """
        SELECT date, start_time, end_time, 'PT Session' AS type
        FROM ptsession
        WHERE trainer_id = ?
        UNION ALL
        SELECT date, start_time, end_time, CONCAT('Class: ', name)
        FROM class
        WHERE trainer_id = ?
        ORDER BY date, start_time
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            stmt.setInt(2, trainerId);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== TRAINER SCHEDULE =====");
            System.out.printf("%-12s %-12s %-12s %-30s\n",
                    "Date", "Start", "End", "Type");

            while (rs.next()) {
                System.out.printf("%-12s %-12s %-12s %-30s\n",
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"),
                        rs.getString("type"));
            }

        } catch (Exception e) {
            System.out.println("Schedule error: " + e.getMessage());
        }
    }

    public static void viewMemberProfile() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter member full name (case-sensitive): ");
        String fullName = sc.nextLine().trim();

        String sql = """
        SELECT 
            m.member_id,
            m.first_name,
            m.last_name,
            m.email,
            m.phone,
            m.gender,

            -- Latest fitness goal
            (SELECT goal_type 
             FROM fitnessgoal fg
             WHERE fg.member_id = m.member_id
             ORDER BY fg.start_date DESC
             LIMIT 1) AS latest_goal,

            (SELECT target_weight
             FROM fitnessgoal fg
             WHERE fg.member_id = m.member_id
             ORDER BY fg.start_date DESC
             LIMIT 1) AS latest_target_weight,

            (SELECT status
             FROM fitnessgoal fg
             WHERE fg.member_id = m.member_id
             ORDER BY fg.start_date DESC
             LIMIT 1) AS latest_goal_status,

            -- Latest health metrics
            (SELECT weight
             FROM healthmetrics hm
             WHERE hm.member_id = m.member_id
             ORDER BY hm.recorded_at DESC
             LIMIT 1) AS latest_weight,

            (SELECT heartrate
             FROM healthmetrics hm
             WHERE hm.member_id = m.member_id
             ORDER BY hm.recorded_at DESC
             LIMIT 1) AS latest_heartrate,

            (SELECT body_fat_percentage
             FROM healthmetrics hm
             WHERE hm.member_id = m.member_id
             ORDER BY hm.recorded_at DESC
             LIMIT 1) AS latest_bfp

        FROM member m
        WHERE (m.first_name || ' ' || m.last_name) = ?
        LIMIT 1;
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Member not found. (Case-sensitive search)");
                return;
            }

            System.out.println("\n===== MEMBER PROFILE =====");
            System.out.println("ID: " + rs.getInt("member_id"));
            System.out.println("Name: " + rs.getString("first_name") + " " + rs.getString("last_name"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Phone: " + rs.getString("phone"));
            System.out.println("Gender: " + rs.getString("gender"));

            // FITNESS GOAL
            System.out.println("\n--- Latest Fitness Goal ---");
            if (rs.getString("latest_goal") != null) {
                System.out.println("Goal: " + rs.getString("latest_goal"));
                System.out.println("Target Weight: " + rs.getString("latest_target_weight"));
                System.out.println("Status: " + rs.getString("latest_goal_status"));
            } else {
                System.out.println("No fitness goals recorded.");
            }

            // HEALTH METRICS
            System.out.println("\n--- Latest Health Metrics ---");
            if (rs.getString("latest_weight") != null) {
                System.out.println("Weight: " + rs.getDouble("latest_weight"));
                System.out.println("Heart Rate: " + rs.getInt("latest_heartrate"));
                System.out.println("Body Fat %: " + rs.getDouble("latest_bfp"));
            } else {
                System.out.println("No health metrics recorded.");
            }

        } catch (Exception e) {
            System.out.println("Error loading profile: " + e.getMessage());
        }
    }

    public static void availabilityMenu(int trainerId) {

        while (true) {
            System.out.println("\n===== SET AVAILABILITY MENU =====");
            System.out.println("1. Add Availability Window");
            System.out.println("2. Add Class");
            System.out.println("3. Add PT Session");
            System.out.println("4. Back");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    setAvailability(trainerId);
                    break;
                case 2:
                    addClass(trainerId);
                    break;
                case 3:
                    addPTSession(trainerId);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void addClass(int trainerId) {
        System.out.print("Class name: ");
        String name = scanner.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        Date date = Date.valueOf(scanner.nextLine());

        System.out.print("Start time (HH:MM): ");
        Time start = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("End time (HH:MM): ");
        Time end = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("Room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        // Prevent conflicts
        if (trainerPTOverlap(trainerId, date, start, end)) {
            System.out.println("Overlaps with a PT session");
            return;
        }

        if (trainerClassOverlap(trainerId, date, start, end)) {
            System.out.println("Overlaps with another class");
            return;
        }

        if (availabilityOverlap(trainerId, date, start, end)) {
            System.out.println("Overlaps with availability slot");
            return;
        }

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
            stmt.setInt(7, 20);   // default capacity

            stmt.executeUpdate();
            System.out.println("✔ Class added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void addPTSession(int trainerId) {
        System.out.print("Member ID: ");
        int memberId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Date (YYYY-MM-DD): ");
        Date date = Date.valueOf(scanner.nextLine());

        System.out.print("Start time (HH:MM): ");
        Time start = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("End time (HH:MM): ");
        Time end = Time.valueOf(scanner.nextLine() + ":00");

        System.out.print("Room ID: ");
        int roomId = scanner.nextInt();
        scanner.nextLine();

        if (trainerPTOverlap(trainerId, date, start, end)) {
            System.out.println("Trainer already has a PT session");
            return;
        }

        if (trainerClassOverlap(trainerId, date, start, end)) {
            System.out.println("Trainer has a class at that time");
            return;
        }

        if (availabilityOverlap(trainerId, date, start, end)) {
            System.out.println("Overlaps with availability window");
            return;
        }

        String sql = """
        INSERT INTO ptsession (member_id, trainer_id, room_id, date, start_time, end_time)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setInt(2, trainerId);
            stmt.setInt(3, roomId);
            stmt.setDate(4, date);
            stmt.setTime(5, start);
            stmt.setTime(6, end);

            stmt.executeUpdate();
            System.out.println("✔ PT Session added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewAvailableRooms() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter date (YYYY-MM-DD): ");
        Date date = Date.valueOf(sc.nextLine());

        System.out.print("Start time (HH:MM): ");
        Time start = Time.valueOf(sc.nextLine() + ":00");

        System.out.print("End time   (HH:MM): ");
        Time end = Time.valueOf(sc.nextLine() + ":00");

        String sql = """
        SELECT room_id, name
        FROM room
        WHERE room_id NOT IN (

            -- rooms taken by PT sessions
            SELECT room_id
            FROM ptsession
            WHERE date = ?
              AND (? < end_time AND ? > start_time)

            UNION

            -- rooms taken by classes
            SELECT room_id
            FROM class
            WHERE date = ?
              AND (? < end_time AND ? > start_time)
        )
        ORDER BY room_id;
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

            System.out.println("\n===== AVAILABLE ROOMS =====");
            System.out.printf("%-10s %-20s\n", "Room ID", "Room Name");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.printf("%-10d %-20s\n",
                        rs.getInt("room_id"),
                        rs.getString("name"));
            }

            if (!found) {
                System.out.println("No rooms available for that time.");
            }

        } catch (Exception e) {
            System.out.println("Error checking rooms: " + e.getMessage());
        }
    }





    public static void viewAvailability(int trainerId) {
        String sql = """
        SELECT date, start_time, end_time
        FROM traineravailability
        WHERE trainer_id = ?
        ORDER BY date, start_time
    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);

            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== YOUR SET AVAILABILITY =====");
            System.out.printf("%-12s %-12s %-12s\n", "Date", "Start", "End");

            while (rs.next()) {
                System.out.printf("%-12s %-12s %-12s\n",
                        rs.getDate("date"),
                        rs.getTime("start_time"),
                        rs.getTime("end_time"));
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }




}

