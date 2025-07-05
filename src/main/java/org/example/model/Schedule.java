package org.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a schedule containing a collection of time slots with conflict-free scheduling.
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
     * Adds a time slot to this schedule if it doesn't conflict with existing slots.
     *
     * @param timeSlot The time slot to add
     * @return true if the time slot was added successfully, false if there was a conflict
     */
    public boolean addTimeSlot(TimeSlot timeSlot) {
        // Check for conflicts with existing time slots
        if (hasConflict(timeSlot)) {
            return false;
        }

        if (!timeSlots.contains(timeSlot)) {
            return timeSlots.add(timeSlot);
        }
        return false;
    }

    /**
     * Checks if a time slot conflicts with any existing time slots in the schedule.
     *
     * @param newTimeSlot The time slot to check for conflicts
     * @return true if there is a conflict, false otherwise
     */
    public boolean hasConflict(TimeSlot newTimeSlot) {
        for (TimeSlot existingSlot : timeSlots) {
            if (existingSlot.overlaps(newTimeSlot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets all conflicting time slots for a given time slot.
     *
     * @param timeSlot The time slot to check conflicts for
     * @return A list of conflicting time slots
     */
    public List<TimeSlot> getConflictingSlots(TimeSlot timeSlot) {
        List<TimeSlot> conflicts = new ArrayList<>();
        for (TimeSlot existingSlot : timeSlots) {
            if (existingSlot.overlaps(timeSlot)) {
                conflicts.add(existingSlot);
            }
        }
        return conflicts;
    }

    /**
     * Attempts to add a time slot and returns information about conflicts.
     *
     * @param timeSlot The time slot to add
     * @return A ScheduleResult containing success status and conflict information
     */
    public ScheduleResult addTimeSlotWithConflictInfo(TimeSlot timeSlot) {
        List<TimeSlot> conflicts = getConflictingSlots(timeSlot);

        if (conflicts.isEmpty()) {
            boolean added = addTimeSlot(timeSlot);
            return new ScheduleResult(added, new ArrayList<>());
        } else {
            return new ScheduleResult(false, conflicts);
        }
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

    /**
     * Result class for schedule operations that provides conflict information.
     */
    public static class ScheduleResult {
        private final boolean success;
        private final List<TimeSlot> conflicts;

        public ScheduleResult(boolean success, List<TimeSlot> conflicts) {
            this.success = success;
            this.conflicts = conflicts;
        }

        public boolean isSuccess() {
            return success;
        }

        public List<TimeSlot> getConflicts() {
            return conflicts;
        }

        public boolean hasConflicts() {
            return !conflicts.isEmpty();
        }
    }
}
