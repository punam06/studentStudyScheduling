package org.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a schedule containing a collection of time slots.
 */
public class Schedule {
    private List<TimeSlot> timeSlots;
    private Member owner;

    /**
     * Creates a new schedule for the specified member.
     *
     * @param owner The member who owns this schedule
     */
    public Schedule(Member owner) {
        this.timeSlots = new ArrayList<>();
        this.owner = owner;
    }

    /**
     * Gets the owner of this schedule.
     *
     * @return The member who owns this schedule
     */
    public Member getOwner() {
        return owner;
    }

    /**
     * Gets all time slots in this schedule.
     *
     * @return A list of time slots
     */
    public List<TimeSlot> getTimeSlots() {
        return new ArrayList<>(timeSlots);
    }

    /**
     * Adds a time slot to this schedule.
     *
     * @param timeSlot The time slot to add
     * @return true if the time slot was added, false otherwise
     */
    public boolean addTimeSlot(TimeSlot timeSlot) {
        if (!timeSlots.contains(timeSlot)) {
            return timeSlots.add(timeSlot);
        }
        return false;
    }

    /**
     * Removes a time slot from this schedule.
     *
     * @param timeSlot The time slot to remove
     * @return true if the time slot was removed, false otherwise
     */
    public boolean removeTimeSlot(TimeSlot timeSlot) {
        return timeSlots.remove(timeSlot);
    }

    /**
     * Gets all time slots on a specific date.
     *
     * @param date The date to filter by
     * @return A list of time slots on the specified date
     */
    public List<TimeSlot> getTimeSlotsOnDate(LocalDate date) {
        return timeSlots.stream()
                .filter(slot -> slot.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /**
     * Checks if there is availability at the specified date and time.
     *
     * @param dateTime The date and time to check
     * @return true if there is a time slot that contains the specified date and time, false otherwise
     */
    public boolean isAvailableAt(LocalDateTime dateTime) {
        return timeSlots.stream()
                .anyMatch(slot ->
                    !dateTime.isBefore(slot.getStartTime()) &&
                    !dateTime.isAfter(slot.getEndTime())
                );
    }

    /**
     * Clears all time slots from this schedule.
     */
    public void clearSchedule() {
        timeSlots.clear();
    }
}
