import java.sql.*;
import java.util.Scanner;

import Member_Related_Functions.Member;
import db.DBConnection;
import Member_Related_Functions.Profile;
import Member_Related_Functions.PTSession;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== GYM SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Create New Member Account");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    int memberId = login();
                    if (memberId > 0) {
                        memberMenu(memberId);
                    }
                    break;

                case 2:
                    int newMemberId = Member.createMemberAccount();
                    if (newMemberId > 0) {
                        System.out.println("Account created! Please login.\n");
                    }
                    break;

                case 3:
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

    private static int login() {

        scanner.nextLine(); // clear leftover newline

        System.out.println("\n===== MEMBER LOGIN =====");
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        // Fixed select: use lowercase column names EXACTLY as in the database
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

}
