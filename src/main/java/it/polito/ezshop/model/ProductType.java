package it.polito.ezshop.model;

import java.util.Objects;

public class ProductType {

    private int id;
    private Integer quantity;
    private Position location;
    private String note;
    private String productDescription;
    private String barCode;
    private Double pricePerUnit;

    public ProductType(String note, String productDescription, String barCode, Double pricePerUnit, int id) {
        this(note, productDescription, barCode, pricePerUnit, id, 0, null);
    }

    public ProductType(String note, String productDescription, String barCode, Double pricePerUnit, int id, Integer quantity, Position location) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");
        Objects.requireNonNull(barCode, "barCode must not be null");
        Objects.requireNonNull(pricePerUnit, "pricePerUnit must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");
        Objects.requireNonNull(note, "note must not be null");

        this.id = id;
        this.productDescription = productDescription;
        this.barCode = barCode;
        this.pricePerUnit = pricePerUnit;
        this.note = note;
        this.quantity = quantity;
        this.location = location;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void updateQuantity(Integer delta) {
        this.quantity += delta;
    }

    public String getLocation() {
        if (this.location == null) {
            return null;
        }

        return this.location.toString();
    }

    public void setLocation(String location) {
        this.location = Position.parsePosition(location);
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getProductDescription() {
        return this.productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductType that = (ProductType) o;
        return id == that.id &&
                quantity.equals(that.quantity) &&
                Objects.equals(location, that.location) &&
                note.equals(that.note) &&
                productDescription.equals(that.productDescription) &&
                barCode.equals(that.barCode) &&
                pricePerUnit.equals(that.pricePerUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
