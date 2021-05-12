package it.polito.ezshop.model;

import java.util.Objects;

public class ProductType implements it.polito.ezshop.data.ProductType {

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


    @Override
    public Integer getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getLocation() {
        if (this.location == null) {
            return null;
        }

        return this.location.toString();
    }

    @Override
    public void setLocation(String location) {
        this.location = Position.parsePosition(location);
    }

    @Override
    public String getNote() {
        return this.note;
    }

    @Override
    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getProductDescription() {
        return this.productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public String getBarCode() {
        return barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Override
    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
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
