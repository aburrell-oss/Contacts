package com.griddynamics;

import java.io.Serial;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents an organization-type contact record.
 *
 * Final to avoid unsafe subclassing and to satisfy
 * static checks. Organization stores name,
 * address, and phone number.
 */
public final class Organization extends Record {

  @Serial private static final long serialVersionUID = 1L;

  /** Organization name. */
  private String name = "";

  /** Organization address. */
  private String address = "";

  /** Organization phone number or "[no number]". */
  private String phone = "[no number]";

  /** Default constructor. */
  public Organization() {
    super();
  }

  /**
   * Getter for Organization name.
   *
   * @return Organization's name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for Organization name.
   *
   * @param newName Organization's name
   */
  public void setName(final String newName) {
    this.name = newName != null ? newName : "";
  }

  /**
   * Getter for Organization address.
   *
   * @return Organization's address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Setter for Organization address.
   *
   * @param newAddress Organization's address
   */
  public void setAddress(final String newAddress) {
    this.address = newAddress != null ? newAddress : "";
  }

  /**
   * Getter for Organization phone number.
   *
   * @return Organization's phone number
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Setter for Organization phone number.
   *
   * @param newPhone Organization's phone number
   */
  public void setPhone(final String newPhone) {
    this.phone = newPhone != null ? newPhone : "[no number]";
  }

  /**
   * Factory method to create a new Organization interactively.
   *
   * @param scanner input source
   * @return Organization instance
   * @throws IllegalArgumentException if scanner is null
   */
  public static Organization create(final Scanner scanner) { //
    if (scanner == null) {
      throw new IllegalArgumentException("Scanner cannot be null");
    }

    final Organization o = new Organization();

    System.out.print("Enter the organization name: ");
    o.name = scanner.nextLine();

    System.out.print("Enter the address: ");
    o.address = scanner.nextLine();

    System.out.print("Enter the number: ");
    final String ph = scanner.nextLine();
    o.phone = validatePhone(ph);

    
    return o;
  }

  /**
   * Validates the phone string.
   *
   * Phone numbers must contain at
   * least one digit and may include common
   * separators like spaces,
   * hyphens, parentheses, and dots. Optionally
   * starts with a plus sign for international numbers.
   *
   * @param phone candidate phone
   * @return validated phone or "[no number]"
   */
  private static String validatePhone(final String phone) {
    if (phone == null || phone.trim().isEmpty()) {
      System.out.println("Bad phone number!");
      return "[no number]";
    }

    // Check for valid phone format:
    // - May start with +
    // - Contains digits, spaces, hyphens, parentheses, dots
    // - Must have at least one digit
    final String phonePattern = "^\\+?[0-9\\s\\-().]+$";

    if (phone.matches(phonePattern) && phone.replaceAll(
            "[^0-9]", "").length() > 0) {
      return phone;
    }

    System.out.println("Bad phone number!");
    return "[no number]";
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getEditableFields() {
    return List.of("name", "address", "number");
  }

  /**
   * {@inheritDoc}
   *
   * Applies safe edits for organization fields.
   * Updates the last-edited timestamp after
   * successful edit.
   */
  @Override
  public void applyEdit(final String field,
                        final String value) {
    if (field == null) {
      return;
    }

    switch (field) {
      case "name" -> setName(value);
      case "address" -> setAddress(value);
      case "number" -> setPhone(validatePhone(value));
      default -> {
        // no-op for unknown fields
      }
    }

    // Update timestamp after edit
    updateTimestamp();
  }

  /**
   * {@inheritDoc}
   *
   * @return organization name as short summary
   */
  @Override
  public String shortInfo() {
    return name;
  }

  /**
   * {@inheritDoc}
   *
   * Performs case-insensitive matching against
   * all text fields.
   *
   * @param pattern regex pattern to match
   * @return true if pattern matches any field
   */
  @Override
  public boolean matches(final String pattern) {
    if (pattern == null || pattern.isEmpty()) {
      return false;
    }

    try {
      final Pattern p = Pattern.compile(pattern,
              Pattern.CASE_INSENSITIVE);
      return p.matcher(name).find()
              || p.matcher(address).find()
              || p.matcher(phone).find();
    } catch (PatternSyntaxException e) {
      // Invalid regex pattern, return false
      return false;
    }
  }

  /**
   * {@inheritDoc}
   *
   * Formats organization details including timestamps.
   *
   * @return multi-line organization description
   */
  @Override
  public String toString() {
    return String.format(
        "Organization name: %s%nAddress: %s%nNumber: %s%n%s",
        name, address, phone, super.toString());
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Organization that = (Organization) obj;
    return Objects.equals(name, that.name)
        && Objects.equals(address, that.address)
        && Objects.equals(phone, that.phone);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), name, address, phone);
  }
}
