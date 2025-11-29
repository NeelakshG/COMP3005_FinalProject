
import java.sql.*;

public class Trainer {

    public void printTrainers() {
        String query = "SELECT * FROM trainer";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("=== Trainers ===");

            while (rs.next()) {
                System.out.println(
                        rs.getInt("trainer_id") + " | " +
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
