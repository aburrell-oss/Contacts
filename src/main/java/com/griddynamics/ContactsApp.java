package com.griddynamics;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Provides the interactive command-line UI for
 * managing a contacts' collection.
 *
 * <p>This class handles user interactions,
 * menu navigation, and coordinates operations between the
 * user interface and the {@link Contacts} data store.
 */
public final class ContactsApp {

    /**
     * The contacts database instance used by the application.
     */
    private final Contacts contacts;

    /**
     * Scanner for reading user input.
     */
    private final Scanner scanner;

    /**
     * Creates the application wrapper for a given {@link Contacts} instance.
     *
     * @param initialContacts contacts database
     * @throws IllegalArgumentException if initialContacts is null
     */
    public ContactsApp(final Contacts initialContacts) {
        if (initialContacts == null) {
            throw new IllegalArgumentException(
                    "Initial contacts cannot be null");
        }
        this.contacts = new Contacts(initialContacts);
        this.scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    }

    /**
     * Constructor for testing purposes with custom scanner.
     *
     * @param initialContacts contacts database
     * @param customScanner   custom scanner for testing
     * @throws IllegalArgumentException if any parameter is null
     */
    @SuppressWarnings("EI_EXPOSE_REP2")
    public ContactsApp(final Contacts initialContacts,
                       final Scanner customScanner) {
        if (initialContacts == null) {
            throw new IllegalArgumentException(
                    "Initial contacts cannot be null");
        }
        if (customScanner == null) {
            throw new IllegalArgumentException(
                    "Scanner cannot be null");
        }
        this.contacts = new Contacts(initialContacts);
        this.scanner = customScanner;
    }

    /**
     * Gets the contacts database instance.
     *
     * @return the contacts instance
     */
    public Contacts getContacts() {
        return contacts;
    }

    /**
     * Starts the main menu loop of the application.
     */
    public void run() {
        while (true) {
            System.out.print("[menu] Enter action "
                    + "(add, list, search, count, exit): ");
            final String action = scanner.nextLine().trim();

            if (action.isEmpty()) {
                System.out.println("Unknown command");
                continue;
            }

            switch (action) {
                case "add" -> handleAdd();
                case "list" -> handleList();
                case "search" -> handleSearch();
                case "count" -> handleCount();
                case "exit" -> {
                    handleExit();
                    return;
                }
                default -> System.out.println("Unknown command\n");
            }
        }
    }

    /**
     * Handles the add command - adds a new record and saves.
     */
    private void handleAdd() {
        contacts.addRecord(scanner);
        contacts.save();
        System.out.println("The record added");
    }

    /**
     * Handles the count command - displays the
     * number of records.
     */
    private void handleCount() {
        System.out.printf("The Phone Book has %d records.%n%n",
                contacts.size());
    }

    /**
     * Handles the exit command.
     */
    private void handleExit() {
        // Placeholder for future cleanup
    }

    /**
     * Handles the list command - displays all records and allows selection.
     */
    private void handleList() {
        if (contacts.size() == 0) {
            System.out.println("No records to list!\n");
            return;
        }

        contacts.printShortList();

        System.out.print("\n[list] Enter action ([number], back): ");
        final String input = scanner.nextLine().trim();

        if ("back".equalsIgnoreCase(input)) {
            System.out.println();
            return;
        }

        try {
            final int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < contacts.size()) {
                openRecordView(index);
            } else {
                System.out.println();
            }
        } catch (NumberFormatException e) {
            System.out.println();
        }
    }

    /**
     * Handles the search command - searches for records and allows selection.
     */
    private void handleSearch() {
        final List<Integer> results = contacts.search(scanner);

        System.out.printf("Found %d results%n", results.size());

        if (results.isEmpty()) {
            System.out.println("No matches found!\n");
            return;
        }

        contacts.printShortList(results);

        System.out.print("\n[search] Enter action ([number], back, again): ");
        final String action = scanner.nextLine().trim();

        switch (action) {
            case "back" -> System.out.println();
            case "again" -> {
                System.out.println();
                handleSearch();
            }
            default -> {
                try {
                    final int index = Integer.parseInt(action) - 1;
                    if (index >= 0 && index < results.size()) {
                        openRecordView(results.get(index));
                    } else {
                        System.out.println();
                    }
                } catch (NumberFormatException e) {
                    System.out.println();
                }
            }
        }
    }

    /**
     * @param index
     * Opens the record view for a specific record.
     */
    private void openRecordView(final int index) {
        while (true) {
            try {
                final Record record = contacts.get(index);
                System.out.println(record);
                System.out.print("[record] Enter action "
                        + "(edit, delete, menu): ");
                final String action = scanner.nextLine().trim();

                switch (action) {
                    case "edit" -> handleEdit(index);
                    case "delete" -> {
                        handleDelete(index);
                        return;
                    }
                    case "menu" -> {
                        System.out.println();
                        return;
                    }
                    default -> System.out.println("Unknown command");
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Record not found.\n");
                return;
            }
        }
    }

    /**
     * @param index Handles editing a record.
     */
    private void handleEdit(final int index) {
        try {
            contacts.editRecord(scanner, index);
            contacts.save();
            System.out.println("Saved");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid field");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Invalid record index.\n");
        }
    }

    /**
     * @param index Handles deleting a record.
     */
    private void handleDelete(final int index) {
        try {
            contacts.deleteRecord(index);
            contacts.save();
            System.out.println("The record removed!\n");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Error: Invalid record index.\n");
        }
    }

    /**
     * Closes the scanner resource. Should be called
     * when the application terminates.
     */
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }
}
