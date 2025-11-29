import java.sql.*;
import java.util.Scanner;

public class Member {

    private Scanner scanner = new Scanner(System.in);

    //print the table of all the members
    public void printMembers() {
        String query = "SELECT * FROM member";

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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //1. User Registration: Create a new member with unique email and basic profile info.

    public void registerMember() {

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

        //since we in
    }


}
