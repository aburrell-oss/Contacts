package com.griddynamics;

import java.io.Serial;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Represents a person-type contact record.
 *
 * <p>Person stores name, surname, birthdate, gender, and phone number.
 * The class is final to avoid unsafe subclassing.</p>
 */
public final class Person extends Record {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * Setter for Person's given name.
     *
     * @param newName - Person's given name
     */
    public void setName(final String newName) {
        this.name = newName;
    }

    /**
     * Setter for Person's family name.
     *
     * @param newSurname - Person's family name
     */
    public void setSurname(final String newSurname) {
        this.surname = newSurname;
    }

    /**
     * Setter for Person's birthdate.
     *
     * @param newBirth - Person's birthdate
     */
    public void setBirth(final String newBirth) {
        this.birth = newBirth;
    }

    /**
     * Setter for Person's gender.
     *
     * @param newGender - Person's gender
     */
    public void setGender(final String newGender) {
        this.gender = newGender;
    }

    /**
     * Setter for Person's phone number.
     *
     * @param newPhone - Person's phone
     */
    public void setPhone(final String newPhone) {
        this.phone = newPhone;
    }

    /**
     * Factory method to create a new Person interactively.
     *
     * @param scanner - input source
     * @return Person instance
     */
    public static Person create(final Scanner scanner) {
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

        System.out.println("The record added.\n");
        return p;
    }

    /**
     * Validates the birthdate string (ISO format).
     *
     * @param birth birthdate candidate
     * @return the valid birthdate or "[no data]" if invalid
     */
    private static String validateBirth(final String birth) {
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
     * Validates the phone string (simple rule: must contain a digit).
     *
     * @param phone phone candidate
     * @return phone if contains a digit; "[no number]" otherwise
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
        return List.of("name", "surname", "birth", "gender", "number");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Applies validation for birth, gender and phone fields.</p>
     */
    @Override
    public void applyEdit(final String field, final String value) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String shortInfo() {
        return name + " " + surname;
    }

    /**
     * {@inheritDoc}
     *
     * Performs case-insensitive matching against all text fields.
     */
    @Override
    public boolean matches(final String pattern) {
        final Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

        return p.matcher(name).find()
                || p.matcher(surname).find()
                || p.matcher(birth).find()
                || p.matcher(gender).find()
                || p.matcher(phone).find();
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
                name,
                surname,
                birth,
                gender,
                phone,
                super.toString());
    }
}
