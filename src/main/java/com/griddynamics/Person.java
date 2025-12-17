package com.griddynamics;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a person-type contact record.
 *
 * Person stores name, surname, birthdate,
 * gender, and phone number. The class is
 * final to avoid
 * unsafe subclassing.
 */
public final class Person extends Record {

  /** Person's given name. */
  private String name = "";

  /** Person's family name. */
  private String surname = "";

  /** Person's birthdate in ISO format or "[no data]". */
  private String birth = "[no data]";

  /** Person's gender ("M","F") or "[no data]". */
  private String gender = "[no data]";

  /** Person's phone number or "[no number]". */
  private String phone = "[no number]";

  /**
   * Getter for Person's given name.
   *
   * @return Person's given name
   */
  public String getName() {
    return name;
  }

  /**
   * Setter for Person's given name.
   *
   * @param newName Person's given name
   */
  public void setName(final String newName) {
    this.name = newName != null ? newName : "";
  }

  /**
   * Getter for Person's family name.
   *
   * @return Person's family name
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Setter for Person's family name.
   *
   * @param newSurname Person's family name
   */
  public void setSurname(final String newSurname) {
    this.surname = newSurname != null ? newSurname : "";
  }

  /**
   * Getter for Person's birthdate.
   *
   * @return Person's birthdate
   */
  public String getBirth() {
    return birth;
  }

  /**
   * Setter for Person's birthdate.
   *
   * @param newBirth Person's birthdate
   */
  public void setBirth(final String newBirth) {
    this.birth = newBirth != null ? newBirth : "[no data]";
  }

  /**
   * Getter for Person's gender.
   *
   * @return Person's gender
   */
  public String getGender() {
    return gender;
  }

  /**
   * Setter for Person's gender.
   *
   * @param newGender Person's gender
   */
  public void setGender(final String newGender) {
    this.gender = newGender != null ? newGender : "[no data]";
  }

  /**
   * Getter for Person's phone number.
   *
   * @return Person's phone number
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Setter for Person's phone number.
   *
   * @param newPhone Person's phone
   */
  public void setPhone(final String newPhone) {
    this.phone = newPhone != null ? newPhone : "[no number]";
  }

  /**
   * Factory method to create a new Person interactively.
   *
   * @param scanner input source
   * @return Person instance
   */
  public static Person create(final Scanner scanner) {
    if (scanner == null) {
      throw new IllegalArgumentException("Scanner cannot be null");
    }

    final Person p = new Person();

    System.out.print("Enter the name: ");
    p.name = scanner.nextLine();

    System.out.print("Enter the surname: ");
    p.surname = scanner.nextLine();

    System.out.print("Enter the birth date: ");
    final String b = scanner.nextLine();
    p.birth = validateBirth(b);

    System.out.print("Enter the gender (M, F): ");
    final String g = scanner.nextLine();
    p.gender = validateGender(g);

    System.out.print("Enter the number: ");
    final String ph = scanner.nextLine();
    p.phone = validatePhone(ph);

    return p;
  }

  /**
   * Validates the birthdate string (ISO format).
   *
   * @param birth birthdate candidate
   * @return the valid birthdate or "[no data]" if invalid
   */
  private static String validateBirth(final String birth) {
    if (birth == null || birth.trim().isEmpty()) {
      System.out.println("Bad birth date!");
      return "[no data]";
    }

    try {
      LocalDate.parse(birth);
      return birth;
    } catch (DateTimeParseException e) {
      System.out.println("Bad birth date!");
      return "[no data]";
    }
  }

  /**
   * Validates the gender string.
   *
   * @param gender gender candidate
   * @return "M" or "F" if valid; "[no data]" otherwise
   */
  private static String validateGender(final String gender) {
    if (gender != null && gender.matches("M|F")) {
      return gender;
    }
    System.out.println("Bad gender!");
    return "[no data]";
  }

  /**
   * Validates the phone string.
   *
   * Phone numbers must contain at least
   * one digit and may include common separators
   * like spaces,
   *
   * hyphens, parentheses, and dots. Optionally
   * starts with a plus sign for international numbers.
   *
   * @param phone phone candidate
   * @return phone if valid; "[no number]" otherwise
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
    return List.of("name", "surname", "birth", "gender", "number");
  }

  /**
   * {@inheritDoc}
   *
   * Applies validation for birth, gender and phone fields.
   */
  @Override
  public void applyEdit(final String field, final String value) {
    if (field == null) {
      return;
    }

    switch (field) {
      case "name" -> setName(value);
      case "surname" -> setSurname(value);
      case "birth" -> setBirth(validateBirth(value));
      case "gender" -> setGender(validateGender(value));
      case "number" -> setPhone(validatePhone(value));
      default -> {
        // no-op for unknown fields
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public String shortInfo() {
    return name + " " + surname;
  }

  /**
   * {@inheritDoc}
   *
   * Performs case-insensitive matching against all text fields.
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
      final Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

      return p.matcher(name).find()
          || p.matcher(surname).find()
          || p.matcher(birth).find()
          || p.matcher(gender).find()
          || p.matcher(phone).find();
    } catch (PatternSyntaxException e) {
      // Invalid regex pattern, return false
      return false;
    }
  }

  /**
   * {@inheritDoc}
   *
   * Formats person details including timestamps.
   *
   * @return multi-line person description
   */
  @Override
  public String toString() {
    return String.format(
        "Name: %s%nSurname: %s%nBirth date: "
                + "%s%nGender: %s%nNumber: %s%n%s",
        name, surname, birth, gender, phone,
            super.toString());
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Person person = (Person) obj;
    return name.equals(person.name)
        && surname.equals(person.surname)
        && birth.equals(person.birth)
        && gender.equals(person.gender)
        && phone.equals(person.phone);
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    int result = name.hashCode();
    final int hashNum = 31;
    result = hashNum * result + surname.hashCode();
    result = hashNum * result + birth.hashCode();
    result = hashNum * result + gender.hashCode();
    result = hashNum * result + phone.hashCode();
    return result;
  }
}
