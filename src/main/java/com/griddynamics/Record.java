package com.griddynamics;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Base class for all contact record types.
 *
 * <p>Subclasses implement editing, matching and short-info behaviour.
 */
public abstract class Record implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Timestamp when the record was created. */
    private final LocalDateTime created = LocalDateTime.now();

    /** Timestamp when the record was last edited. */
    private LocalDateTime lastEdited = LocalDateTime.now();

    /** Formatter used to render timestamps. */
    protected static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Updates the last-edited timestamp to now.
     */
    public void updateTimestamp() {
        lastEdited = LocalDateTime.now();
    }

    /**
     * Returns the record creation timestamp.
     *
     * @return creation time
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * Returns the last-edited timestamp.
     *
     * @return last edited time
     */
    public LocalDateTime getLastEdited() {
        return lastEdited;
    }

    /**
     * Returns a list of editable field names for this record.
     *
     * @return editable field identifiers
     */
    public abstract List<String> getEditableFields();

    /**
     * Applies a change to a field of this record.
     *
     * @param field field identifier
     * @param value new value
     */
    public abstract void applyEdit(String field, String value);

    /**
     * Returns a one-line description of the record.
     *
     * @return summary text
     */
    public abstract String shortInfo();

    /**
     * Checks whether this record matches the provided pattern.
     *
     * @param pattern case-insensitive search pattern
     * @return true when any field matches
     */
    public abstract boolean matches(String pattern);

    /**
     * Returns formatted timestamps for display.
     *
     * @return multi-line timestamp string
     */
    @Override
    public String toString() {
        return String.format(
                "Time created: %s%nTime last edit: %s%n",
                created.format(FMT),
                lastEdited.format(FMT)
        );
    }
}
