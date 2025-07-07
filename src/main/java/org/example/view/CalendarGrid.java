package org.example.view;

import org.example.model.Member;
import org.example.model.TimeSlot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom panel that displays a grid where rows represent group members
 * and columns represent time slots with support for multiple time ranges.
 */
public class CalendarGrid extends JPanel {
    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 30;
    private static final int HEADER_HEIGHT = 50;
    private static final int ROW_HEADER_WIDTH = 150;
    private static final int RANGE_SEPARATOR_WIDTH = 20; // Space between time ranges
    private static final Color UNAVAILABLE_COLOR = Color.WHITE;
    private static final Color AVAILABLE_COLOR = new Color(144, 238, 144); // Light green
    private static final Color COMMON_COLOR = new Color(50, 205, 50);      // Bright green
    private static final Color SELECTED_COLOR = new Color(135, 206, 250);  // Light blue
    private static final Color SEPARATOR_COLOR = new Color(200, 200, 200); // Light gray for separator
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private List<Member> members;
    private LocalDate currentDate;
    private List<TimeRange> timeRanges;
    private int timeSlotInterval; // in minutes

    private Map<Point, CellState> cellStates;
    private Point dragStartCell;
    private JPopupMenu contextMenu;

    /**
     * Represents a time range with start and end times.
     */
    private static class TimeRange {
        final LocalTime startTime;
        final LocalTime endTime;
        final String label;

        TimeRange(LocalTime startTime, LocalTime endTime, String label) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.label = label;
        }

        int getTimeSlotCount(int interval) {
            int minutes = (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 60;
            // Handle overnight ranges (e.g., 8 PM to 1 AM)
            if (endTime.isBefore(startTime)) {
                minutes = (24 * 60) - (startTime.toSecondOfDay() / 60) + (endTime.toSecondOfDay() / 60);
            }
            return minutes / interval;
        }
    }

    /**
     * Represents the state of a cell in the grid.
     */
    private enum CellState {
        UNAVAILABLE,
        AVAILABLE,
        COMMON,
        SELECTED
    }

    /**
     * Creates a new calendar grid with default settings for study hours.
     */
    public CalendarGrid() {
        this.members = new ArrayList<>();
        this.currentDate = LocalDate.now();
        this.timeSlotInterval = 30; // 30 minutes
        this.cellStates = new HashMap<>();

        // Initialize time ranges: 8 AM to 2 PM and 8 PM to 1 AM
        this.timeRanges = new ArrayList<>();
        this.timeRanges.add(new TimeRange(LocalTime.of(8, 0), LocalTime.of(14, 0), "Morning/Afternoon"));
        this.timeRanges.add(new TimeRange(LocalTime.of(20, 0), LocalTime.of(1, 0), "Evening/Night"));

        setBackground(Color.WHITE);
        updatePreferredSize();

        setupMouseListeners();
        setupContextMenu();
    }

