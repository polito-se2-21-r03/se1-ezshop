package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static it.polito.ezshop.utils.Utils.isValidBarcode;

public class ProductType {

    /**
     * DUMMY_RFID represents a product not associated with a valid RFID
     */
    public static final String DUMMY_RFID = "dummy_RFID";

    private final int id;
    private int quantity;
    private Position position;
    private String note;
    private String productDescription;
    private String barCode;
    private double pricePerUnit;

    private final List<String> RFIDs = new ArrayList<>();

    public ProductType(Integer id, String productDescription, String barCode, Double pricePerUnit, String note) throws
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, InvalidProductIdException, InvalidQuantityException {
        this(id, productDescription, barCode, pricePerUnit, note, 0, null);
    }

    public ProductType(Integer id, String productDescription, String barCode, Double pricePerUnit, String note, Integer quantity, Position position) throws
            InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, InvalidProductIdException, InvalidQuantityException {

        if (id == null || id <= 0) {
            throw new InvalidProductIdException("The product ID must be a positive integer");
        }

        this.id = id;
        this.setProductDescription(productDescription);
        this.setBarCode(barCode);
        this.setPricePerUnit(pricePerUnit);
        this.setNote(note);
        this.setPosition(position);
        this.setQuantity(quantity);
    }

    public static void validateProductCode(String code) throws InvalidProductCodeException {
        if (!isValidBarcode(code)) {
            throw new InvalidProductCodeException("Invalid Bar Code");
        }
    }

    public int getId() {
        return this.id;
    }

    public String getProductDescription() {
        return this.productDescription;
    }

    /**
     * Set the product's description.
     *
     * @param productDescription new product description
     * @throws InvalidProductDescriptionException thrown if description is null or empty string
     */
    public void setProductDescription(String productDescription) throws InvalidProductDescriptionException {
        if (productDescription == null || productDescription.equals("")) {
            throw new InvalidProductDescriptionException("Product description may not be null or empty.");
        }
        this.productDescription = productDescription;
    }

    public String getBarCode() {
        return barCode;
    }

    /**
     * Define a new barcode for the product. Must follow GTIN-12, GTIN-13 or GTIN-14 standard.
     *
     * @param barCode new barcode of the product
     * @throws InvalidProductCodeException thrown if barcode doesn't comply to standard
     */
    public void setBarCode(String barCode) throws InvalidProductCodeException {
        if (!isValidBarcode(barCode)) {
            throw new InvalidProductCodeException("Barcode must follow the GTIN-12, GTIN-13 or GTIN-14 standard");
        }
        this.barCode = barCode;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    /**
     * Set the selling price for one unit of this product.
     *
     * @param pricePerUnit selling price for one unit of this product
     * @throws InvalidPricePerUnitException thrown if price is 0 or negative
     */
    public void setPricePerUnit(Double pricePerUnit) throws InvalidPricePerUnitException {
        if (pricePerUnit == null || pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException("Price per unit must be positive double");
        }
        this.pricePerUnit = pricePerUnit;
    }

    public String getNote() {
        return this.note;
    }

    /**
     * Set the note for this product.
     *
     * @param note New note for this product. Null means note is empty string
     */
    public void setNote(String note) {
        if (note == null) {
            this.note = "";
        } else {
            this.note = note;
        }
    }

    public int getQuantity() {
        return this.RFIDs.size();
    }

    /**
     * Set available quantity of this product
     *
     * TODO: remove this method
     *
     * @param quantity quantity to be set
     * @throws InvalidQuantityException thrown if quantity is negative
     * @throws IllegalStateException    thrown if no position has been defined
     */
    @Deprecated
    public void setQuantity(Integer quantity) throws InvalidQuantityException, IllegalStateException {
        if (quantity == null || quantity < 0) {
            throw new InvalidQuantityException("Product quantity must non-negative");
        }
        if (position == null && quantity > 0) {
            throw new IllegalStateException("Can not set quantity, location must be defined");
        }
        this.quantity = quantity;
    }

    public Position getPosition() {
        if (this.position == null) {
            return null;
        }
        return new Position(this.position);
    }

    /**
     * Set the position this product is assigned to.
     *
     * @param position new position of the product, can be zero if no position should be assigned
     */
    public void setPosition(Position position) {
        if (position == null) {
            this.position = null;
        } else {
            this.position = new Position(position);
        }
    }

    public boolean addRFID (String RFID) {
        if (!RFID.equals(DUMMY_RFID) && RFIDs.contains(RFID)) {
            return false;
        }

        RFIDs.add(RFID);
        return true;
    }

    public boolean removeRFID (String RFID) {
        return RFIDs.remove(RFID);
    }

    public boolean RFIDexists (String RFID) {
        return RFIDs.contains(RFID);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductType that = (ProductType) o;
        return id == that.id &&
                quantity == that.quantity &&
                Objects.equals(position, that.position) &&
                note.equals(that.note) &&
                productDescription.equals(that.productDescription) &&
                barCode.equals(that.barCode) &&
                pricePerUnit == that.pricePerUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
