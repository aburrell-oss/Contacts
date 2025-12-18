package com.griddynamics;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Base class for all contact record types.
 *
 * Subclasses implement editing, matching, and short-info behaviour.
 * This class is abstract and provides common timestamp functionality
 * for all record types.
 */
public abstract class Record implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Timestamp when the record was created (immutable). */
    private final LocalDateTime createdTime;

    /** Timestamp when the record was last edited. */
    private LocalDateTime lastEditedTime;

    /** Formatter used to render timestamps. */
    protected static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Constructor that initializes timestamps.
     */
    protected Record() {
        this.createdTime = LocalDateTime.now();
        this.lastEditedTime = LocalDateTime.now();
    }

    /**
     * Constructor for testing or data import with specific timestamps.
     *
     * @param created creation timestamp
     * @param lastEdited last edited timestamp
     */
    protected Record(final LocalDateTime created,
                     final LocalDateTime lastEdited) {
        this.createdTime = Objects.requireNonNull(created,
                "Created timestamp cannot be null");
        this.lastEditedTime = Objects.requireNonNull(
                lastEdited, "Last edited timestamp cannot be null");
    }

    /**
     * Updates the last-edited timestamp to now.
     */
    public void updateTimestamp() {
        this.lastEditedTime = LocalDateTime.now();
    }

    /**
     * Sets the last-edited timestamp to a specific value.
     * Primarily for testing or data import scenarios.
     *
     * @param timestamp the timestamp to set
     * @throws IllegalArgumentException if timestamp is null or
     * before creation time
     */
    protected void setLastEdited(final LocalDateTime timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be null");
        }
        if (timestamp.isBefore(createdTime)) {
            throw new IllegalArgumentException(
                    "Last edited timestamp cannot be "
                            + "before creation timestamp");
        }
        this.lastEditedTime = timestamp;
    }

    /** @return - Returns the record creation timestamp. */
    public LocalDateTime getCreated() {
        return createdTime;
    }

    /** @return - Returns the last-edited timestamp. */
    public LocalDateTime getLastEdited() {
        return lastEditedTime;
    }

    /** @return - Returns formatted creation timestamp. */
    public String getFormattedCreated() {
        return createdTime.format(FMT);
    }

    /** @return - Returns formatted last-edited timestamp. */
    public String getFormattedLastEdited() {
        return lastEditedTime.format(FMT);
    }

    /** @return - Returns a list of editable field names for this record. */
    public abstract List<String> getEditableFields();

    /**
     * Applies a change to a field of this record.
     * Updates the last-edited timestamp after successful edit.
     *
     * @param field field identifier
     * @param value new value
     */
    public abstract void applyEdit(String field, String value);

    /** @return - Returns a one-line description of the record. */
    public abstract String shortInfo();

    /**
     * Checks whether this record matches the provided pattern.
     *
     * @param pattern case-insensitive search pattern
     * @return true when any field matches
     */
    public abstract boolean matches(String pattern);

    /** Returns formatted timestamps for display. */
    @Override
    public String toString() {
        return String.format(
                "Time created: %s%nTime last edit: %s",
                createdTime.format(FMT),
                lastEditedTime.format(FMT)
        );
    }

    /** Compares this record with another based on timestamps. */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Record record = (Record) obj;
        return Objects.equals(createdTime, record.createdTime)
                && Objects.equals(lastEditedTime, record.lastEditedTime);
    }

    /** Returns hash code based on timestamps. */
    @Override
    public int hashCode() {
        return Objects.hash(createdTime, lastEditedTime);
    }
}
