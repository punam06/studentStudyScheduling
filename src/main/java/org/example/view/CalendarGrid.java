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
 * and columns represent time slots.
 */
public class CalendarGrid extends JPanel {
    private static final int CELL_WIDTH = 40;
    private static final int CELL_HEIGHT = 30;
    private static final int HEADER_HEIGHT = 50;
    private static final int ROW_HEADER_WIDTH = 150;
    private static final Color UNAVAILABLE_COLOR = Color.WHITE;
    private static final Color AVAILABLE_COLOR = new Color(144, 238, 144); // Light green
    private static final Color COMMON_COLOR = new Color(50, 205, 50);      // Bright green
    private static final Color SELECTED_COLOR = new Color(135, 206, 250);  // Light blue
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private List<Member> members;
    private LocalDate currentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int timeSlotInterval; // in minutes

    private Map<Point, CellState> cellStates;
    private Point dragStartCell;
    private JPopupMenu contextMenu;

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
     * Creates a new calendar grid with default settings.
     */
    public CalendarGrid() {
        this.members = new ArrayList<>();
        this.currentDate = LocalDate.now();
        this.startTime = LocalTime.of(8, 0); // 8:00 AM
        this.endTime = LocalTime.of(22, 0);  // 10:00 PM
        this.timeSlotInterval = 30; // 30 minutes
        this.cellStates = new HashMap<>();

        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 400));

        setupMouseListeners();
        setupContextMenu();
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
     * Gets the cell at the specified coordinates.
     */
    private Point getCellAt(int x, int y) {
        if (x <= ROW_HEADER_WIDTH || y <= HEADER_HEIGHT) {
            return null; // Header area
        }

        int row = (y - HEADER_HEIGHT) / CELL_HEIGHT;
        int col = (x - ROW_HEADER_WIDTH) / CELL_WIDTH;
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
     * Gets the number of time slots in the grid.
     */
    private int getNumberOfTimeSlots() {
        int minutes = (endTime.toSecondOfDay() - startTime.toSecondOfDay()) / 60;
        return minutes / timeSlotInterval;
    }

    /**
     * Gets the time slot at the specified column index.
     */
    private TimeSlot getTimeSlotAt(int columnIndex) {
        LocalTime slotStart = startTime.plusMinutes((long) columnIndex * timeSlotInterval);
        LocalTime slotEnd = slotStart.plusMinutes(timeSlotInterval);

        LocalDateTime startDateTime = LocalDateTime.of(currentDate, slotStart);
        LocalDateTime endDateTime = LocalDateTime.of(currentDate, slotEnd);

        return new TimeSlot(startDateTime, endDateTime);
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
     * Sets the members to display in the grid.
     */
    public void setMembers(List<Member> members) {
        this.members = new ArrayList<>(members);
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
     *
     * @return The current date
     */
    public LocalDate getDate() {
        return currentDate;
    }

    /**
     * Sets the time range to display in the grid.
     */
    public void setTimeRange(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        repaint();
    }

    /**
     * Sets the time slot interval in minutes.
     */
    public void setTimeSlotInterval(int minutes) {
        this.timeSlotInterval = minutes;
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

        // Draw time slot headers
        for (int i = 0; i < getNumberOfTimeSlots(); i++) {
            LocalTime time = startTime.plusMinutes((long) i * timeSlotInterval);
            String timeStr = time.format(TIME_FORMATTER);

            int x = ROW_HEADER_WIDTH + i * CELL_WIDTH + CELL_WIDTH / 2;
            g2d.drawString(timeStr, x - 15, HEADER_HEIGHT - 10);
        }

        // Draw member names
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, HEADER_HEIGHT, ROW_HEADER_WIDTH, getHeight());
        g2d.setColor(Color.BLACK);

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            int y = HEADER_HEIGHT + i * CELL_HEIGHT + CELL_HEIGHT / 2;
            g2d.drawString(member.getName(), 10, y + 5);
        }

        // Draw grid lines
        g2d.setColor(Color.LIGHT_GRAY);

        // Vertical lines
        for (int i = 0; i <= getNumberOfTimeSlots(); i++) {
            int x = ROW_HEADER_WIDTH + i * CELL_WIDTH;
            g2d.drawLine(x, HEADER_HEIGHT, x, HEADER_HEIGHT + members.size() * CELL_HEIGHT);
        }

        // Horizontal lines
        for (int i = 0; i <= members.size(); i++) {
            int y = HEADER_HEIGHT + i * CELL_HEIGHT;
            g2d.drawLine(ROW_HEADER_WIDTH, y, ROW_HEADER_WIDTH + getNumberOfTimeSlots() * CELL_WIDTH, y);
        }

        // Draw grid cells
        for (int row = 0; row < members.size(); row++) {
            for (int col = 0; col < getNumberOfTimeSlots(); col++) {
                Point cell = new Point(col, row);
                CellState state = getCellState(cell);

                int x = ROW_HEADER_WIDTH + col * CELL_WIDTH;
                int y = HEADER_HEIGHT + row * CELL_HEIGHT;

                // Draw cell background based on state
                switch (state) {
                    case AVAILABLE:
                        g2d.setColor(AVAILABLE_COLOR);
                        break;
                    case COMMON:
                        g2d.setColor(COMMON_COLOR);
                        break;
                    case SELECTED:
                        g2d.setColor(SELECTED_COLOR);
                        break;
                    default:
                        g2d.setColor(UNAVAILABLE_COLOR);
                }

                g2d.fillRect(x + 1, y + 1, CELL_WIDTH - 1, CELL_HEIGHT - 1);
            }
        }

        g2d.dispose();
    }
}
