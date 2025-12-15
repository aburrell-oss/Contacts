package com.griddynamics;

import java.io.Serial;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents an organization-type contact record.
 *
 * <p>Final to avoid unsafe subclassing and to satisfy static checks.
 */
public final class Organization extends Record {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Organization name. */
    private String name = "";

    /** Organization address. */
    private String address = "";

    /** Organization phone number or "[no number]". */
    private String phone = "[no number]";

    /**
     * Setter for Organization name.
     *
     * @param newName - Organization's name
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Setter for Organization address.
     *
     * @param newAddress - Organization's address
     */
    public void setAddress(final String newAddress) {
        this.address = newAddress;
    }

    /**
     * Setter for Organization phone number.
     *
     * @param newPhone - Organization's phone number
     */
    public void setPhone(final String newPhone) {
        this.phone = newPhone;
    }

    /**
     * Factory method to create a new Organization interactively.
     *
     * @param scanner input source
     * @return Organization instance
     */
    public static Organization create(final java.util.Scanner scanner) {
        final Organization o = new Organization();

        System.out.print("Enter the organization name: ");
        o.name = scanner.nextLine();

        System.out.print("Enter the address: ");
        o.address = scanner.nextLine();

        System.out.print("Enter the number: ");
        o.phone = validatePhone(scanner.nextLine());

        System.out.println("The record added.\n");
        return o;
    }

    /**
     * Simple phone validation (must contain a digit).
     *
     * @param phone candidate phone
     * @return validated phone or "[no number]"
     */
    private static String validatePhone(final String phone) {
        if (phone != null && phone.matches(".*\\d.*")) {
            return phone;
        }
        return "[no number]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getEditableFields() {
        return List.of("name", "address", "number");
    }

    /**
     * {@inheritDoc}
     *
     * Applies safe edits for organization fields.
     */
    @Override
    public void applyEdit(final String field, final String value) {
        switch (field) {
            case "name" -> setName(value);
            case "address" -> setAddress(value);
            case "number" -> setPhone(validatePhone(value));
            default -> {
                // no-op
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return short one-line summary
     */
    @Override
    public String shortInfo() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(final String pattern) {
        final Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        return p.matcher(name).find()
                || p.matcher(address).find()
                || p.matcher(phone).find();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(
                "Organization name: %s%nAddress: %s%nNumber: %s%n%s",
                name, address, phone, super.toString());
    }
}
