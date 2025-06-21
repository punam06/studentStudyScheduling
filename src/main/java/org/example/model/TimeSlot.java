package org.example.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a time slot with a start and end time.
 */
public class TimeSlot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Creates a new time slot with the specified start and end times.
     *
     * @param startTime The start time of the slot
     * @param endTime The end time of the slot
     * @throws IllegalArgumentException if the end time is before the start time
     */
    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Gets the start time of the time slot.
     *
     * @return The start time
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the time slot.
     *
     * @return The end time
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Checks if this time slot overlaps with another time slot.
     *
     * @param other The other time slot to check against
     * @return true if the time slots overlap, false otherwise
     */
    public boolean overlaps(TimeSlot other) {
        return !endTime.isBefore(other.startTime) && !startTime.isAfter(other.endTime);
    }

    /**
     * Gets the duration of this time slot in minutes.
     *
     * @return The duration in minutes
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(startTime, timeSlot.startTime) && Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }

    @Override
    public String toString() {
        return startTime.format(FORMATTER) + " - " + endTime.format(FORMATTER);
    }
}
