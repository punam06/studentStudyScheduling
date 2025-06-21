package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a member of a study group with personal information and available time slots.
 */
public class Member {
    private String name;
    private String email;
    private List<TimeSlot> availableTimeSlots;

    /**
     * Creates a new member with the specified name and email.
     *
     * @param name The name of the member
     * @param email The email address of the member
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
        this.availableTimeSlots = new ArrayList<>();
    }

    /**
     * Gets the name of the member.
     *
     * @return The member's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the member.
     *
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the member.
     *
     * @return The member's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the member.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the list of available time slots for this member.
     *
     * @return The list of available time slots
     */
    public List<TimeSlot> getAvailableTimeSlots() {
        return new ArrayList<>(availableTimeSlots);
    }

    /**
     * Adds a time slot to the member's availability.
     *
     * @param timeSlot The time slot to add
     * @return true if the time slot was added, false if it already exists
     */
    public boolean addTimeSlot(TimeSlot timeSlot) {
        if (!availableTimeSlots.contains(timeSlot)) {
            return availableTimeSlots.add(timeSlot);
        }
        return false;
    }

    /**
     * Removes a time slot from the member's availability.
     *
     * @param timeSlot The time slot to remove
     * @return true if the time slot was removed, false if it wasn't in the list
     */
    public boolean removeTimeSlot(TimeSlot timeSlot) {
        return availableTimeSlots.remove(timeSlot);
    }

    /**
     * Checks if a member is available during a specific time slot.
     *
     * @param timeSlot The time slot to check
     * @return true if the member is available, false otherwise
     */
    public boolean isAvailable(TimeSlot timeSlot) {
        return availableTimeSlots.contains(timeSlot);
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}
