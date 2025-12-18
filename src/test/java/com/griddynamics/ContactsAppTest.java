package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for ContactsApp. */
class ContactsAppTest { //  Removed "extends Contacts"

  private Contacts contacts;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    contacts = new Contacts();

    // Capture System.out
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    // Restore System.out
    System.setOut(originalOut);
  }

  /** Helper method to create ContactsApp with simulated input. */
  private ContactsApp createAppWithInput(String input) {
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    return new ContactsApp(contacts, scanner);
  }

  @Test
  void testExitCommand() {
    ContactsApp app = createAppWithInput("exit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("[menu] Enter action"));
  }

  @Test
  void testUnknownCommand() {
    ContactsApp app = createAppWithInput("invalid\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Unknown command"));
  }

  @Test
  void testCountWithNoRecords() {
    ContactsApp app = createAppWithInput("count\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("The Phone Book has 0 records"));
  }

  @Test
  void testAddRecord() {
    String input = "add\nperson\nAlice\nSmith\n1990-01-01\nF\n555-1234\nexit\n";
    ContactsApp app = createAppWithInput(input);

    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("The record added"));
    assertEquals(1, app.getContacts().size());
  }

  @Test
  void testListEmptyRecords() {
    ContactsApp app = createAppWithInput("list\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("No records to list"));
  }

  @Test
  void testHandleEditInvalidField() {
    Person person = new Person();
    person.setName("John");
    person.setSurname("Doe");
    contacts.addRecord(person);

    String input = "list\n1\nedit\ninvalidField\nValue\nmenu\nexit\n";
    ContactsApp app = createAppWithInput(input);
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Invalid field"));
  }

  @Test
  void testHandleDeleteRecord() {
    Person person = new Person();
    person.setName("John");
    person.setSurname("Doe");
    contacts.addRecord(person);

    String input = "list\n1\ndelete\nexit\n";
    ContactsApp app = createAppWithInput(input);
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("The record removed"));
    assertEquals(0, app.getContacts().size());
  }

  @Test
  void testCloseMethod() {
    ContactsApp app = createAppWithInput("exit\n");
    assertDoesNotThrow(app::close);
  }

  @Test
  void testSearchWithMatch() {
    Person person = new Person();
    person.setName("Alice");
    person.setSurname("Smith");
    contacts.addRecord(person);

    String input = "search\nAlice\nback\nexit\n";
    ContactsApp app = createAppWithInput(input);
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Found 1 results"));
    assertTrue(output.contains("Alice Smith"));
  }

  @Test
  void testEditValidField() {
    Person person = new Person();
    person.setName("John");
    person.setSurname("Doe");
    contacts.addRecord(person);

    String input = "list\n1\nedit\nname\nJane\nmenu\nexit\n";
    ContactsApp app = createAppWithInput(input);
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Saved"));
    assertEquals("Jane", ((Person) app.getContacts().get(0)).getName());
  }

  @Test
  void testEmptyMenuInput() {
    ContactsApp app = createAppWithInput("\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Unknown command"));
  }

  @Test
  void testListWithNonNumericSelection() {
    Person person = new Person();
    person.setName("John");
    contacts.addRecord(person);

    ContactsApp app = createAppWithInput("list\nabc\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("[list] Enter action"));
  }

  @Test
  void testListWithOutOfRangeIndex() {
    Person person = new Person();
    person.setName("John");
    contacts.addRecord(person);

    ContactsApp app = createAppWithInput("list\n99\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("[list] Enter action"));
  }

  @Test
  void testRecordViewUnknownCommand() {
    Person person = new Person();
    person.setName("John");
    contacts.addRecord(person);

    ContactsApp app = createAppWithInput("list\n1\nfoo\nmenu\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("Unknown command"));
  }

  @Test
  void testOpenRecordInvalidIndex() {
    ContactsApp app = createAppWithInput("list\n1\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("No records to list"));
  }

  @Test
  void testEditInvalidIndex() {
    ContactsApp app = createAppWithInput("list\n1\nedit\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("No records to list"));
  }

  @Test
  void testDeleteInvalidIndex() {
    ContactsApp app = createAppWithInput("list\n1\ndelete\nexit\n");
    app.run();

    String output = outputStream.toString();
    assertTrue(output.contains("No records to list"));
  }

  @Test
  void testConstructorNullContacts() {
    assertThrows(IllegalArgumentException.class, () -> new ContactsApp(null));
  }

  @Test
  void testConstructorNullScanner() {
    Contacts contacts = new Contacts();
    assertThrows(IllegalArgumentException.class, () -> new ContactsApp(contacts, null));
  }

  @Test
  void testSearchNonNumericSelection() {
    Person p = new Person();
    p.setName("Alice");
    contacts.addRecord(p);

    ContactsApp app = createAppWithInput("search\nAlice\nabc\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("Found 1 results"));
  }

  @Test
  void testSearchOutOfRangeIndex() {
    Person p = new Person();
    p.setName("Alice");
    contacts.addRecord(p);

    ContactsApp app = createAppWithInput("search\nAlice\n99\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("Found 1 results"));
  }

  @Test
  void testOpenRecordViewInvalidIndex() {
    ContactsApp app = createAppWithInput("list\n1\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("No records to list"));
  }

  @Test
  void testEditInvalidIndexBranch() {
    ContactsApp app = createAppWithInput("list\n1\nedit\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("No records to list"));
  }

  @Test
  void testEmptyMenuInputBranch() {
    ContactsApp app = createAppWithInput("\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("Unknown command"));
  }

  @Test
  void testDeleteInvalidIndexBranch() {
    ContactsApp app = createAppWithInput("list\n1\ndelete\nexit\n");
    app.run();

    assertTrue(outputStream.toString().contains("No records to list"));
  }
}
