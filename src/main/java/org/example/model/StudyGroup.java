package org.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a study group with members and functionality to find common available time slots.
 */
public class StudyGroup {
    private String name;
    private List<Member> members;
    private LocalTime defaultStartTime;
    private LocalTime defaultEndTime;
    private int minimumMembersRequired;
    private boolean emergencyScheduling;

    /**
     * Creates a new study group with the specified name.
     *
     * @param name The name of the study group
     */
    public StudyGroup(String name) {
        this.name = name;
        this.members = new ArrayList<>();
        this.defaultStartTime = LocalTime.of(8, 0); // 8:00 AM
        this.defaultEndTime = LocalTime.of(22, 0); // 10:00 PM
        this.minimumMembersRequired = 0; // Default: all members required
        this.emergencyScheduling = false;
    }

    /**
     * Gets the name of the study group.
     *
     * @return The study group name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the study group.
     *
     * @param name The new study group name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets all members in this study group.
     *
     * @return A list of members
     */
    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }

    /**
     * Adds a member to the study group.
     *
     * @param member The member to add
     * @return true if the member was added, false if they were already in the group
     */
    public boolean addMember(Member member) {
        if (!members.contains(member)) {
            return members.add(member);
        }
        return false;
    }

    /**
     * Removes a member from the study group.
     *
     * @param member The member to remove
     * @return true if the member was removed, false otherwise
     */
    public boolean removeMember(Member member) {
        return members.remove(member);
    }

    /**
     * Gets the default start time for scheduling.
     *
     * @return The default start time
     */
    public LocalTime getDefaultStartTime() {
        return defaultStartTime;
    }

    /**
     * Sets the default start time for scheduling.
     *
     * @param defaultStartTime The new default start time
     */
    public void setDefaultStartTime(LocalTime defaultStartTime) {
        this.defaultStartTime = defaultStartTime;
    }

    /**
     * Gets the default end time for scheduling.
     *
     * @return The default end time
     */
    public LocalTime getDefaultEndTime() {
        return defaultEndTime;
    }

    /**
     * Sets the default end time for scheduling.
     *
     * @param defaultEndTime The new default end time
     */
    public void setDefaultEndTime(LocalTime defaultEndTime) {
        this.defaultEndTime = defaultEndTime;
    }

    /**
     * Gets the minimum number of members required for scheduling.
     * If set to 0, all members are required.
     *
     * @return The minimum members required
     */
    public int getMinimumMembersRequired() {
        return minimumMembersRequired;
    }

    /**
     * Sets the minimum number of members required for scheduling.
     * Set to 0 to require all members.
     *
     * @param minimumMembersRequired The minimum members required
     */
    public void setMinimumMembersRequired(int minimumMembersRequired) {
        this.minimumMembersRequired = minimumMembersRequired;
    }

    /**
     * Checks if emergency scheduling is enabled.
     *
     * @return true if emergency scheduling is enabled, false otherwise
     */
    public boolean isEmergencyScheduling() {
        return emergencyScheduling;
    }

    /**
     * Sets whether emergency scheduling is enabled.
     *
     * @param emergencyScheduling true to enable emergency scheduling, false otherwise
     */
    public void setEmergencyScheduling(boolean emergencyScheduling) {
        this.emergencyScheduling = emergencyScheduling;
    }

    /**
     * Finds common available time slots for all members of the study group on the specified date.
     *
     * @param date The date to find common time slots for
     * @param slotDurationMinutes The duration of each time slot in minutes
     * @return A list of common available time slots
     */
    public List<TimeSlot> findCommonTimeSlots(LocalDate date, int slotDurationMinutes) {
        if (members.isEmpty()) {
            return Collections.emptyList();
        }

        // Determine the required number of members
        int requiredMembers = minimumMembersRequired > 0 ?
                Math.min(minimumMembersRequired, members.size()) :
                members.size();

        List<TimeSlot> commonSlots = new ArrayList<>();

        // Generate potential time slots for the day
        List<TimeSlot> potentialSlots = generateTimeSlots(date, slotDurationMinutes);

        // For each potential slot, check if enough members are available
        for (TimeSlot slot : potentialSlots) {
            int availableMembers = 0;

            for (Member member : members) {
                if (isAvailableForSlot(member, slot)) {
                    availableMembers++;
                }

                // If we have enough members, or emergency scheduling is enabled, add the slot
                if (availableMembers >= requiredMembers || emergencyScheduling) {
                    commonSlots.add(slot);
                    break;
                }
            }
        }

        return commonSlots;
    }

    /**
     * Generates potential time slots for the specified date using the default time range.
     *
     * @param date The date to generate slots for
     * @param slotDurationMinutes The duration of each slot in minutes
     * @return A list of time slots
     */
    private List<TimeSlot> generateTimeSlots(LocalDate date, int slotDurationMinutes) {
        List<TimeSlot> slots = new ArrayList<>();

        LocalDateTime startDateTime = LocalDateTime.of(date, defaultStartTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, defaultEndTime);

        while (startDateTime.plusMinutes(slotDurationMinutes).isBefore(endDateTime) ||
               startDateTime.plusMinutes(slotDurationMinutes).equals(endDateTime)) {

            TimeSlot slot = new TimeSlot(
                startDateTime,
                startDateTime.plusMinutes(slotDurationMinutes)
            );

            slots.add(slot);
            startDateTime = startDateTime.plusMinutes(slotDurationMinutes);
        }

        return slots;
    }

    /**
     * Checks if a member is available for a specific time slot.
     *
     * @param member The member to check
     * @param timeSlot The time slot to check
     * @return true if the member is available, false otherwise
     */
    private boolean isAvailableForSlot(Member member, TimeSlot timeSlot) {
        return member.getAvailableTimeSlots().stream()
                .anyMatch(slot -> slot.overlaps(timeSlot));
    }
}
