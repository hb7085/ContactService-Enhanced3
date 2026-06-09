package contactapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContactDAO {

    public void insertContact(Contact contact) {
        String sql = "INSERT INTO contacts (id, firstName, lastName, phone, address) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getContactId());
            stmt.setString(2, contact.getFirstName());
            stmt.setString(3, contact.getLastName());
            stmt.setString(4, contact.getPhone());
            stmt.setString(5, contact.getAddress());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new InvalidContactException("Failed to insert contact: " + e.getMessage());
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>();
        String sql = "SELECT * FROM contacts";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Contact(
                        rs.getString("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getString("address")
                ));
            }

        } catch (SQLException e) {
            throw new InvalidContactException("Failed to load contacts: " + e.getMessage());
        }

        return list;
    }

    public void updateContact(Contact contact) {
        String sql = "UPDATE contacts SET firstName=?, lastName=?, phone=?, address=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getLastName());
            stmt.setString(3, contact.getPhone());
            stmt.setString(4, contact.getAddress());
            stmt.setString(5, contact.getContactId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new InvalidContactException("Failed to update contact: " + e.getMessage());
        }
    }

    public void deleteContact(String id) {
        String sql = "DELETE FROM contacts WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new InvalidContactException("Failed to delete contact: " + e.getMessage());
        }
    }
}
