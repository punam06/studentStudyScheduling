package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Member;
import org.example.model.StudyGroup;
import org.example.model.TimeSlot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistent storage of study group data including members and schedules.
 */
public class DataManager {
    private static final String DATA_DIRECTORY = "data";
    private static final String MEMBERS_FILE = "members.json";
    private static final String SCHEDULES_FILE = "schedules.json";
    private static final String STUDY_GROUP_FILE = "study_group.json";

    private final ObjectMapper objectMapper;
    private final File dataDir;

    public DataManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Create data directory if it doesn't exist
        this.dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    /**
     * Saves the study group data to persistent storage.
     */
    public void saveStudyGroup(StudyGroup studyGroup) {
        try {
            // Save study group metadata
            File studyGroupFile = new File(dataDir, STUDY_GROUP_FILE);
            objectMapper.writeValue(studyGroupFile, studyGroup);

            // Save members separately for easier management
            saveMembersList(studyGroup.getMembers());

            // Save time slots separately
            saveSchedulesList(studyGroup.getTimeSlots());

            System.out.println("✅ Study group data saved successfully");
        } catch (IOException e) {
            System.err.println("❌ Error saving study group data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the study group data from persistent storage.
     */
    public StudyGroup loadStudyGroup() {
        try {
            File studyGroupFile = new File(dataDir, STUDY_GROUP_FILE);

            StudyGroup studyGroup;
            if (studyGroupFile.exists()) {
                studyGroup = objectMapper.readValue(studyGroupFile, StudyGroup.class);
                System.out.println("✅ Study group loaded from file");
            } else {
                studyGroup = new StudyGroup("My Study Group");
                System.out.println("📝 Created new study group");
            }

            // Load members
            List<Member> members = loadMembersList();
            for (Member member : members) {
                studyGroup.addMember(member);
            }

            // Load time slots
            List<TimeSlot> timeSlots = loadSchedulesList();
            for (TimeSlot timeSlot : timeSlots) {
                studyGroup.addTimeSlot(timeSlot);
            }

            System.out.println("✅ Loaded " + members.size() + " members and " + timeSlots.size() + " schedules");
            return studyGroup;

        } catch (IOException e) {
            System.err.println("❌ Error loading study group data: " + e.getMessage());
            e.printStackTrace();
            return new StudyGroup("My Study Group");
        }
    }

    /**
     * Saves the list of members to a JSON file.
     */
    private void saveMembersList(List<Member> members) {
        try {
            File membersFile = new File(dataDir, MEMBERS_FILE);
            objectMapper.writeValue(membersFile, members);
        } catch (IOException e) {
            System.err.println("❌ Error saving members: " + e.getMessage());
        }
    }

    /**
     * Loads the list of members from a JSON file.
     */
    private List<Member> loadMembersList() {
        try {
            File membersFile = new File(dataDir, MEMBERS_FILE);
            if (membersFile.exists()) {
                return objectMapper.readValue(membersFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Member.class));
            }
        } catch (IOException e) {
            System.err.println("❌ Error loading members: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Saves the list of time slots (schedules) to a JSON file.
     */
    private void saveSchedulesList(List<TimeSlot> timeSlots) {
        try {
            File schedulesFile = new File(dataDir, SCHEDULES_FILE);
            objectMapper.writeValue(schedulesFile, timeSlots);
        } catch (IOException e) {
            System.err.println("❌ Error saving schedules: " + e.getMessage());
        }
    }

    /**
     * Loads the list of time slots (schedules) from a JSON file.
     */
    private List<TimeSlot> loadSchedulesList() {
        try {
            File schedulesFile = new File(dataDir, SCHEDULES_FILE);
            if (schedulesFile.exists()) {
                return objectMapper.readValue(schedulesFile,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, TimeSlot.class));
            }
        } catch (IOException e) {
            System.err.println("❌ Error loading schedules: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Saves a new member and updates the persistent storage.
     */
    public void saveMember(Member member, StudyGroup studyGroup) {
        studyGroup.addMember(member);
        saveStudyGroup(studyGroup);
    }

    /**
     * Removes a member and updates the persistent storage.
     */
    public void removeMember(Member member, StudyGroup studyGroup) {
        studyGroup.removeMember(member);
        saveStudyGroup(studyGroup);
    }

    /**
     * Saves a new schedule and updates the persistent storage.
     */
    public void saveSchedule(TimeSlot timeSlot, StudyGroup studyGroup) {
        studyGroup.addTimeSlot(timeSlot);
        saveStudyGroup(studyGroup);
    }

    /**
     * Clears all data (for testing purposes).
     */
    public void clearAllData() {
        try {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            System.out.println("✅ All data cleared");
        } catch (Exception e) {
            System.err.println("❌ Error clearing data: " + e.getMessage());
        }
    }
}
