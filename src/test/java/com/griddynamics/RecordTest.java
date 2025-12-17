package com.griddynamics;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Record abstract class. Uses a concrete implementation (TestRecord) to test
 * base functionality.
 */
class RecordTest {

  private TestRecord record;
  private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /** Concrete implementation of Record for testing purposes. */
  private static class TestRecord extends Record {
    private String testField = "default";

    public TestRecord() {
      super();
    }

    public TestRecord(LocalDateTime created, LocalDateTime lastEdited) {
      super(created, lastEdited);
    }

    @Override
    public List<String> getEditableFields() {
      return List.of("testField");
    }

    @Override
    public void applyEdit(String field, String value) {
      if ("testField".equals(field)) {
        this.testField = value;
        updateTimestamp();
      }
    }

    @Override
    public String shortInfo() {
      return "TestRecord: " + testField;
    }

    @Override
    public boolean matches(String pattern) {
      return testField.toLowerCase().contains(pattern.toLowerCase());
    }

    public String getTestField() {
      return testField;
    }
  }

  @BeforeEach
  void setUp() {
    record = new TestRecord();
  }

  @Test
  void testDefaultConstructorInitializesTimestamps() {
    assertNotNull(record.getCreated());
    assertNotNull(record.getLastEdited());
  }

  @Test
  void testCreatedAndLastEditedAreInitiallyClose() {
    LocalDateTime created = record.getCreated();
    LocalDateTime lastEdited = record.getLastEdited();

    // Should be within a second of each other
    assertTrue(created.isBefore(lastEdited) || created.isEqual(lastEdited));
    assertTrue(
        lastEdited.minusSeconds(1).isBefore(created)
            || lastEdited.minusSeconds(1).isEqual(created));
  }

  @Test
  void testParameterizedConstructor() {
    LocalDateTime created = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime lastEdited = LocalDateTime.of(2020, 1, 2, 15, 30);

    TestRecord customRecord = new TestRecord(created, lastEdited);

    assertEquals(created, customRecord.getCreated());
    assertEquals(lastEdited, customRecord.getLastEdited());
  }

  @Test
  void testParameterizedConstructorWithNullCreated() {
    LocalDateTime lastEdited = LocalDateTime.now();

    assertThrows(NullPointerException.class, () -> new TestRecord(null, lastEdited));
  }

  @Test
  void testParameterizedConstructorWithNullLastEdited() {
    LocalDateTime created = LocalDateTime.now();

    assertThrows(NullPointerException.class, () -> new TestRecord(created, null));
  }

  @Test
  void testUpdateTimestampChangesLastEdited() throws InterruptedException {
    LocalDateTime originalLastEdited = record.getLastEdited();

    // Wait a bit to ensure timestamp difference
    Thread.sleep(10);

    record.updateTimestamp();
    LocalDateTime newLastEdited = record.getLastEdited();

    assertTrue(newLastEdited.isAfter(originalLastEdited));
  }

  @Test
  void testUpdateTimestampDoesNotChangeCreated() throws InterruptedException {
    LocalDateTime originalCreated = record.getCreated();

    Thread.sleep(10);
    record.updateTimestamp();

    assertEquals(originalCreated, record.getCreated());
  }

  @Test
  void testGetCreatedReturnsImmutableValue() {
    LocalDateTime created1 = record.getCreated();
    LocalDateTime created2 = record.getCreated();

    assertEquals(created1, created2);
  }

  @Test
  void testGetFormattedCreated() {
    String formatted = record.getFormattedCreated();

    assertNotNull(formatted);
    assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}"));

