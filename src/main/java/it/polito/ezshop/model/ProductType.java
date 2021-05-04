package it.polito.ezshop.model;

public class ProductType implements it.polito.ezshop.data.ProductType {

    private Integer quantity;
    private Position location;
    private String note;
    private String productDescription;
    private String barCode;
    private Double pricePerUnit;
    private int id;

    public ProductType(String note, String productDescription, String barCode, Double pricePerUnit, int id) {
        this.note = note;
        this.productDescription = productDescription;
        this.barCode = barCode;
        this.pricePerUnit = pricePerUnit;
        this.id = id;
    }


    @Override
    public Integer getQuantity() { return this.quantity; }

    @Override
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    @Override
    public String getLocation() { return this.location.toString(); }

    @Override
    public void setLocation(String location) { this.location = Position.parsePosition(location); }

    @Override
    public String getNote() { return this.note; }

    @Override
    public void setNote(String note) { this.note = note; }

    @Override
    public String getProductDescription() {return this.productDescription; }

    @Override
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    @Override
    public String getBarCode() { return barCode; }

    @Override
    public void setBarCode(String barCode) { this.barCode = barCode; }

    @Override
    public Double getPricePerUnit() { return pricePerUnit; }

    @Override
    public void setPricePerUnit(Double pricePerUnit) { this.pricePerUnit = pricePerUnit; }

    @Override
    public Integer getId() { return this.id; }

    @Override
    public void setId(Integer id) { this.id = id; }
}
