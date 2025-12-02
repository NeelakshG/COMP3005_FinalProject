package Member_Related_Functions;

import java.sql.*;
import java.util.Scanner;
import db.DBConnection;


public class Member {

    public static Scanner scanner = new Scanner(System.in);

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


    public static int createMemberAccount() {
        System.out.println("\n===== MEMBER REGISTRATION =====");
        scanner.nextLine();

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

        //inserting all the info to a query
        String checkSql = "SELECT member_id FROM member WHERE email = ? OR phone = ?";
        String insertSql =
                "INSERT INTO member (first_name, last_name, email, phone, password, gender) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING member_id";

        try (Connection conn = DBConnection.getConnection()) {

            //validation check, we check
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
                    System.out.println("Account created successfully!");
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

