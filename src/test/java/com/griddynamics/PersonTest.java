package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Person class. Tests all functionality including validation, creation, and
 * field editing.
 */
class PersonTest {

  private Person person;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    person = new Person();

    // Capture System.out for validation message testing
    outputStream = new ByteArrayOutputStream();
    originalOut = System.out;
    System.setOut(new PrintStream(outputStream));
  }

  @AfterEach
  void tearDown() {
    // Restore original System.out
    System.setOut(originalOut);
  }

  @Test
  void testGettersAndSetters() {
    person.setName("John");
    person.setSurname("Doe");
    person.setBirth("2000-01-01");
    person.setGender("M");
    person.setPhone("+123456789");

    assertEquals("John", person.getName());
    assertEquals("Doe", person.getSurname());
    assertEquals("2000-01-01", person.getBirth());
    assertEquals("M", person.getGender());
    assertEquals("+123456789", person.getPhone());
  }

  @Test
  void testSettersWithNull() {
    person.setName(null);
    person.setSurname(null);
    person.setBirth(null);
    person.setGender(null);
    person.setPhone(null);

    assertEquals("", person.getName());
    assertEquals("", person.getSurname());
    assertEquals("[no data]", person.getBirth());
    assertEquals("[no data]", person.getGender());
    assertEquals("[no number]", person.getPhone());
  }

  @Test
  void testShortInfo() {
    person.setName("John");
    person.setSurname("Doe");

    assertEquals("John Doe", person.shortInfo());
  }

  @Test
  void testShortInfoWithEmptyFields() {
    assertEquals(" ", person.shortInfo());
  }

  @Test
  void testCreateWithValidInput() {
    String simulatedInput = "John\nDoe\n2000-01-01\nM\n+1-234-567-8900\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    assertEquals("John", p.getName());
    assertEquals("Doe", p.getSurname());
    assertEquals("2000-01-01", p.getBirth());
    assertEquals("M", p.getGender());
    assertEquals("+1-234-567-8900", p.getPhone());

    String output = outputStream.toString();
    assertFalse(output.contains("The record added."));
  }

  @Test
  void testCreateWithInvalidBirthDate() {
    String simulatedInput = "Alice\nSmith\nnot-a-date\nF\n555-1234\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    assertEquals("Alice", p.getName());
    assertEquals("Smith", p.getSurname());
    assertEquals("[no data]", p.getBirth());
    assertEquals("F", p.getGender());
    assertEquals("555-1234", p.getPhone());

    String output = outputStream.toString();
    assertTrue(output.contains("Bad birth date!"));
  }

  @Test
  void testCreateWithInvalidGender() {
    String simulatedInput = "Bob\nJones\n1985-03-15\nX\n123-456-7890\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    assertEquals("[no data]", p.getGender());

    String output = outputStream.toString();
    assertTrue(output.contains("Bad gender!"));
  }

  @Test
  void testCreateWithInvalidPhone() {
    String simulatedInput = "Charlie\nBrown\n1990-12-25\nM\nabc-def-ghij\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    assertEquals("[no number]", p.getPhone());

    String output = outputStream.toString();
    assertTrue(output.contains("Bad phone number!"));
  }

  @Test
  void testCreateWithNullScanner() {
    assertThrows(IllegalArgumentException.class, () -> Person.create(null));
  }

  @Test
  void testMatchesCaseInsensitive() {
    person.setName("Alice");
    person.setSurname("Smith");
    person.setBirth("1990-05-10");
    person.setGender("F");
    person.setPhone("123-456-7890");

    assertTrue(person.matches("alice"));
    assertTrue(person.matches("ALICE"));
    assertTrue(person.matches("Alice"));
    assertTrue(person.matches("smith"));
    assertTrue(person.matches("SMITH"));
    assertTrue(person.matches("1990"));
    assertTrue(person.matches("f"));
    assertTrue(person.matches("F"));
    assertTrue(person.matches("123"));
    assertTrue(person.matches("456"));
  }

  @Test
  void testMatchesReturnsFalse() {
    person.setName("Alice");
    person.setSurname("Smith");

    assertFalse(person.matches("Bob"));
    assertFalse(person.matches("Jones"));
    assertFalse(person.matches("xyz"));
  }

  @Test
  void testMatchesWithNullPattern() {
    person.setName("Test");
    assertFalse(person.matches(null));
  }

  @Test
  void testMatchesWithEmptyPattern() {
    person.setName("Test");
    assertFalse(person.matches(""));
  }

  @Test
  void testMatchesWithInvalidRegex() {
    person.setName("Test");
    // Invalid regex pattern should return false, not throw exception
    assertFalse(person.matches("[invalid"));
  }

  @Test
  void testApplyEditName() {
    person.applyEdit("name", "NewName");
    assertEquals("NewName", person.getName());
  }

  @Test
  void testApplyEditSurname() {
    person.applyEdit("surname", "NewSurname");
    assertEquals("NewSurname", person.getSurname());
  }

  @Test
  void testApplyEditBirthValid() {
    person.applyEdit("birth", "1995-06-15");
    assertEquals("1995-06-15", person.getBirth());
  }

  @Test
  void testApplyEditBirthInvalid() {
    person.applyEdit("birth", "invalid-date");
    assertEquals("[no data]", person.getBirth());
    assertTrue(outputStream.toString().contains("Bad birth date!"));
  }

  @Test
  void testApplyEditGenderValid() {
    person.applyEdit("gender", "M");
    assertEquals("M", person.getGender());

    person.applyEdit("gender", "F");
    assertEquals("F", person.getGender());
  }

  @Test
  void testApplyEditGenderInvalid() {
    person.applyEdit("gender", "X");
    assertEquals("[no data]", person.getGender());
    assertTrue(outputStream.toString().contains("Bad gender!"));
  }

  @Test
  void testApplyEditPhoneValid() {
    person.applyEdit("number", "123-456-7890");
    assertEquals("123-456-7890", person.getPhone());
  }

  @Test
  void testApplyEditPhoneInvalid() {
    person.applyEdit("number", "abc");
    assertEquals("[no number]", person.getPhone());
    assertTrue(outputStream.toString().contains("Bad phone number!"));
  }

  @Test
  void testApplyEditUnknownField() {
    person.setName("Original");
    person.applyEdit("unknown", "value");
    // Should not change anything
    assertEquals("Original", person.getName());
  }

  @Test
  void testApplyEditWithNullField() {
    person.setName("Original");
    person.applyEdit(null, "value");
    // Should not throw exception or change anything
    assertEquals("Original", person.getName());
  }

  @Test
  void testGetEditableFields() {
    List<String> fields = person.getEditableFields();
    assertEquals(5, fields.size());
    assertEquals(List.of("name", "surname", "birth", "gender", "number"), fields);
  }

  @Test
  void testToStringContainsAllFields() {
    person.setName("John");
    person.setSurname("Doe");
    person.setBirth("2000-01-01");
    person.setGender("M");
    person.setPhone("+1-234-567-8900");

    String output = person.toString();

    assertTrue(output.contains("Name: John"));
    assertTrue(output.contains("Surname: Doe"));
    assertTrue(output.contains("Birth date: 2000-01-01"));
    assertTrue(output.contains("Gender: M"));
    assertTrue(output.contains("Number: +1-234-567-8900"));
  }

  @Test
  void testToStringWithDefaultValues() {
    String output = person.toString();

    assertTrue(output.contains("Name: "));
    assertTrue(output.contains("Surname: "));
    assertTrue(output.contains("Birth date: [no data]"));
    assertTrue(output.contains("Gender: [no data]"));
    assertTrue(output.contains("Number: [no number]"));
  }

  @Test
  void testEqualsWithSameObject() {
    assertTrue(person.equals(person));
  }

  @Test
  void testEqualsWithNull() {
    assertFalse(person.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    assertFalse(person.equals("not a person"));
  }

  @Test
  void testEqualsWithEqualPersons() {
    Person p1 = new Person();
    p1.setName("John");
    p1.setSurname("Doe");
    p1.setBirth("2000-01-01");
    p1.setGender("M");
    p1.setPhone("123-456-7890");

    Person p2 = new Person();
    p2.setName("John");
    p2.setSurname("Doe");
    p2.setBirth("2000-01-01");
    p2.setGender("M");
    p2.setPhone("123-456-7890");

    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));
  }

  @Test
  void testEqualsWithDifferentPersons() {
    Person p1 = new Person();
    p1.setName("John");

    Person p2 = new Person();
    p2.setName("Jane");

    assertFalse(p1.equals(p2));
  }

  @Test
  void testHashCodeConsistency() {
    person.setName("John");
    person.setSurname("Doe");

    int hash1 = person.hashCode();
    int hash2 = person.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  void testHashCodeEqualityForEqualObjects() {
    Person p1 = new Person();
    p1.setName("John");
    p1.setSurname("Doe");

    Person p2 = new Person();
    p2.setName("John");
    p2.setSurname("Doe");

    assertEquals(p1.hashCode(), p2.hashCode());
  }

  @Test
  void testPhoneValidationWithVariousFormats() {
    // Test with Scanner since validation methods are private
    testPhoneFormat("+1-234-567-8900", true);
    testPhoneFormat("(555) 123-4567", true);
    testPhoneFormat("555-1234", true);
    testPhoneFormat("555.123.4567", true);
    testPhoneFormat("+44 20 7123 4567", true);
    testPhoneFormat("1234567890", true);
    testPhoneFormat("abc", false);
    testPhoneFormat("no-digits-here", false);
    testPhoneFormat("", false);
  }

  private void testPhoneFormat(String phone, boolean shouldBeValid) {
    outputStream.reset(); // Clear previous output

    String input = "Test\nUser\n2000-01-01\nM\n" + phone + "\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    if (shouldBeValid) {
      assertEquals(phone, p.getPhone());
    } else {
      assertEquals("[no number]", p.getPhone());
    }
  }

  @Test
  void testBirthValidationWithEmptyString() {
    String input = "Test\nUser\n\nM\n123456\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Person p = Person.create(scanner);

    assertEquals("[no data]", p.getBirth());
    assertTrue(outputStream.toString().contains("Bad birth date!"));
  }

  @Test
  void testGenderValidationAcceptsBothCases() {
    person.applyEdit("gender", "M");
    assertEquals("M", person.getGender());

    person.applyEdit("gender", "F");
    assertEquals("F", person.getGender());
  }

  @Test
  void testMatchesWithPartialMatch() {
    person.setName("Alexander");
    person.setSurname("Hamilton");

    assertTrue(person.matches("Alex"));
    assertTrue(person.matches("Hamilton"));
    assertTrue(person.matches("ander"));
    assertTrue(person.matches("ilton"));
  }
}
