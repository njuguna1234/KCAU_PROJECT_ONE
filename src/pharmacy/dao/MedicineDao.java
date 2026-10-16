package pharmacy.dao;

import pharmacy.Db;
import pharmacy.model.Medicine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDao {

    private Medicine fromRow(ResultSet rs)
            throws SQLException {

        return new Medicine(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getBigDecimal("price"),
            rs.getInt("quantity"),
            rs.getDate("expiry_date").toLocalDate(),
            rs.getInt("low_stock_level")
        );
    }

    // View all medicines or search by name/category.
    public List<Medicine> findAll(String search)
            throws SQLException {

        List<Medicine> list = new ArrayList<>();

        String sql =
            "SELECT * FROM medicines " +
            "WHERE name LIKE ? OR category LIKE ? " +
            "ORDER BY name";

        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            String term = "%" +
                (search == null ? "" : search.trim()) + "%";

            p.setString(1, term);
            p.setString(2, term);

            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    list.add(fromRow(r));
                }
            }
        }

        return list;
    }

    // Add a new medicine or update an existing one.
    public void save(Medicine m) throws SQLException {

        if (m.getId() <= 0) {
            String sql =
                "INSERT INTO medicines " +
                "(name, category, price, quantity, " +
                "expiry_date, low_stock_level) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection c = Db.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {

                bind(p, m);
                p.executeUpdate();
            }

        } else {
            String sql =
                "UPDATE medicines SET name=?, category=?, " +
                "price=?, quantity=?, expiry_date=?, " +
                "low_stock_level=? WHERE id=?";

            try (Connection c = Db.getConnection();
                 PreparedStatement p = c.prepareStatement(sql)) {

                bind(p, m);
                p.setInt(7, m.getId());

                if (p.executeUpdate() == 0) {
                    throw new SQLException(
                        "Medicine record no longer exists."
                    );
                }
            }
        }
    }

    // Set the medicine values in a prepared statement.
    private void bind(
            PreparedStatement p,
            Medicine m) throws SQLException {

        p.setString(1, m.getName().trim());
        p.setString(2, m.getCategory().trim());
        p.setBigDecimal(3, m.getPrice());
        p.setInt(4, m.getQuantity());
        p.setDate(5, Date.valueOf(m.getExpiryDate()));
        p.setInt(6, m.getLowStockLevel());
    }

    // Delete a medicine using its ID.
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM medicines WHERE id=?";

        try (Connection c = Db.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, id);

            if (p.executeUpdate() == 0) {
                throw new SQLException(
                    "Medicine record was not found."
                );
            }
        }
    }
}