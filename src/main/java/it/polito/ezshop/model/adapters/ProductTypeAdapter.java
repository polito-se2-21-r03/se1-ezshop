package it.polito.ezshop.model.adapters;

import it.polito.ezshop.model.ProductType;

/**
 * ProductTypeAdapter adapts it.polito.ezshop.model.ProductType to the
 * it.polito.ezshop.data.ProductType interface
 */
public class ProductTypeAdapter implements it.polito.ezshop.data.ProductType {

    private final ProductType product;

    public ProductTypeAdapter(ProductType product) {
        this.product = product;
    }

    @Override
    public Integer getQuantity() {
        return product.getQuantity();
    }

    @Override
    public void setQuantity(Integer quantity) {
        product.setQuantity(quantity);
    }

    @Override
    public String getLocation() {
        return product.getLocation();
    }

    @Override
    public void setLocation(String location) {
        product.setLocation(location);
    }

    @Override
    public String getNote() {
        return product.getNote();
    }

    @Override
    public void setNote(String note) {
        product.setNote(note);
    }

    @Override
    public String getProductDescription() {
        return product.getProductDescription();
    }

    @Override
    public void setProductDescription(String productDescription) {
        product.setProductDescription(productDescription);
    }

    @Override
    public String getBarCode() {
        return product.getBarCode();
    }

    @Override
    public void setBarCode(String barCode) {
        product.setBarCode(barCode);
    }

    @Override
    public Double getPricePerUnit() {
        return product.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        product.setPricePerUnit(pricePerUnit);
    }

    @Override
    public Integer getId() {
        return product.getId();
    }

    @Override
    public void setId(Integer id) {
        product.setId(id);
    }
}
