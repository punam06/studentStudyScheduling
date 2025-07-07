package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a member of a study group with personal information and available time slots.
 */
public class Member {
    private String name;
    private String email;
    private String group; // New field for group membership
    private List<TimeSlot> availableTimeSlots;

    /**
     * Default constructor for JSON deserialization.
     */
    public Member() {
        this.availableTimeSlots = new ArrayList<>();
    }

    /**
     * Creates a new member with the specified name and email.
     *
     * @param name The name of the member
     * @param email The email address of the member
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
        this.group = null; // No group by default
        this.availableTimeSlots = new ArrayList<>();
    }

    /**
     * Creates a new member with the specified name, email, and group.
     *
     * @param name The name of the member
     * @param email The email address of the member
     * @param group The group the member belongs to
     */
    public Member(String name, String email, String group) {
        this.name = name;
        this.email = email;
        this.group = group;
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
     * Gets the group this member belongs to.
     *
     * @return The member's group, or null if not assigned to a group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group for this member.
     *
     * @param group The group name, or null to remove from group
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Checks if this member belongs to a specific group.
     *
     * @param groupName The group name to check
     * @return true if the member belongs to the specified group, false otherwise
     */
    public boolean belongsToGroup(String groupName) {
        if (group == null || groupName == null) {
            return false;
        }
        return group.equals(groupName);
    }

    /**
     * Checks if this member has a group assignment.
     *
     * @return true if the member is assigned to a group, false otherwise
     */
    public boolean hasGroup() {
        return group != null && !group.trim().isEmpty();
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
     * Sets the list of available time slots for this member.
     *
     * @param availableTimeSlots The new list of available time slots
     */
    public void setAvailableTimeSlots(List<TimeSlot> availableTimeSlots) {
        this.availableTimeSlots = availableTimeSlots != null ?
            new ArrayList<>(availableTimeSlots) : new ArrayList<>();
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

    /**
     * Gets the number of available time slots for this member.
     *
     * @return The number of available time slots
     */
    public int getAvailableSlotCount() {
        return availableTimeSlots.size();
    }

    /**
     * Clears all available time slots for this member.
     */
    public void clearAvailableSlots() {
        availableTimeSlots.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if (email != null && !email.trim().isEmpty()) {
            sb.append(" (").append(email).append(")");
        }
        if (hasGroup()) {
            sb.append(" [Group: ").append(group).append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Member member = (Member) obj;

        if (name != null ? !name.equals(member.name) : member.name != null) return false;
        if (email != null ? !email.equals(member.email) : member.email != null) return false;
        return group != null ? group.equals(member.group) : member.group == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }
}
