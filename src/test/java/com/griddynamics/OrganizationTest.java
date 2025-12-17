package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Organization class. Tests all functionality including validation, creation,
 * and field editing.
 */
class OrganizationTest {

  private Organization organization;
  private ByteArrayOutputStream outputStream;
  private PrintStream originalOut;

  @BeforeEach
  void setUp() {
    organization = new Organization();

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
  void testDefaultConstructor() {
    assertNotNull(organization);
    assertEquals("", organization.getName());
    assertEquals("", organization.getAddress());
    assertEquals("[no number]", organization.getPhone());
  }

  @Test
  void testGettersAndSetters() {
    organization.setName("Acme Corp");
    organization.setAddress("123 Main St");
    organization.setPhone("+1-555-1234");

    assertEquals("Acme Corp", organization.getName());
    assertEquals("123 Main St", organization.getAddress());
    assertEquals("+1-555-1234", organization.getPhone());
  }

  @Test
  void testSettersWithNull() {
    organization.setName(null);
    organization.setAddress(null);
    organization.setPhone(null);

    assertEquals("", organization.getName());
    assertEquals("", organization.getAddress());
    assertEquals("[no number]", organization.getPhone());
  }

  @Test
  void testShortInfo() {
    organization.setName("TechCorp");
    assertEquals("TechCorp", organization.shortInfo());
  }

  @Test
  void testShortInfoWithEmptyName() {
    assertEquals("", organization.shortInfo());
  }

  @Test
  void testCreateWithValidInput() {
    String simulatedInput = "Microsoft\nOne Microsoft Way, Redmond\n+1-425-882-8080\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals("Microsoft", org.getName());
    assertEquals("One Microsoft Way, Redmond", org.getAddress());
    assertEquals("+1-425-882-8080", org.getPhone());

    String output = outputStream.toString();
    assertFalse(output.contains("The record added."));
  }

  @Test
  void testCreateWithInvalidPhone() {
    String simulatedInput = "Apple Inc\nCupertino, CA\nabc-def-ghij\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals("Apple Inc", org.getName());
    assertEquals("Cupertino, CA", org.getAddress());
    assertEquals("[no number]", org.getPhone());

    String output = outputStream.toString();
    assertTrue(output.contains("Bad phone number!"));
  }

  @Test
  void testCreateWithEmptyPhone() {
    String simulatedInput = "Google\nMountain View, CA\n\n";
    Scanner scanner =
        new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals("[no number]", org.getPhone());

    String output = outputStream.toString();
    assertTrue(output.contains("Bad phone number!"));
  }

  @Test
  void testCreateWithNullScanner() {
    assertThrows(IllegalArgumentException.class, () -> Organization.create(null));
  }

  @Test
  void testMatchesCaseInsensitive() {
    organization.setName("Amazon");
    organization.setAddress("Seattle, WA");
    organization.setPhone("206-266-1000");

    assertTrue(organization.matches("amazon"));
    assertTrue(organization.matches("AMAZON"));
    assertTrue(organization.matches("Amazon"));
    assertTrue(organization.matches("seattle"));
    assertTrue(organization.matches("SEATTLE"));
    assertTrue(organization.matches("206"));
    assertTrue(organization.matches("wa"));
  }

  @Test
  void testMatchesReturnsFalse() {
    organization.setName("Tesla");
    organization.setAddress("Austin, TX");

    assertFalse(organization.matches("SpaceX"));
    assertFalse(organization.matches("California"));
    assertFalse(organization.matches("999"));
  }

  @Test
  void testMatchesWithNullPattern() {
    organization.setName("Test");
    assertFalse(organization.matches(null));
  }

  @Test
  void testMatchesWithEmptyPattern() {
    organization.setName("Test");
    assertFalse(organization.matches(""));
  }

  @Test
  void testMatchesWithInvalidRegex() {
    organization.setName("Test");
    // Invalid regex pattern should return false, not throw exception
    assertFalse(organization.matches("[invalid"));
  }

  @Test
  void testMatchesWithPartialMatch() {
    organization.setName("International Business Machines");
    organization.setAddress("Armonk, New York");

    assertTrue(organization.matches("Business"));
    assertTrue(organization.matches("International"));
    assertTrue(organization.matches("Armonk"));
    assertTrue(organization.matches("New"));
  }

  @Test
  void testApplyEditName() {
    organization.applyEdit("name", "NewCompany");
    assertEquals("NewCompany", organization.getName());
  }

  @Test
  void testApplyEditAddress() {
    organization.applyEdit("address", "456 Oak Ave");
    assertEquals("456 Oak Ave", organization.getAddress());
  }

  @Test
  void testApplyEditPhoneValid() {
    organization.applyEdit("number", "555-1234");
    assertEquals("555-1234", organization.getPhone());
  }

  @Test
  void testApplyEditPhoneInvalid() {
    organization.applyEdit("number", "invalid");
    assertEquals("[no number]", organization.getPhone());
    assertTrue(outputStream.toString().contains("Bad phone number!"));
  }

  @Test
  void testApplyEditUnknownField() {
    organization.setName("Original");
    organization.applyEdit("unknown", "value");
    assertEquals("Original", organization.getName());
  }

  @Test
  void testApplyEditWithNullField() {
    organization.setName("Original");
    organization.applyEdit(null, "value");
    assertEquals("Original", organization.getName());
  }

  @Test
  void testApplyEditUpdatesTimestamp() throws InterruptedException {
    LocalDateTime originalLastEdited = organization.getLastEdited();

    Thread.sleep(10);
    organization.applyEdit("name", "Updated Name");

    assertTrue(organization.getLastEdited().isAfter(originalLastEdited));
  }

  @Test
  void testGetEditableFields() {
    List<String> fields = organization.getEditableFields();
    assertEquals(3, fields.size());
    assertEquals(List.of("name", "address", "number"), fields);
  }

  @Test
  void testToStringContainsAllFields() {
    organization.setName("IBM");
    organization.setAddress("New York");
    organization.setPhone("555-0100");

    String output = organization.toString();

    assertTrue(output.contains("Organization name: IBM"));
    assertTrue(output.contains("Address: New York"));
    assertTrue(output.contains("Number: 555-0100"));
    assertTrue(output.contains("Time created:"));
    assertTrue(output.contains("Time last edit:"));
  }

  @Test
  void testToStringWithDefaultValues() {
    String output = organization.toString();

    assertTrue(output.contains("Organization name: "));
    assertTrue(output.contains("Address: "));
    assertTrue(output.contains("Number: [no number]"));
  }

  @Test
  void testEqualsWithSameObject() {
    assertTrue(organization.equals(organization));
  }

  @Test
  void testEqualsWithNull() {
    assertFalse(organization.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    assertFalse(organization.equals("not an organization"));
  }

  @Test
  void testEqualsWithEqualOrganizations() {
    Organization org1 = new Organization();
    org1.setName("Acme");
    org1.setAddress("123 Main");
    org1.setPhone("555-1234");

    Organization org2 = new Organization();
    org2.setName("Acme");
    org2.setAddress("123 Main");
    org2.setPhone("555-1234");

    // Note: They won't be equal because timestamps will be different
    // This is expected behavior for Record subclasses
    assertFalse(org1.equals(org2));
  }

  @Test
  void testEqualsWithDifferentNames() {
    Organization org1 = new Organization();
    org1.setName("Company A");

    Organization org2 = new Organization();
    org2.setName("Company B");

    assertFalse(org1.equals(org2));
  }

  @Test
  void testHashCodeConsistency() {
    organization.setName("Test Corp");
    organization.setAddress("Test Address");

    int hash1 = organization.hashCode();
    int hash2 = organization.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  void testHashCodeIncludesSuperClass() {
    Organization org1 = new Organization();
    org1.setName("Same Name");
    org1.setAddress("Same Address");
    org1.setPhone("555-1234");

    Organization org2 = new Organization();
    org2.setName("Same Name");
    org2.setAddress("Same Address");
    org2.setPhone("555-1234");

    // Hash codes will be different because timestamps are different
    assertNotEquals(org1.hashCode(), org2.hashCode());
  }

  @Test
  void testPhoneValidationWithVariousFormats() {
    testPhoneFormat("+1-234-567-8900", true);
    testPhoneFormat("(555) 123-4567", true);
    testPhoneFormat("555-1234", true);
    testPhoneFormat("555.123.4567", true);
    testPhoneFormat("+44 20 7123 4567", true);
    testPhoneFormat("1234567890", true);
    testPhoneFormat("123 456 7890", true);
    testPhoneFormat("abc", false);
    testPhoneFormat("no-digits-here", false);
    testPhoneFormat("", false);
    testPhoneFormat("   ", false);
  }

  private void testPhoneFormat(String phone, boolean shouldBeValid) {
    outputStream.reset(); // Clear previous output

    String input = "TestOrg\nTest Address\n" + phone + "\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    if (shouldBeValid) {
      assertEquals(phone, org.getPhone());
    } else {
      assertEquals("[no number]", org.getPhone());
    }
  }

  @Test
  void testMultipleEditsUpdateTimestamp() throws InterruptedException {
    LocalDateTime start = organization.getLastEdited();

    Thread.sleep(10);
    organization.applyEdit("name", "Name1");
    LocalDateTime after1 = organization.getLastEdited();

    Thread.sleep(10);
    organization.applyEdit("address", "Address1");
    LocalDateTime after2 = organization.getLastEdited();

    Thread.sleep(10);
    organization.applyEdit("number", "123-456-7890");
    LocalDateTime after3 = organization.getLastEdited();

    assertTrue(after1.isAfter(start));
    assertTrue(after2.isAfter(after1));
    assertTrue(after3.isAfter(after2));
  }

  @Test
  void testCreateWithLongOrganizationName() {
    String longName = "International Business Machines Corporation Limited Inc.";
    String input = longName + "\n123 Main St\n555-1234\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals(longName, org.getName());
  }

  @Test
  void testCreateWithMultilineAddress() {
    String input = "Acme Corp\n123 Main St, Suite 100, Building A\n555-1234\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals("123 Main St, Suite 100, Building A", org.getAddress());
  }

  @Test
  void testCreateWithSpecialCharactersInName() {
    String input = "O'Reilly & Associates, Inc.\n123 Main\n555-1234\n";
    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));

    Organization org = Organization.create(scanner);

    assertEquals("O'Reilly & Associates, Inc.", org.getName());
  }

