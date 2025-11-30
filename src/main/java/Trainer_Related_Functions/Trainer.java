package Trainer_Related_Functions;

import db.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