    // Verify it matches the actual created timestamp
    assertEquals(record.getCreated().format(FMT), formatted);
  }

  @Test
  void testGetFormattedLastEdited() {
    String formatted = record.getFormattedLastEdited();

    assertNotNull(formatted);
    assertTrue(formatted.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}"));

    // Verify it matches the actual last edited timestamp
    assertEquals(record.getLastEdited().format(FMT), formatted);
  }

  @Test
  void testSetLastEditedWithValidTimestamp() {
    LocalDateTime newTimestamp = LocalDateTime.now().plusHours(1);

    record.setLastEdited(newTimestamp);

    assertEquals(newTimestamp, record.getLastEdited());
  }

  @Test
  void testSetLastEditedWithNullThrowsException() {
    assertThrows(IllegalArgumentException.class, () -> record.setLastEdited(null));
  }

  @Test
  void testSetLastEditedBeforeCreatedThrowsException() {
    LocalDateTime beforeCreated = record.getCreated().minusDays(1);

    assertThrows(IllegalArgumentException.class, () -> record.setLastEdited(beforeCreated));
  }

  @Test
  void testSetLastEditedWithSameAsCreatedIsValid() {
    LocalDateTime sameAsCreated = record.getCreated();

    assertDoesNotThrow(() -> record.setLastEdited(sameAsCreated));
    assertEquals(sameAsCreated, record.getLastEdited());
  }

  @Test
  void testToStringContainsFormattedTimestamps() {
    String output = record.toString();

    assertTrue(output.contains("Time created:"));
    assertTrue(output.contains("Time last edit:"));
    assertTrue(output.contains(record.getFormattedCreated()));
    assertTrue(output.contains(record.getFormattedLastEdited()));
  }

  @Test
  void testToStringFormat() {
    String output = record.toString();
    String[] lines = output.split("\n");

    assertEquals(2, lines.length);
    assertTrue(lines[0].startsWith("Time created:"));
    assertTrue(lines[1].startsWith("Time last edit:"));
  }

  @Test
  void testToStringDoesNotHaveTrailingNewline() {
    String output = record.toString();
    assertFalse(output.endsWith("\n"));
  }

  @Test
  void testEqualsWithSameObject() {
    assertTrue(record.equals(record));
  }

  @Test
  void testEqualsWithNull() {
    assertFalse(record.equals(null));
  }

  @Test
  void testEqualsWithDifferentClass() {
    assertFalse(record.equals("not a record"));
  }

  @Test
  void testEqualsWithEqualRecords() {
    LocalDateTime created = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime lastEdited = LocalDateTime.of(2020, 1, 2, 15, 30);

    TestRecord r1 = new TestRecord(created, lastEdited);
    TestRecord r2 = new TestRecord(created, lastEdited);

    assertTrue(r1.equals(r2));
    assertTrue(r2.equals(r1));
  }

  @Test
  void testEqualsWithDifferentCreatedTimes() {
    LocalDateTime created1 = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime created2 = LocalDateTime.of(2020, 1, 2, 10, 0);
    LocalDateTime lastEdited = LocalDateTime.of(2020, 1, 3, 15, 30);

    TestRecord r1 = new TestRecord(created1, lastEdited);
    TestRecord r2 = new TestRecord(created2, lastEdited);

    assertFalse(r1.equals(r2));
  }

  @Test
  void testEqualsWithDifferentLastEditedTimes() {
    LocalDateTime created = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime lastEdited1 = LocalDateTime.of(2020, 1, 2, 15, 30);
    LocalDateTime lastEdited2 = LocalDateTime.of(2020, 1, 3, 15, 30);

    TestRecord r1 = new TestRecord(created, lastEdited1);
    TestRecord r2 = new TestRecord(created, lastEdited2);

    assertFalse(r1.equals(r2));
  }

  @Test
  void testHashCodeConsistency() {
    int hash1 = record.hashCode();
    int hash2 = record.hashCode();

    assertEquals(hash1, hash2);
  }

  @Test
  void testHashCodeEqualityForEqualObjects() {
    LocalDateTime created = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime lastEdited = LocalDateTime.of(2020, 1, 2, 15, 30);

    TestRecord r1 = new TestRecord(created, lastEdited);
    TestRecord r2 = new TestRecord(created, lastEdited);

    assertEquals(r1.hashCode(), r2.hashCode());
  }

  @Test
  void testHashCodeDifferentForDifferentObjects() {
    LocalDateTime created1 = LocalDateTime.of(2020, 1, 1, 10, 0);
    LocalDateTime created2 = LocalDateTime.of(2020, 1, 2, 10, 0);
    LocalDateTime lastEdited = LocalDateTime.of(2020, 1, 3, 15, 30);

    TestRecord r1 = new TestRecord(created1, lastEdited);
    TestRecord r2 = new TestRecord(created2, lastEdited);

    assertNotEquals(r1.hashCode(), r2.hashCode());
  }

  @Test
  void testApplyEditUpdatesTimestamp() throws InterruptedException {
    LocalDateTime originalLastEdited = record.getLastEdited();

    Thread.sleep(10);
    record.applyEdit("testField", "newValue");

    assertTrue(record.getLastEdited().isAfter(originalLastEdited));
  }

  @Test
  void testConcreteMethodsWork() {
    assertEquals(List.of("testField"), record.getEditableFields());

    record.applyEdit("testField", "test123");
    assertEquals("test123", record.getTestField());

    assertEquals("TestRecord: test123", record.shortInfo());

    assertTrue(record.matches("test"));
    assertTrue(record.matches("TEST"));
    assertFalse(record.matches("xyz"));
  }

  @Test
  void testSerializability() throws IOException, ClassNotFoundException {
    record.applyEdit("testField", "serialization test");

    // Serialize
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(record);
    oos.close();

    // Deserialize
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bais);
    TestRecord deserialized = (TestRecord) ois.readObject();
    ois.close();

    // Verify
    assertEquals(record.getCreated(), deserialized.getCreated());
    assertEquals(record.getLastEdited(), deserialized.getLastEdited());
    assertEquals(record.getTestField(), deserialized.getTestField());
    assertEquals(record.shortInfo(), deserialized.shortInfo());
  }

  @Test
  void testFormatterIsAccessibleToSubclasses() {
    // The FMT field should be accessible to subclasses (protected)
    assertNotNull(TestRecord.FMT);

    // Verify the formatter works correctly by formatting a known date
    LocalDateTime testDate = LocalDateTime.of(2023, 12, 25, 14, 30);
    String formatted = testDate.format(TestRecord.FMT);
    assertEquals("2023-12-25T14:30", formatted);
  }

  @Test
  void testTimestampFormatIsConsistent() {
    LocalDateTime testTime = LocalDateTime.of(2023, 12, 25, 14, 30);
    String formatted = testTime.format(FMT);

    assertEquals("2023-12-25T14:30", formatted);
  }

  @Test
  void testMultipleUpdateTimestampCalls() throws InterruptedException {
    LocalDateTime first = record.getLastEdited();

    Thread.sleep(10);
    record.updateTimestamp();
    LocalDateTime second = record.getLastEdited();

    Thread.sleep(10);
    record.updateTimestamp();
    LocalDateTime third = record.getLastEdited();

    assertTrue(second.isAfter(first));
    assertTrue(third.isAfter(second));
  }

  @Test
  void testCreatedTimestampIsImmutable() {
    LocalDateTime created = record.getCreated();

    record.updateTimestamp();
    record.applyEdit("testField", "change");

    assertEquals(created, record.getCreated());
  }
}