  @Test
  void testMatchesWithRegexSpecialCharacters() {
    organization.setName("AT&T");
    organization.setAddress("123 Main St.");

    assertTrue(organization.matches("AT&T"));
    assertTrue(organization.matches("Main St\\."));
    assertTrue(organization.matches("123"));
  }

  @Test
  void testEmptyFieldsAfterDefaultConstruction() {
    Organization org = new Organization();

    assertEquals("", org.getName());
    assertEquals("", org.getAddress());
    assertEquals("[no number]", org.getPhone());
    assertNotNull(org.getCreated());
    assertNotNull(org.getLastEdited());
  }

  @Test
  void testInternationalPhoneNumbers() {
    testPhoneFormat("+1 (555) 123-4567", true);
    testPhoneFormat("+44 20 7946 0958", true);
    testPhoneFormat("+86 10 6554 9966", true);
    testPhoneFormat("+7 495 123-45-67", true);
    testPhoneFormat("+33 1 42 86 82 00", true);
  }

  @Test
  void testApplyEditWithEmptyValues() {
    organization.setName("Original Name");
    organization.setAddress("Original Address");

    organization.applyEdit("name", "");
    organization.applyEdit("address", "");

    assertEquals("", organization.getName());
    assertEquals("", organization.getAddress());
  }

  @Test
  void testSerializationCompatibility() {
    // Verify serialVersionUID is present
    try {
      java.lang.reflect.Field field = Organization.class.getDeclaredField("serialVersionUID");
      field.setAccessible(true);
      long serialUID = field.getLong(null);
      assertEquals(1L, serialUID);
    } catch (Exception e) {
      fail("serialVersionUID should be accessible and equal to 1L");
    }
  }

  @Test
  void testClassIsFinal() {
    assertTrue(
        java.lang.reflect.Modifier.isFinal(Organization.class.getModifiers()),
        "Organization class should be final");
  }

  @Test
  void testExtendsRecord() {
    assertTrue(
        Record.class.isAssignableFrom(Organization.class), "Organization should extend Record");
  }
}
