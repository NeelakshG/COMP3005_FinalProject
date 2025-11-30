package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;


public class Member {

    public static Scanner scanner = new Scanner(System.in);

    //print the table of all the members
    public void printMembers() {
        String query = "SELECT * FROM Member";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("=== Members ===");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("member_id") + " | " +
                                rs.getString("first_name") + " | " +
                                rs.getString("last_name") + " | " +
                                rs.getString("email") + " | " +
                                rs.getString("phone") + " | " +
                                rs.getString("password") + " | " +
                                rs.getString("gender")
                );
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //1. User Registration: Create a new member with unique email and basic profile info.

    //helper function -- checking if email exists

    private static boolean emailExists(String email) {

        String query = "SELECT 1 FROM member WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void registerMember() {

        System.out.println("\n=== MEMBER REGISTRATION ===");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Phone (optional): ");
        String phone = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Gender: ");
        String gender = scanner.nextLine();

        //since we index based on email, we have to check if the email exists
        if (emailExists(email)) {
            System.out.println("Email already exists. Choose another email to create your account");
        }

        String insertSQL = "INSERT INTO member (first_name, last_name, email, phone, password, gender) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, phone.isEmpty() ? null : phone);
            stmt.setString(5, password);
            stmt.setString(6, gender);

            stmt.executeUpdate();
            System.out.println("You have successfully registered your account!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int createMemberAccount() {
        System.out.println("\n===== CREATE NEW MEMBER ACCOUNT =====");

        scanner.nextLine(); // clean leftover newline

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Enter gender: ");
        String gender = scanner.nextLine().trim();

        String checkSql = "SELECT member_id FROM member WHERE email = ? OR phone = ?";
        String insertSql =
                "INSERT INTO member (first_name, last_name, email, phone, password, gender) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING member_id";

        try (Connection conn = DBConnection.getConnection()) {

            // 1) Check if email OR phone already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                checkStmt.setString(2, phone);

                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    System.out.println("An account with this email or phone already exists.");
                    return -1;
                }
            }

            // 2) Insert new member
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, firstName);
                insertStmt.setString(2, lastName);
                insertStmt.setString(3, email);
                insertStmt.setString(4, phone);
                insertStmt.setString(5, password);
                insertStmt.setString(6, gender);

                ResultSet rs = insertStmt.executeQuery();
                if (rs.next()) {
                    int newId = rs.getInt("member_id");
                    System.out.println("🎉 Account created successfully! Your Member ID is: " + newId);
                    return newId;
                }
            }

        } catch (Exception e) {
            System.out.println("Error creating member (full message below):");
            e.printStackTrace();
        }

        return -1;
    }




}

