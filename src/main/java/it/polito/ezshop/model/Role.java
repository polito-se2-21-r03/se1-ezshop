package it.polito.ezshop.model;

public enum Role {
    ADMINISTRATOR("Administrator"),
    CASHIER("Cashier"),
    SHOP_MANAGER("ShopManager");

    /**
     * Textual representation of the enum.
     */
    private final String value;

    Role(String value) {
        this.value = value;
    }

    /**
     * Return a role from its textual representation.
     *
     * @param value is the textual representation of the enum.
     * @return the enum
     */
    public static Role fromString(String value) {
        for (Role r : Role.values()) {
            if (r.value.equals(value)) {
                return r;
            }
        }

        return null;
    }

    public String getValue() {
        return this.value;
    }

}
