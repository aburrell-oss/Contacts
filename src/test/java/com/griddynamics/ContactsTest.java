package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.Test;

class ContactsTest {

  @Test
  void createPersonWithValidData() {
    String input =
        """
        John
        Doe
        1990-01-01
        M
        123456
        """;

    Person person = Person.create(new Scanner(input));

    assertEquals("John Doe", person.shortInfo());
    assertTrue(person.matches("john"));
    assertTrue(person.matches("DOE"));
    assertTrue(person.matches("123"));
  }

  @Test
  void invalidPersonDataIsHandledGracefully() {
    String input =
        """
        Jane
        Smith
        invalid-date
        X
        abc
        """;

    Person person = Person.create(new Scanner(input));

    String output = person.toString();
    assertTrue(output.contains("[no data]"));
    assertTrue(output.contains("[no number]"));
  }

  @Test
  void personApplyEditUpdatesFields() {
    Person person = new Person();

    person.applyEdit("name", "Alice");
    person.applyEdit("surname", "Brown");
    person.applyEdit("number", "555");

    assertEquals("Alice Brown", person.shortInfo());
    assertTrue(person.matches("555"));
  }

  /* =========================================================
  Organization tests
  ========================================================= */

  @Test
  void createOrganizationWithValidData() {
    String input =
        """
        Grid Dynamics
        Silicon Valley
        987654
        """;

    Organization org = Organization.create(new Scanner(input));

    assertEquals("Grid Dynamics", org.shortInfo());
    assertTrue(org.matches("grid"));
    assertTrue(org.matches("987"));
  }

  @Test
  void invalidOrganizationPhoneDefaultsToNoNumber() {
    Organization org = new Organization();

    org.applyEdit("number", "abc");

    assertTrue(org.toString().contains("[no number]"));
  }

  /* =========================================================
  Contacts tests
  ========================================================= */

  @Test
  void addPersonRecordToContacts() {
    Contacts contacts = new Contacts();

    String input =
        """
        person
        John
        Doe
        1990-01-01
        M
        123
        """;

    contacts.addRecord(new Scanner(input));

    assertEquals(1, contacts.size());
    assertEquals("John Doe", contacts.get(0).shortInfo());
  }

  @Test
  void addOrganizationRecordToContacts() {
    Contacts contacts = new Contacts();

    String input =
        """
        organization
        Acme Corp
        New York
        999
        """;

    contacts.addRecord(new Scanner(input));

    assertEquals(1, contacts.size());
    assertEquals("Acme Corp", contacts.get(0).shortInfo());
  }

  @Test
  void searchReturnsCorrectRecordIndexes() {
    Contacts contacts = new Contacts();

    contacts.addRecord(
        new Scanner(
            """
            person
            John
            Doe
            1990-01-01
            M
            123
            """));

    contacts.addRecord(
        new Scanner(
            """
            organization
            Acme Corp
            NY
            999
            """));

    List<Integer> result = contacts.search(new Scanner("john"));

    assertEquals(1, result.size());
    assertEquals(0, result.get(0));
  }

  @Test
  void editRecordUpdatesContactData() {
    Contacts contacts = new Contacts();

    contacts.addRecord(
        new Scanner(
            """
            person
            Bob
            Smith
            1995-01-01
            M
            123
            """));

    contacts.editRecord(
        new Scanner(
            """
            name
            Robert
            """),
        0);

    assertTrue(contacts.get(0).shortInfo().contains("Robert"));
  }

  @Test
  void deleteRecordRemovesItFromContacts() {
    Contacts contacts = new Contacts();

    contacts.addRecord(
        new Scanner(
            """
            organization
            TestOrg
            Addr
            111
            """));

    assertEquals(1, contacts.size());

    contacts.deleteRecord(0);

    assertEquals(0, contacts.size());
  }

  @Test
  void save_createsFile_whenFilenameSet() throws Exception {
    Contacts contacts = new Contacts();
    File tempFile = Files.createTempFile("contacts", ".dat").toFile();

    contacts.setFilename(tempFile.getAbsolutePath());
    contacts.save();

    assertTrue(tempFile.exists());
    assertTrue(tempFile.length() > 0);

    tempFile.deleteOnExit();
  }

  /* =========================================================
  Record (timestamps) tests
  ========================================================= */

  @Test
  void recordTimestampsAreInitialized() {
    Person person = new Person();

    assertNotNull(person.getCreated());
    assertNotNull(person.getLastEdited());
  }

  @Test
  void updateTimestampChangesLastEdited() throws InterruptedException {
    Person person = new Person();
    var before = person.getLastEdited();

    Thread.sleep(5);
    person.updateTimestamp();

    assertTrue(person.getLastEdited().isAfter(before));
  }
}
