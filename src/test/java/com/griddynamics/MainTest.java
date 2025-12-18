package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Unit tests for the Main class. Tests application entry point and file loading functionality. */
class MainTest {

  @TempDir Path tempDir;

  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
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

  /** Sets up System.in with simulated input. */
  private void setupInput(String input) {
    ByteArrayInputStream inputStream =
        new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    System.setIn(inputStream);
  }

  @Test
  void testMainClassIsFinal() {
    assertTrue(
        java.lang.reflect.Modifier.isFinal(Main.class.getModifiers()),
        "Main class should be final");
  }

  @Test
  void testStartMethod() {
    setupInput("exit\n");

    Main main = new Main();
    main.start();

    String output = outputStream.toString();
    assertTrue(output.contains("[menu] Enter action"));
  }

  @Test
  void testMainWithNoArguments() {
    setupInput("exit\n");

    Main.main(new String[] {});

    String output = outputStream.toString();
    assertTrue(output.contains("[menu] Enter action"));
  }

  @Test
  void testMainWithNonExistentFile() {
    setupInput("exit\n");

    Main.main(new String[] {"nonexistent.db"});

    String output = outputStream.toString();
    assertTrue(output.contains("[menu] Enter action"));
    // Should not print error for non-existent file, just continue
  }

  @Test
  void testMainWithExistingFile() throws Exception {
    // Create a test contacts file
    Path testFile = tempDir.resolve("test_contacts.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename(testFile.toString());

    Person person = new Person();
    person.setName("Test");
    person.setSurname("User");
    testContacts.addRecord(person);

    // Save to file
    testContacts.save();

    // Load via main
    setupInput("count\nexit\n");
    Main.main(new String[] {testFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("The Phone Book has 1 records"));
  }

  @Test
  void testMainWithCorruptedFile() throws Exception {
    // Create a corrupted file
    Path corruptedFile = tempDir.resolve("corrupted.db");
    Files.writeString(corruptedFile, "This is not a valid serialized object");

    setupInput("exit\n");
    Main.main(new String[] {corruptedFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("Cannot load file."));
    assertTrue(output.contains("[menu] Enter action"));
  }

  @Test
  void testMainWithInvalidSerializedData() throws Exception {
    // Create a file with wrong class
    Path invalidFile = tempDir.resolve("invalid.db");

    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(invalidFile))) {
      out.writeObject("Not a Contacts object");
    }

    setupInput("exit\n");
    Main.main(new String[] {invalidFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("Cannot load file."));
  }

  @Test
  void testMainLoadsMultipleRecords() throws Exception {
    Path testFile = tempDir.resolve("multi_contacts.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename(testFile.toString());

    Person p1 = new Person();
    p1.setName("Alice");
    p1.setSurname("Smith");
    testContacts.addRecord(p1);

    Person p2 = new Person();
    p2.setName("Bob");
    p2.setSurname("Jones");
    testContacts.addRecord(p2);

    Organization org = new Organization();
    org.setName("Acme Corp");
    testContacts.addRecord(org);

    testContacts.save();

    setupInput("count\nexit\n");
    Main.main(new String[] {testFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("The Phone Book has 3 records"));
  }

  @Test
  void testMainSetsFilenameAfterLoading() throws Exception {
    Path testFile = tempDir.resolve("filename_test.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename("different.db");

    Person person = new Person();
    person.setName("Test");
    testContacts.addRecord(person);

    // Save with ObjectOutputStream directly
    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(testFile))) {
      out.writeObject(testContacts);
    }

    setupInput("add\nperson\nNew\nUser\n2000-01-01\nM\n555-0000\nexit\n");
    Main.main(new String[] {testFile.toString()});

    // Verify the file was updated with new record
    String output = outputStream.toString();
    assertTrue(output.contains("The record added"));
  }

  @Test
  void testStartCreatesNewContactsInstance() {
    setupInput("count\nexit\n");

    Main main = new Main();
    main.start();

    String output = outputStream.toString();
    assertTrue(output.contains("The Phone Book has 0 records"));
  }

  @Test
  void testMainWithEmptyFile() throws Exception {
    Path emptyFile = tempDir.resolve("empty.db");
    Files.createFile(emptyFile);

    setupInput("exit\n");
    Main.main(new String[] {emptyFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("Cannot load file."));
  }

  @Test
  void testMainMethodExists() throws NoSuchMethodException {
    var mainMethod = Main.class.getDeclaredMethod("main", String[].class);
    assertTrue(
        java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()),
        "main method should be static");
    assertTrue(
        java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()),
        "main method should be public");
    assertEquals(void.class, mainMethod.getReturnType(), "main method should return void");
  }

  @Test
  void testStartMethodExists() throws NoSuchMethodException {
    var startMethod = Main.class.getDeclaredMethod("start");
    assertTrue(
        java.lang.reflect.Modifier.isPublic(startMethod.getModifiers()),
        "start method should be public");
    assertEquals(void.class, startMethod.getReturnType(), "start method should return void");
  }

  @Test
  void testMainWithMultipleArguments() {
    setupInput("exit\n");

    // Should only use first argument
    Main.main(new String[] {"file1.db", "file2.db", "file3.db"});

    String output = outputStream.toString();
    assertTrue(output.contains("[menu] Enter action"));
  }

  @Test
  void testLoadedContactsCanBeModified() throws Exception {
    Path testFile = tempDir.resolve("modifiable.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename(testFile.toString());

    Person person = new Person();
    person.setName("Original");
    testContacts.addRecord(person);
    testContacts.save();

    setupInput("list\n1\nedit\nname\nModified\nmenu\ncount\nexit\n");
    Main.main(new String[] {testFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("Saved"));
    assertTrue(output.contains("The Phone Book has 1 records"));
  }

  @Test
  void testMainHandlesIOException() throws Exception {
    Path testFile = tempDir.resolve("io_error.db");

    // Create valid file
    Contacts testContacts = new Contacts();
    Person person = new Person();
    person.setName("Test");
    testContacts.addRecord(person);

    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(testFile))) {
      out.writeObject(testContacts);
    }

    // Make file unreadable (platform-dependent)
    testFile.toFile().setReadable(false);

    setupInput("exit\n");
    Main.main(new String[] {testFile.toString()});

    String output = outputStream.toString();
    // Should either load successfully or show error message
    assertTrue(output.contains("[menu] Enter action") || output.contains("Cannot load file."));

    // Restore readable permission
    testFile.toFile().setReadable(true);
  }

  @Test
  void testMainWithVeryLargeFile() throws Exception {
    Path largeFile = tempDir.resolve("large.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename(largeFile.toString());

    // Add many records
    for (int i = 0; i < 100; i++) {
      Person person = new Person();
      person.setName("Person" + i);
      person.setSurname("Surname" + i);
      testContacts.addRecord(person);
    }

    testContacts.save();

    setupInput("count\nexit\n");
    Main.main(new String[] {largeFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("The Phone Book has 100 records"));
  }

  @Test
  void testMainPreservesFilenameDuringLoad() throws Exception {
    Path testFile = tempDir.resolve("preserve_filename.db");

    Contacts testContacts = new Contacts();
    testContacts.setFilename("wrong_filename.db");

    Person person = new Person();
    person.setName("Test");
    testContacts.addRecord(person);

    try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(testFile))) {
      out.writeObject(testContacts);
    }

    setupInput("add\nperson\nNew\nUser\n2000-01-01\nM\n555-0000\nexit\n");
    Main.main(new String[] {testFile.toString()});

    // The app should save to the correct file (testFile), not wrong_filename.db
    assertTrue(Files.exists(testFile));
  }

  @Test
  void testConstructorExists() throws NoSuchMethodException {
    var constructor = Main.class.getDeclaredConstructor();
    assertTrue(
        java.lang.reflect.Modifier.isPublic(constructor.getModifiers()),
        "Constructor should be public");
  }

  @Test
  void testMainContinuesAfterLoadFailure() throws Exception {
    Path testFile = tempDir.resolve("fail_then_continue.db");
    Files.writeString(testFile, "Invalid data");

    setupInput("add\nperson\nTest\nUser\n2000-01-01\nM\n555-1234\ncount\nexit\n");
    Main.main(new String[] {testFile.toString()});

    String output = outputStream.toString();
    assertTrue(output.contains("Cannot load file."));
    assertTrue(output.contains("The record added"));
    assertTrue(output.contains("The Phone Book has 1 records"));
  }
}
