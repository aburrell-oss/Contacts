package com.griddynamics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Stores a collection of {@link Record}
 * objects and provides methods for adding,
 * editing, deleting,
 * searching, and saving.
 */
public class Contacts implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Stored contact records.
     */
    private List<Record> records = new ArrayList<>();

    /**
     * Filename for persistent storage.
     */
    private String filename;

    /**
     * Creates an empty contacts list.
     */
    public Contacts() {
    }

    /**
     * Defensive copy constructor.
     *
     * @param other original contacts
     */
    public Contacts(final Contacts other) {
        this.filename = other.filename;
        this.records = new ArrayList<>(other.records);
    }

    /**
     * Sets the filename for saving this contacts list.
     *
     * @param filePath storage path
     */
    public void setFilename(final String filePath) {
        this.filename = filePath;
    }

    /**
     * Returns the number of stored records.
     *
     * @return size of the contact list
     */
    public int size() {
        return records.size();
    }

    /**
     * Retrieves a record at a given index.
     *
     * @param index record index
     * @return selected record
     */
    public Record get(final int index) {
        return records.get(index);
    }

    /**
     * Adds a new record based on user input.
     *
     * @param scanner input source
     */
    public void addRecord(final Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }

        System.out.print("Enter the type (person, organization): ");
        final String type = scanner.nextLine().trim();

        final Record record =
                switch (type) {
                    case "person" -> Person.create(scanner);
                    case "organization" -> Organization.create(scanner);
                    default -> {
                        System.out.println("Unknown record type.");
                        yield null;
                    }
                };

        if (record != null) {
            records.add(record);
            System.out.println("The record added.\n");
        }
    }

    /**
     * Adds a record directly to the contacts list.
     *
     * @param record the record to add
     */
    public void addRecord(final Record record) {
        if (record == null) {
            throw new IllegalArgumentException("Record cannot be null");
        }
        records.add(record);
    }

    /**
     * Edits a record at the given index.
     *
     * @param scanner input source
     * @param index   record index
     */
    public void editRecord(final Scanner scanner, final int index) {
        final Record record = records.get(index);
        final List<String> fields = record.getEditableFields();

        System.out.printf("Select a field %s: ", fields);
        final String field = scanner.nextLine().trim();

        if (!fields.contains(field)) {
            System.out.println("Invalid field!\n");
            return;
        }

        System.out.printf("Enter %s: ", field);
        final String value = scanner.nextLine().trim();

        record.applyEdit(field, value);
        record.updateTimestamp();

        System.out.println("Saved\n");
    }

    /**
     * Deletes the record at the given index.
     *
     * @param index record index
     */
    public void deleteRecord(final int index) {
        records.remove(index);
    }

    /**
     * Prints a one-line summary of all records.
     */
    public void printShortList() {
        for (int i = 0; i < records.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, records.get(i).shortInfo());
        }
    }

    /**
     * Prints a one-line summary of records corresponding to given indices.
     *
     * @param indexes list of indices to display
     */
    public void printShortList(final List<Integer> indexes) {
        for (int i = 0; i < indexes.size(); i++) {
            System.out.printf("%d. %s%n", i + 1,
                    records.get(indexes.get(i)).shortInfo());
        }
    }

    /**
     * Searches all records for the query entered by the user.
     *
     * @param scanner input source
     * @return list of indices representing matches
     */
    public List<Integer> search(final Scanner scanner) {
        System.out.print("Enter search query: ");
        final String query = scanner.nextLine().trim();

        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).matches(query)) {
                result.add(i);
            }
        }

        System.out.printf("Found %d results:%n", result.size());
        return result;
    }

    /**
     * Saves this contacts database to disk if a filename is configured.
     */
    public void save() {
        if (filename == null) {
            return;
        }

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Error saving data.");
        }
    }
}
