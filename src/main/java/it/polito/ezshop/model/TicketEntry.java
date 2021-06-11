package it.polito.ezshop.model;

import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.InvalidPricePerUnitException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static it.polito.ezshop.utils.Utils.DUMMY_RFID;

public class TicketEntry {

    private ProductType productType;
    private double pricePerUnit;
    private double discountRate;

    private List<String> RFIDs = new ArrayList<>(); // rfids = { "1", "2", "3"}

    public TicketEntry(ProductType productType, int amount) throws InvalidQuantityException {
        // verify productType is not null
        Objects.requireNonNull(productType);

        // validate amount
        validateAmount(amount);

        this.productType = productType;
        this.pricePerUnit = productType.getPricePerUnit();
        this.discountRate = 0.0;
        for (int i = 0; i < amount; i++) {
            this.addRFID(DUMMY_RFID);
        }
    }

    public TicketEntry(ProductType productType, String RFID) throws InvalidRFIDException {
        // verify productType is not null
        Objects.requireNonNull(productType);

        // validate RFID
        if(!it.polito.ezshop.utils.Utils.isValidRFID(RFID))
            throw new InvalidRFIDException("Error, Invalid RFID");

        this.productType = productType;
        // this.amount = 1;
        this.pricePerUnit = productType.getPricePerUnit();
        this.discountRate = 0.0;
        this.addRFID(RFID);
    }

    public TicketEntry(ProductType productType, int amount, double discount) throws InvalidQuantityException,
            InvalidDiscountRateException {
        // verify productType is not null
        Objects.requireNonNull(productType);

        // validate amount and discount
        validateAmount(amount);
        validateDiscount(discount);

        this.productType = productType;
        this.pricePerUnit = productType.getPricePerUnit();
        this.discountRate = discount;
        for (int i = 0; i < amount; i++) {
            this.addRFID(DUMMY_RFID);
        }
    }

    public static void validateAmount(int amount) throws InvalidQuantityException {
        if (amount < 0) {
            throw new InvalidQuantityException();
        }
    }

    public static void validateDiscount(double discount) throws InvalidDiscountRateException {
        if (discount < 0 || discount >= 1.0) {
            throw new InvalidDiscountRateException();
        }
    }

    private static void validatePrice(double pricePerUnit) throws InvalidPricePerUnitException {
        if (pricePerUnit <= 0) {
            throw new InvalidPricePerUnitException();
        }
    }

    public double computeTotal() {
        return (1.0 - discountRate) * getAmount() * pricePerUnit;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public int getAmount() {
            return this.RFIDs.size();
    }

    public void setAmount(int amount) throws InvalidQuantityException {
        validateAmount(amount);

        int delta = amount - this.getAmount();

        if (delta >= 0) {
            // add new products (dummy RFIDs)
            this.RFIDs.addAll(Collections.nCopies(delta, DUMMY_RFID));
        } else {
            // remove -delta RFIDs
            int nToRemove = -delta;
            // first, try to remove DUMMY RFIDs
            for (; nToRemove > 0 && this.RFIDs.remove(DUMMY_RFID); nToRemove--);
            // then, remove random RFIDs
            for (; nToRemove > 0; nToRemove--) {
                this.RFIDs.remove(0);
            }
        }
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) throws InvalidDiscountRateException {
        validateDiscount(discountRate);
        this.discountRate = discountRate;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) throws InvalidPricePerUnitException {
        validatePrice(pricePerUnit);
        this.pricePerUnit = pricePerUnit;
    }

    public List<String> getRFIDs () {
        return this.RFIDs;
    }

    /**
     * Add an RFID to the list
     * @param RFID code to add
     * @return true if the code was added, false otherwise
     */
    public boolean addRFID (String RFID) {
        if (RFID == null) return false;

        // check the given RFID is either dummy or valid
        if (Utils.isValidRFID(RFID)) {

            // check the uniqueness of the RFIDs
            if (!RFID.equals(DUMMY_RFID) && RFIDs.contains(RFID)) {
                return false;
            }

            RFIDs.add(RFID);
            return true;
        }

        return false;
    }
    /**
     * Remove one RFID code from the list.
     * @param RFID code to remove
     * @return true if one RFID was removed, false otherwise
     */
    public boolean removeRFID (String RFID) {
        return RFIDs.remove(RFID);
    }

    /**
     * Check if a given RFID exists.
     * @param RFID code to check
     * @return true if the RFID exists, false otherwise
     */
    public boolean RFIDexists (String RFID) {
        return RFIDs.contains(RFID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TicketEntry that = (TicketEntry) o;
        return Double.compare(that.pricePerUnit, pricePerUnit) == 0 &&
                Double.compare(that.discountRate, discountRate) == 0 &&
                Objects.equals(productType, that.productType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productType, discountRate);
    }
}