    /**
     * Updates the preferred size based on the number of time slots.
     */
    private void updatePreferredSize() {
        int totalTimeSlots = getTotalTimeSlots();
        int width = ROW_HEADER_WIDTH + (totalTimeSlots * CELL_WIDTH) +
                   ((timeRanges.size() - 1) * RANGE_SEPARATOR_WIDTH) + 50;
        int height = HEADER_HEIGHT + (members.size() * CELL_HEIGHT) + 50;
        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Sets up mouse listeners for cell selection.
     */
    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point cell = getCellAt(e.getX(), e.getY());
                    if (isValidCell(cell)) {
                        dragStartCell = cell;
                        toggleCellState(cell);
                        repaint();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    Point cell = getCellAt(e.getX(), e.getY());
                    if (isValidCell(cell)) {
                        showContextMenu(e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStartCell = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && dragStartCell != null) {
                    Point cell = getCellAt(e.getX(), e.getY());
                    if (isValidCell(cell) && !cell.equals(dragStartCell)) {
                        setCellState(cell, getCellState(dragStartCell));
                        repaint();
                    }
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    /**
     * Sets up the context menu for additional options.
     */
    private void setupContextMenu() {
        contextMenu = new JPopupMenu();

        JMenuItem markAvailableItem = new JMenuItem("Mark as Available");
        markAvailableItem.addActionListener(e -> {
            Point cell = getCellAt(contextMenu.getX(), contextMenu.getY());
            if (isValidCell(cell)) {
                setCellState(cell, CellState.AVAILABLE);
                repaint();
            }
        });

        JMenuItem markUnavailableItem = new JMenuItem("Mark as Unavailable");
        markUnavailableItem.addActionListener(e -> {
            Point cell = getCellAt(contextMenu.getX(), contextMenu.getY());
            if (isValidCell(cell)) {
                setCellState(cell, CellState.UNAVAILABLE);
                repaint();
            }
        });

        contextMenu.add(markAvailableItem);
        contextMenu.add(markUnavailableItem);
    }

    /**
     * Shows the context menu at the specified position.
     */
    private void showContextMenu(int x, int y) {
        contextMenu.show(this, x, y);
    }

    /**
     * Toggles the state of a cell between available and unavailable.
     */
    private void toggleCellState(Point cell) {
        CellState currentState = getCellState(cell);
        setCellState(cell, currentState == CellState.AVAILABLE ? CellState.UNAVAILABLE : CellState.AVAILABLE);
    }

    /**
     * Gets the current state of a cell.
     */
    private CellState getCellState(Point cell) {
        return cellStates.getOrDefault(cell, CellState.UNAVAILABLE);
    }

    /**
     * Sets the state of a cell.
     */
    private void setCellState(Point cell, CellState state) {
        cellStates.put(cell, state);
    }

    /**
     * Gets the total number of time slots across all ranges.
     */
    private int getTotalTimeSlots() {
        return timeRanges.stream().mapToInt(range -> range.getTimeSlotCount(timeSlotInterval)).sum();
    }

    /**
     * Gets the number of time slots in the grid.
     */
    private int getNumberOfTimeSlots() {
        return getTotalTimeSlots();
    }

    /**
     * Gets the time slot at the specified column index.
     */
    private TimeSlot getTimeSlotAt(int columnIndex) {
        int currentColumn = 0;

        for (TimeRange range : timeRanges) {
            int rangeSlots = range.getTimeSlotCount(timeSlotInterval);

            if (columnIndex < currentColumn + rangeSlots) {
                int slotIndexInRange = columnIndex - currentColumn;
                LocalTime slotStart = range.startTime.plusMinutes((long) slotIndexInRange * timeSlotInterval);
                LocalTime slotEnd = slotStart.plusMinutes(timeSlotInterval);

                // Handle overnight ranges
                LocalDate startDate = currentDate;
                LocalDate endDate = currentDate;

                if (range.endTime.isBefore(range.startTime) && slotEnd.isBefore(slotStart)) {
                    endDate = currentDate.plusDays(1);
                }

                LocalDateTime startDateTime = LocalDateTime.of(startDate, slotStart);
                LocalDateTime endDateTime = LocalDateTime.of(endDate, slotEnd);

                return new TimeSlot(startDateTime, endDateTime);
            }

            currentColumn += rangeSlots;
        }

        return null;
    }

    /**
     * Gets the cell at the specified coordinates.
     */
    private Point getCellAt(int x, int y) {
        if (x <= ROW_HEADER_WIDTH || y <= HEADER_HEIGHT) {
            return null; // Header area
        }

        int row = (y - HEADER_HEIGHT) / CELL_HEIGHT;
        int adjustedX = x - ROW_HEADER_WIDTH;

        // Calculate column considering separators between ranges
        int col = 0;
        int currentX = 0;

        for (int rangeIndex = 0; rangeIndex < timeRanges.size(); rangeIndex++) {
            TimeRange range = timeRanges.get(rangeIndex);
            int rangeSlots = range.getTimeSlotCount(timeSlotInterval);
            int rangeWidth = rangeSlots * CELL_WIDTH;

            if (adjustedX >= currentX && adjustedX < currentX + rangeWidth) {
                col += (adjustedX - currentX) / CELL_WIDTH;
                break;
            }

            currentX += rangeWidth;
            col += rangeSlots;

            // Add separator width if not the last range
            if (rangeIndex < timeRanges.size() - 1) {
                currentX += RANGE_SEPARATOR_WIDTH;
            }
        }

        return new Point(col, row);
    }

    /**
     * Checks if a cell is valid (within the grid).
     */
    private boolean isValidCell(Point cell) {
        if (cell == null) return false;

        int maxRow = members.size() - 1;
        int maxCol = getNumberOfTimeSlots() - 1;

        return cell.x >= 0 && cell.x <= maxCol && cell.y >= 0 && cell.y <= maxRow;
    }

    /**
     * Paints the component.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the grid background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw the date header
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, getWidth(), HEADER_HEIGHT);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString(currentDate.toString(), 10, 25);

        // Draw time slot headers and ranges
        int currentX = ROW_HEADER_WIDTH;
        int globalColumn = 0;

        for (int rangeIndex = 0; rangeIndex < timeRanges.size(); rangeIndex++) {
            TimeRange range = timeRanges.get(rangeIndex);
            int rangeSlots = range.getTimeSlotCount(timeSlotInterval);

            // Draw range label
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.setColor(new Color(70, 130, 180));
            g2d.drawString(range.label, currentX + 5, 15);

            // Draw time headers for this range
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.BLACK);

            for (int i = 0; i < rangeSlots; i++) {
                LocalTime time = range.startTime.plusMinutes((long) i * timeSlotInterval);

                // Handle overnight time display
                if (range.endTime.isBefore(range.startTime) && time.isBefore(range.startTime)) {
                    // This is next day time, but we'll display it normally
                }

                String timeStr = time.format(TIME_FORMATTER);
                int x = currentX + i * CELL_WIDTH + CELL_WIDTH / 2;
                g2d.drawString(timeStr, x - 15, HEADER_HEIGHT - 10);
            }

            // Draw cells for this range
            for (int i = 0; i < rangeSlots; i++) {
                for (int memberIndex = 0; memberIndex < members.size(); memberIndex++) {
                    Point cellPoint = new Point(globalColumn + i, memberIndex);
                    CellState state = getCellState(cellPoint);

                    Color cellColor = getCellColor(state);
                    g2d.setColor(cellColor);

                    int cellX = currentX + i * CELL_WIDTH;
                    int cellY = HEADER_HEIGHT + memberIndex * CELL_HEIGHT;
                    g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                }
            }

            // Draw vertical lines for this range
            g2d.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= rangeSlots; i++) {
                int x = currentX + i * CELL_WIDTH;
                g2d.drawLine(x, HEADER_HEIGHT, x, HEADER_HEIGHT + members.size() * CELL_HEIGHT);
            }

            currentX += rangeSlots * CELL_WIDTH;
            globalColumn += rangeSlots;

            // Draw separator between ranges
            if (rangeIndex < timeRanges.size() - 1) {
                g2d.setColor(SEPARATOR_COLOR);
                g2d.fillRect(currentX, HEADER_HEIGHT, RANGE_SEPARATOR_WIDTH, members.size() * CELL_HEIGHT);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawLine(currentX + RANGE_SEPARATOR_WIDTH/2, HEADER_HEIGHT,
                           currentX + RANGE_SEPARATOR_WIDTH/2, HEADER_HEIGHT + members.size() * CELL_HEIGHT);
                currentX += RANGE_SEPARATOR_WIDTH;
            }
        }

        // Draw member names
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, HEADER_HEIGHT, ROW_HEADER_WIDTH, getHeight());
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            int y = HEADER_HEIGHT + i * CELL_HEIGHT + CELL_HEIGHT / 2;
            g2d.drawString(member.getName(), 10, y + 5);
        }

        // Draw horizontal lines
        g2d.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= members.size(); i++) {
            int y = HEADER_HEIGHT + i * CELL_HEIGHT;
            g2d.drawLine(ROW_HEADER_WIDTH, y, getWidth(), y);
        }

        g2d.dispose();
    }

    /**
     * Gets the color for a cell based on its state.
     */
    private Color getCellColor(CellState state) {
        switch (state) {
            case AVAILABLE:
                return AVAILABLE_COLOR;
            case COMMON:
                return COMMON_COLOR;
            case SELECTED:
                return SELECTED_COLOR;
            case UNAVAILABLE:
            default:
                return UNAVAILABLE_COLOR;
        }
    }

    /**
     * Sets the members to display in the grid.
     */
    public void setMembers(List<Member> members) {
        this.members = new ArrayList<>(members);
        updatePreferredSize();
        repaint();
    }

    /**
     * Sets the date to display in the grid.
     */
    public void setDate(LocalDate date) {
        this.currentDate = date;
        repaint();
    }

    /**
     * Gets the current date being displayed in the grid.
     */
    public LocalDate getDate() {
        return currentDate;
    }

    /**
     * Sets custom time ranges for the grid.
     */
    public void setTimeRanges(List<TimeRange> ranges) {
        this.timeRanges = new ArrayList<>(ranges);
        updatePreferredSize();
        repaint();
    }

    /**
     * Sets the time slot interval in minutes.
     */
    public void setTimeSlotInterval(int minutes) {
        this.timeSlotInterval = minutes;
        updatePreferredSize();
        repaint();
    }

    /**
     * Marks common time slots where all members are available.
     */
    public void markCommonSlots() {
        // Reset common slots
        cellStates.entrySet().removeIf(entry -> entry.getValue() == CellState.COMMON);

        // For simplicity, we'll mark slots as common if they are available for all members
        if (members.isEmpty()) return;

        for (int col = 0; col < getNumberOfTimeSlots(); col++) {
            boolean allAvailable = true;

            for (int row = 0; row < members.size(); row++) {
                Point cell = new Point(col, row);
                if (getCellState(cell) != CellState.AVAILABLE) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                for (int row = 0; row < members.size(); row++) {
                    Point cell = new Point(col, row);
                    setCellState(cell, CellState.COMMON);
                }
            }
        }

        repaint();
    }

    /**
     * Adds a time slot to the calendar grid and marks it as scheduled.
     *
     * @param timeSlot The time slot to add
     */
    public void addTimeSlot(TimeSlot timeSlot) {
        // Convert the time slot to grid coordinates and mark as scheduled
        LocalTime slotStart = timeSlot.getStartTime().toLocalTime();
        LocalTime slotEnd = timeSlot.getEndTime().toLocalTime();

        // Calculate which time columns this slot spans
        int startColumn = getTimeColumnForTime(slotStart);
        int endColumn = getTimeColumnForTime(slotEnd);

        // Mark all affected cells as scheduled/selected
        for (int member = 0; member < members.size(); member++) {
            for (int timeCol = startColumn; timeCol < endColumn; timeCol++) {
                Point cell = new Point(member, timeCol);
                if (isValidCell(cell)) {
                    setCellState(cell, CellState.SELECTED);
                }
            }
        }

        repaint();
    }

    /**
     * Gets the time column index for a given time.
     *
     * @param time The time to find the column for
     * @return The column index, or -1 if not found
     */
    private int getTimeColumnForTime(LocalTime time) {
        int column = 0;

        for (TimeRange range : timeRanges) {
            int rangeSlots = range.getTimeSlotCount(timeSlotInterval);
            LocalTime currentTime = range.startTime;

            for (int i = 0; i < rangeSlots; i++) {
                if (currentTime.equals(time) ||
                        (currentTime.isBefore(time) && currentTime.plusMinutes(timeSlotInterval).isAfter(time))) {
                    return column;
                }
                currentTime = currentTime.plusMinutes(timeSlotInterval);
                column++;
            }
        }

        return -1;
    }

    /**
     * Gets the member at the specified row index.
     */
    private Member getMemberAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < members.size()) {
            return members.get(rowIndex);
        }
        return null;
    }

    /**
     * Gets a list of available time slots for scheduling.
     */
    public List<TimeSlot> getAvailableTimeSlots() {
        List<TimeSlot> availableSlots = new ArrayList<>();

        for (int col = 0; col < getNumberOfTimeSlots(); col++) {
            boolean allAvailable = true;

            for (int row = 0; row < members.size(); row++) {
                Point cell = new Point(col, row);
                if (getCellState(cell) != CellState.AVAILABLE) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                TimeSlot slot = getTimeSlotAt(col);
                if (slot != null) {
                    availableSlots.add(slot);
                }
            }
        }

        return availableSlots;
    }

    /**
     * Clears all cell states, resetting the grid.
     */
    public void clearGrid() {
        cellStates.clear();
        repaint();
    }

    /**
     * Gets a preview of an invitation email.
     */
    public String generatePreview(Member member, TimeSlot timeSlot, String subject, String message) {
        // This method is for compatibility with the existing email system
        return "Meeting scheduled for " + member.getName() + " at " + timeSlot.toString();
    }
}
