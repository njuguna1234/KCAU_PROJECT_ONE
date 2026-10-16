package pharmacy.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Medicine extends InventoryItem {
    private String category;
    private BigDecimal price;
    private int quantity;
    private LocalDate expiryDate;
    private int lowStockLevel;

    public Medicine(
            int id,
            String name,
            String category,
            BigDecimal price,
            int quantity,
            LocalDate expiryDate,
            int lowStockLevel) {

        super(id, name);
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.lowStockLevel = lowStockLevel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getLowStockLevel() {
        return lowStockLevel;
    }

    public void setLowStockLevel(int lowStockLevel) {
        this.lowStockLevel = lowStockLevel;
    }

    @Override
    public String getStatus() {
        if (expiryDate.isBefore(LocalDate.now())) {
            return "EXPIRED";
        }

        if (!expiryDate.isAfter(LocalDate.now().plusDays(30))) {
            return "NEAR EXPIRY";
        }

        if (quantity <= lowStockLevel) {
            return "LOW STOCK";
        }

        return "OK";
    }

    public long daysUntilExpiry() {
        return ChronoUnit.DAYS.between(
            LocalDate.now(), expiryDate
        );
    }
}