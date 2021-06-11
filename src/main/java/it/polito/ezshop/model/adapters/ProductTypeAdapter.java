package it.polito.ezshop.model.adapters;

import it.polito.ezshop.exceptions.*;
import it.polito.ezshop.model.Position;
import it.polito.ezshop.model.ProductType;
import it.polito.ezshop.model.TicketEntry;

/**
 * ProductTypeAdapter adapts it.polito.ezshop.model.ProductType to the
 * it.polito.ezshop.data.ProductType interface
 */
public class ProductTypeAdapter implements it.polito.ezshop.data.ProductType {

    private final ProductType product;

    public ProductTypeAdapter(ProductType product) {
        this.product = product;
    }

    /**
     * For debugging purposes only!!!
     */
    public ProductType get() {
        return this.product;
    }

    @Override
    public Integer getQuantity() {
        return product.getQuantity();
    }

    @Override
    public void setQuantity(Integer quantity) {
        try {
            product.setQuantity(quantity);
        } catch (InvalidQuantityException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getLocation() {
        if (product.getPosition() == null) {
            return null;
        }
        return product.getPosition().toString();
    }

    @Override
    public void setLocation(String location) {
        try {
            product.setPosition(new Position(location));
        } catch (InvalidLocationException e) {
            throw new IllegalArgumentException(e);
        }
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
        try {
            product.setProductDescription(productDescription);
        } catch (InvalidProductDescriptionException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String getBarCode() {
        return product.getBarCode();
    }

    @Override
    public void setBarCode(String barCode) {
        try {
            product.setBarCode(barCode);
        } catch (InvalidProductCodeException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Double getPricePerUnit() {
        return product.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        try {
            product.setPricePerUnit(pricePerUnit);
        } catch (InvalidPricePerUnitException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Integer getId() {
        return product.getId();
    }

    @Override
    public void setId(Integer id) {
        throw new UnsupportedOperationException("Changing the product ID is forbidden.");
    }
}
