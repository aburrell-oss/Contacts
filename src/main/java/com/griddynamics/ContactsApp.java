package com.griddynamics;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Provides the interactive command-line UI for managing a collection.
 */
public class ContactsApp {

    /** The contacts database instance used by the application. */
    private final Contacts contacts;

    /**
     * Creates the application wrapper for a given {@link Contacts} instance.
     *
     * @param initialContacts contacts database
     */
    public ContactsApp(final Contacts initialContacts) {
        this.contacts = new Contacts(initialContacts);
    }

    /**
     * Starts the main menu loop of the application.
     */
    public void run() {
        final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

        while (true) {
            System.out.print(
                    "[menu] Enter action (add, list, search, count, exit): ");
            final String action = scanner.nextLine().trim();

            switch (action) {
                case "add" -> {
                    contacts.addRecord(scanner);
                    contacts.save();
                }
                case "list" -> handleList(scanner, contacts);
                case "search" -> handleSearch(scanner, contacts);
                case "count" -> System.out.printf(
                        "The Phone Book has %d records.%n%n", contacts.size());
                case "exit" -> {
                    return;
                }
                default -> System.out.println("Unknown command\n");
            }
        }
    }

    private void handleList(final Scanner scanner,
                                   final Contacts contacts) {
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
                openRecordView(scanner, contacts, index);
            }
        } catch (NumberFormatException ignored) {
            // Ignore invalid number
        }
        System.out.println();
    }

    private void handleSearch(final Scanner scanner,
                                     final Contacts contacts) {
        final List<Integer> results = contacts.search(scanner);

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
                handleSearch(scanner, contacts);
            }
            default -> {
                try {
                    final int index = Integer.parseInt(action) - 1;
                    if (index >= 0 && index < results.size()) {
                        openRecordView(scanner, contacts, results.get(index));
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid number
                }
                System.out.println();
            }
        }
    }

    private void openRecordView(final Scanner scanner,
                                       final Contacts contacts,
                                       final int index) {
        while (true) {
            final Record record = contacts.get(index);
            System.out.println(record);
            System.out.print("[record] Enter action (edit, delete, menu): ");
            final String action = scanner.nextLine().trim();

            switch (action) {
                case "edit" -> {
                    contacts.editRecord(scanner, index);
                    contacts.save();
                }
                case "delete" -> {
                    contacts.deleteRecord(index);
                    contacts.save();
                    System.out.println("The record removed!\n");
                    return;
                }
                case "menu" -> {
                    System.out.println();
                    return;
                }
                default -> System.out.println("Unknown command");
            }
        }
    }
}
