package org.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a time slot with start and end times.
 */
public class TimeSlot {
    @JsonProperty("startTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    /**
     * Default constructor for JSON deserialization.
     */
    public TimeSlot() {
    }

    /**
     * Creates a new time slot with the specified start and end times.
     *
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time cannot be null");
        }
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = "";
        this.description = "";
    }

    /**
     * Creates a new time slot with the specified start and end times, title, and description.
     *
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     * @param title The title of the time slot
     * @param description The description of the time slot
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime, String title, String description) {
        this(startTime, endTime);
        this.title = title != null ? title : "";
        this.description = description != null ? description : "";
    }

    /**
     * Gets the start time of this time slot.
     *
     * @return The start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of this time slot.
     *
     * @param startTime The new start time
     */
    public void setStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        if (endTime != null && (startTime.isAfter(endTime) || startTime.equals(endTime))) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
        this.startTime = startTime;
    }

    /**
     * Gets the end time of this time slot.
     *
     * @return The end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time of this time slot.
     *
     * @param endTime The new end time
     */
    public void setEndTime(LocalDateTime endTime) {
        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        if (startTime != null && (startTime.isAfter(endTime) || startTime.equals(endTime))) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.endTime = endTime;
    }

    /**
     * Gets the title of this time slot.
     *
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of this time slot.
     *
     * @param title The new title
     */
    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    /**
     * Gets the description of this time slot.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of this time slot.
     *
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description != null ? description : "";
    }

    /**
     * Gets the duration of this time slot in minutes.
     *
     * @return The duration in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     *
     * @param other The other time slot to check overlap with
     * @return true if the time slots overlap, false otherwise
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null) {
            return false;
        }

        // Two time slots overlap if one starts before the other ends and vice versa
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }

    /**
     * Checks if this time slot contains the specified time.
     *
     * @param time The time to check
     * @return true if the time is within this slot, false otherwise
     */
    public boolean contains(LocalDateTime time) {
        if (time == null) {
            return false;
        }
        return !time.isBefore(startTime) && time.isBefore(endTime);
    }

    /**
     * Checks if this time slot is completely within another time slot.
     *
     * @param other The other time slot
     * @return true if this slot is within the other slot, false otherwise
     */
    public boolean isWithin(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return !startTime.isBefore(other.startTime) && !endTime.isAfter(other.endTime);
    }

    /**
     * Checks if this time slot is adjacent to another time slot.
     *
     * @param other The other time slot
     * @return true if the slots are adjacent (one ends when the other starts), false otherwise
     */
    public boolean isAdjacentTo(TimeSlot other) {
        if (other == null) {
            return false;
        }
        return endTime.equals(other.startTime) || startTime.equals(other.endTime);
    }

    /**
     * Creates a new time slot that represents the overlap between this slot and another.
     *
     * @param other The other time slot
     * @return A new TimeSlot representing the overlap, or null if no overlap exists
     */
    public TimeSlot getOverlap(TimeSlot other) {
        if (!overlaps(other)) {
            return null;
        }

        LocalDateTime overlapStart = startTime.isAfter(other.startTime) ? startTime : other.startTime;
        LocalDateTime overlapEnd = endTime.isBefore(other.endTime) ? endTime : other.endTime;

        return new TimeSlot(overlapStart, overlapEnd, "Overlap", "Overlapping time period");
    }

    /**
     * Formats the time slot as a readable string.
     *
     * @return A formatted string representation
     */
    public String formatTimeRange() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        if (startTime.toLocalDate().equals(endTime.toLocalDate())) {
            // Same day
            return startTime.format(dateFormatter) + " " +
                   startTime.format(timeFormatter) + " - " +
                   endTime.format(timeFormatter);
        } else {
            // Different days
            return startTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) + " - " +
                   endTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        TimeSlot timeSlot = (TimeSlot) obj;
        return Objects.equals(startTime, timeSlot.startTime) &&
               Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (title != null && !title.trim().isEmpty()) {
            sb.append(title).append(": ");
        }
        sb.append(formatTimeRange());
        if (description != null && !description.trim().isEmpty()) {
            sb.append(" (").append(description).append(")");
        }
        return sb.toString();
    }

    /**
     * Creates a copy of this time slot.
     *
     * @return A new TimeSlot with the same properties
     */
    public TimeSlot copy() {
        return new TimeSlot(startTime, endTime, title, description);
    }

    /**
     * Checks if this time slot is valid (has proper start and end times).
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}
