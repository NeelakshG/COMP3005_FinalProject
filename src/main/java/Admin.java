import java.sql.*;

public class Admin {

    public void printAdmins() {
        String query = "SELECT * FROM administrativestaff";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("=== Admin Staff ===");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("admin_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("email") + " | " +
                                rs.getString("specialization")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
