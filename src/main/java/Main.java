import java.sql.*;
import java.util.Scanner;

import Admin_Related_Functions.Admin;
import Admin_Related_Functions.ClassManagement;
import Member_Related_Functions.Member;
import db.DBConnection;
import Member_Related_Functions.Profile;
import Member_Related_Functions.PTSession;
import Trainer_Related_Functions.Trainer;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== GYM SYSTEM =====");
            System.out.println("1. Member Login");
            System.out.println("2. Trainer login");
            System.out.println("3. Admin login");
            System.out.println("4. Create New Member Account");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    int memberId = memberLogin();
                    if (memberId > 0) {
                        memberMenu(memberId);
                    }
                    break;

                case 2:
                    int trainerId = trainerLogin();
                    if (trainerId > 0) {
                        trainerMenu(trainerId);
                    }
                    break;
                case 4:
                    int newMemberId = Member.createMemberAccount();
                    if (newMemberId > 0) {
                        System.out.println("Account created! Please login.\n");
                    }
                    break;
                case 3:
                    int adminId = adminLogin();
                    if (adminId >0) {
                        adminMenu(adminId);
                    }
                case 5:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    //===========================================================
    // LOGIN FUNCTION (FIXED)
    //===========================================================

    private static int memberLogin() {

        scanner.nextLine(); // clear leftover newline

        System.out.println("\n===== MEMBER LOGIN =====");
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        String sql =
                "SELECT member_id, first_name, last_name, email, password " +
                        "FROM member WHERE LOWER(email) = LOWER(?) AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                int memberId = rs.getInt("member_id");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");

                System.out.println("Welcome back, " + name + " (ID: " + memberId + ")");
                return memberId;
            }

            System.out.println("Invalid email or password.");
            return -1;

        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            return -1;
        }
    }

    public static Integer trainerLogin() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Trainer ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        String sql = "SELECT trainer_id FROM trainer WHERE trainer_id = ? AND email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful. Welcome Trainer!");
                return id;
            } else {
                System.out.println("Invalid trainer ID or email.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return -1;
        }
    }

    public static Integer adminLogin() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Admin ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        String sql = "SELECT admin_id FROM administrativestaff WHERE admin_id = ? AND email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful. Welcome Admin!");
                return id;
            } else {
                System.out.println("Invalid admin ID or email.");
                return -1;
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return -1;
        }
    }



    //===========================================================
    // MEMBER MENU
    //===========================================================

    private static void memberMenu(int memberId) {
        while (true) {
            System.out.println("\n===== MEMBER MENU =====");
            System.out.println("1. View Dashboard");
            System.out.println("2. Update Profile Info");
            System.out.println("3. Manage Fitness Goals");
            System.out.println("4. Health Metrics");
            System.out.println("5. Group Class Registration");
            System.out.println("6. PT Session scheduling");

            System.out.println("7. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Profile.viewDashboard(memberId);
                    break;
                case 2:
                    Profile.updateInformationDisplay(memberId);
                    break;
                case 3:
                    Profile.fitnessGoals(memberId);
                    break;
                case 4:
                    Profile.healthMetrics(memberId);
                    break;
                case 5:
                    Profile.classRegistrationMenu(memberId);
                    break;
                case 6:
                    PTSession.managePTSessions(memberId);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
        public static void trainerMenu(int trainerId) {
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\n===== TRAINER MENU =====");
                System.out.println("1. Set Availability");
                System.out.println("2. View My Availability");
                System.out.println("3. View available Rooms");
                System.out.println("4. View My Schedule (PT + Classes)");
                System.out.println("5. Member Lookup");
                System.out.println("6. Logout");
                System.out.print("Choose: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: Trainer.availabilityMenu(trainerId); break;
                    case 2: Trainer.viewAvailability(trainerId); break;
                    case 3: Trainer.viewAvailableRooms(); break;
                    case 4: Trainer.viewTrainerSchedule(trainerId); break;
                    case 5: Trainer.viewMemberProfile(); break;
                    case 6: { return; }
                    default: System.out.println("Invalid option");
                }
            }
        }

    public static void adminMenu(int adminId) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Room Booking");              // Assign rooms for classes or PT sessions
            System.out.println("2. Equipment Maintenance");      // Log and track issues
            System.out.println("3. Class Management");           // Create classes, assign trainers, rooms, times
            System.out.println("4. Logout");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    Admin.roomBookingMenu(adminId);
                    break;

                case 2:
                    Admin.equipmentMaintenanceMenu(adminId);
                    break;

                case 3:
                    ClassManagement.classManagementMenu();
                    break;

                case 4:
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    }











