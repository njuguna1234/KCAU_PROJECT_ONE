package pharmacy.ui;

import pharmacy.dao.MedicineDao;
import pharmacy.model.Medicine;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final MedicineDao dao = new MedicineDao();
    private final JTextField search = new JTextField(20);

    private final DefaultTableModel model =
        new DefaultTableModel(
            new Object[]{
                "ID", "Medicine", "Category", "Price (KES)",
                "Quantity", "Expiry Date", "Low-stock level", "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    private final JTable table = new JTable(model);

    public DashboardFrame(String username) {
        setTitle("Pharmacy Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1120, 620);
        setLocationRelativeTo(null);

        // Header
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setBorder(
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        );
        top.setBackground(new Color(22, 75, 94));

        JLabel heading = new JLabel(
            "Pharmacy Management System"
        );
        heading.setForeground(Color.WHITE);
        heading.setFont(
            new Font("SansSerif", Font.BOLD, 22)
        );

        JLabel signedIn = new JLabel(
            "Signed in: " + username + "   "
        );
        signedIn.setForeground(Color.WHITE);

        top.add(heading, BorderLayout.WEST);
        top.add(signedIn, BorderLayout.EAST);

        // Buttons and search field
        JPanel controls = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 10, 10)
        );

        controls.add(new JLabel("Search medicine:"));
        controls.add(search);

        JButton searchButton = new JButton("Search");
        JButton allButton = new JButton("Show All");
        JButton addButton = new JButton("Add Medicine");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");

        controls.add(searchButton);
        controls.add(allButton);
        controls.add(addButton);
        controls.add(editButton);
        controls.add(deleteButton);
        controls.add(refreshButton);

        searchButton.addActionListener(
            e -> loadMedicines(search.getText())
        );

        allButton.addActionListener(e -> {
            search.setText("");
            loadMedicines("");
        });

        refreshButton.addActionListener(
            e -> loadMedicines(search.getText())
        );

        addButton.addActionListener(
            e -> showMedicineDialog(null)
        );

        editButton.addActionListener(e -> editSelected());
        deleteButton.addActionListener(e -> deleteSelected());

        // Medicine table
        table.setRowHeight(26);
        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );
        table.setAutoCreateRowSorter(true);

        JScrollPane scroll = new JScrollPane(table);

        JLabel footer = new JLabel(
            "Expiry warning: 30 days | " +
            "Low stock: quantity at or below threshold"
        );
        footer.setBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.add(controls, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);

        loadMedicines("");
    }

    // Load medicines from MySQL into the table.
    private void loadMedicines(String term) {
        try {
            model.setRowCount(0);

            List<Medicine> medicines = dao.findAll(term);

            for (Medicine m : medicines) {
                model.addRow(new Object[]{
                    m.getId(),
                    m.getName(),
                    m.getCategory(),
                    m.getPrice(),
                    m.getQuantity(),
                    m.getExpiryDate(),
                    m.getLowStockLevel(),
                    m.getStatus()
                });
            }

        } catch (Exception ex) {
            showError(
                "Could not load medicines. Check MySQL connection.\n"
                + ex.getMessage()
            );
        }
    }

    // Get the medicine selected in the table.
    private Medicine selectedMedicine() {
        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            return null;
        }

        int row = table.convertRowIndexToModel(viewRow);

        try {
            return new Medicine(
                Integer.parseInt(
                    model.getValueAt(row, 0).toString()
                ),
                model.getValueAt(row, 1).toString(),
                model.getValueAt(row, 2).toString(),
                new BigDecimal(
                    model.getValueAt(row, 3).toString()
                ),
                Integer.parseInt(
                    model.getValueAt(row, 4).toString()
                ),
                LocalDate.parse(
                    model.getValueAt(row, 5).toString()
                ),
                Integer.parseInt(
                    model.getValueAt(row, 6).toString()
                )
            );

        } catch (Exception ex) {
            showError("Could not read the selected row.");
            return null;
        }
    }

    // Edit the selected medicine.
    private void editSelected() {
        Medicine m = selectedMedicine();

        if (m == null) {
            JOptionPane.showMessageDialog(
                this, "Select a medicine first."
            );
            return;
        }

        showMedicineDialog(m);
    }

    // Delete the selected medicine.
    private void deleteSelected() {
        Medicine m = selectedMedicine();

        if (m == null) {
            JOptionPane.showMessageDialog(
                this, "Select a medicine first."
            );
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Permanently delete " + m.getName() + "?",
            "Confirm deletion",
            JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.delete(m.getId());
            loadMedicines(search.getText());

        } catch (Exception ex) {
            showError(
                "Could not delete medicine.\n" + ex.getMessage()
            );
        }
    }

    // Display the form for adding or editing a medicine.
    private void showMedicineDialog(Medicine existing) {
        JTextField name = new JTextField(
            existing == null ? "" : existing.getName(), 22
        );

        JTextField category = new JTextField(
            existing == null ? "" : existing.getCategory(), 22
        );

        JTextField price = new JTextField(
            existing == null ? "" :
                existing.getPrice().toPlainString(), 22
        );

        JTextField quantity = new JTextField(
            existing == null ? "0" :
                String.valueOf(existing.getQuantity()), 22
        );

        JTextField expiry = new JTextField(
            existing == null ? "" :
                existing.getExpiryDate().toString(), 22
        );

        JTextField low = new JTextField(
            existing == null ? "10" :
                String.valueOf(existing.getLowStockLevel()), 22
        );

        JPanel form = new JPanel(
            new GridLayout(0, 2, 8, 8)
        );

        form.add(new JLabel("Medicine name *"));
        form.add(name);

        form.add(new JLabel("Category *"));
        form.add(category);

        form.add(new JLabel("Price (KES) *"));
        form.add(price);

        form.add(new JLabel("Quantity *"));
        form.add(quantity);

        form.add(new JLabel("Expiry date (YYYY-MM-DD) *"));
        form.add(expiry);

        form.add(new JLabel("Low-stock threshold *"));
        form.add(low);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            existing == null ? "Add Medicine" : "Edit Medicine",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String n = name.getText().trim();
            String c = category.getText().trim();

            if (n.isBlank() || c.isBlank()) {
                throw new IllegalArgumentException(
                    "Name and category are required."
                );
            }

            BigDecimal p =
                new BigDecimal(price.getText().trim());

            int q = Integer.parseInt(
                quantity.getText().trim()
            );

            LocalDate d = LocalDate.parse(
                expiry.getText().trim()
            );

            int threshold = Integer.parseInt(
                low.getText().trim()
            );

            if (p.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                    "Price cannot be negative."
                );
            }

            if (q < 0 || threshold < 0) {
                throw new IllegalArgumentException(
                    "Quantity and threshold cannot be negative."
                );
            }

            Medicine m = new Medicine(
                existing == null ? 0 : existing.getId(),
                n,
                c,
                p,
                q,
                d,
                threshold
            );

            dao.save(m);
            loadMedicines(search.getText());

        } catch (NumberFormatException ex) {
            showError(
                "Price, quantity, and threshold must contain valid numbers."
            );

        } catch (DateTimeParseException ex) {
            showError(
                "Enter the expiry date in YYYY-MM-DD format, " +
                "for example 2027-05-12."
            );

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());

        } catch (Exception ex) {
            showError(
                "Could not save medicine.\n" + ex.getMessage()
            );
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Pharmacy Management System",
            JOptionPane.ERROR_MESSAGE
        );
    }
}