package Admin_Related_Functions;
import db.DBConnection;

import java.sql.*;
import java.util.Scanner;

public class Equipment {

    public static Scanner sc = new Scanner(System.in);

    public static void equipmentMenu() {
        while (true) {
            System.out.println("\n===== EQUIPMENT MENU =====");
            System.out.println("1. Add New Equipment");
            System.out.println("2. View Equipment");
            System.out.println("3. Update Equipment Status");
            System.out.println("4. Back");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: addNewEquipment(); break;
                case 2: viewAllEquipment(); break;
                case 3: updateEquipmentStatus(); break;
                case 4: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    public static void addNewEquipment() {
        System.out.println("\n=== NEW EQUIPMENT ===");

        System.out.print("Room Id (0 for no designated room): ");
        int roomId = sc.nextInt();
        sc.nextLine();

        System.out.print("Equipment Name: ");
        String name = sc.nextLine();

        System.out.print("Type: ");
        String type = sc.nextLine();

        System.out.print("Operational Status (OPERATIONAL, MAINTENANCE_REQUIRED, REMOVE): ");
        String operationalStatus = sc.nextLine().toUpperCase();

        String insertSQL = "INSERT INTO Equipment (room_id, name, type, operational_status) "
                + "VALUES (?, ?, ?, ?);";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            if(roomId == 0) {
                stmt.setNull(1, roomId);

            } else {
                stmt.setInt(1, roomId);
            }
            stmt.setString(2, name);
            stmt.setString(3, type);
            stmt.setString(4, operationalStatus);

            stmt.executeUpdate();
            System.out.println("Successfully added equipment.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewAllEquipment() {
        String sql = "SELECT equipment_id, room_id, name, type, operational_status FROM Equipment ORDER BY equipment_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n===== ALL EQUIPMENT =====");
            System.out.printf("%-20s %-10s %-20s %-20s %-20s\n", "Equipment Id", "Room Id", "Name", "Type", "Operational Status");

            while (rs.next()) {
                Integer room = rs.getObject("room_id", Integer.class);
                System.out.printf("%-20d %-10s %-20s %-20s %-20s\n",
                        rs.getInt("equipment_id"),
                        room  == null ? "N/A" : room,
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getString("operational_status")
                );

            }

        } catch (Exception e) {
            System.out.println("Error loading equipment: " + e.getMessage());
        }
    }

        public static void updateEquipmentStatus() {
            System.out.println("\n===== UPDATE EQUIPMENT STATUS =====");

            System.out.print("Equipment ID: ");
            int equipmentId = sc.nextInt();
            sc.nextLine();

            System.out.print("New operational status (OPERATIONAL, MAINTENANCE_REQUIRED, REMOVE): ");
            String new_status = sc.nextLine().toUpperCase();

            if(new_status.equals("OPERATIONAL") || new_status.equals("MAINTENANCE_REQUIRED")) {
                String sql = """
                UPDATE Equipment SET operational_status = ?
                WHERE equipmment_id = ?
                """;

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, new_status);
                    stmt.setInt(2, equipmentId);

                    int updated = stmt.executeUpdate();
                    if (updated == 0) {
                        System.out.println("Equipment record not found.");
                    } else {
                        System.out.println("✔ Operational status updated successfully!");
                    }

                } catch (Exception e) {
                    System.out.println("Error updating equipment status: " + e.getMessage());
                }

            } else if(new_status.equals("REMOVE")) {
                String deleteQuery = """
                        DELETE FROM Equipment WHERE equipment_id = ?""";

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                    stmt.setInt(1, equipmentId);

                    int updated = stmt.executeUpdate();
                    if (updated == 0) {
                        System.out.println("Equipment record not found.");
                    } else {
                        System.out.println("✔ Equipment removed successfully!");
                    }

                } catch (Exception e) {
                    System.out.println("Error updating equipment status: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid operational status.");
            }


        }
    }



