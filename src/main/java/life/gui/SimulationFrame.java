package life.gui;

import life.controller.PlacementTool;
import life.controller.SimulationController;
import life.model.LifeSimulation;
import life.model.SimulationListener;
import life.model.SimulationSnapshot;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class SimulationFrame extends JFrame implements SimulationListener {
    private final LifeSimulation simulation;
    private final SimulationBoardPanel boardPanel;
    private final JLabel tickValueLabel = createValueLabel();
    private final JLabel conwayValueLabel = createValueLabel();
    private final JLabel alternativeValueLabel = createValueLabel();
    private final JLabel totalValueLabel = createValueLabel();
    private final JLabel speedValueLabel = createValueLabel();
    private final JLabel zoomValueLabel = createValueLabel();
    private final JLabel statusLabel = new JLabel("Editing mode");

    public SimulationFrame(LifeSimulation simulation, SimulationController controller) {
        this.simulation = simulation;
        this.boardPanel = new SimulationBoardPanel(controller, simulation.getSnapshot());

        setTitle("Conway and Alternative Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 820));
        getContentPane().setBackground(AppPalette.BACKGROUND);
        setLayout(new BorderLayout(18, 18));

        JScrollPane boardScrollPane = new JScrollPane(boardPanel);
        boardPanel.attachScrollPane(boardScrollPane);
        configureScrollPane(boardScrollPane);

        add(createHeader(controller), BorderLayout.NORTH);
        add(boardScrollPane, BorderLayout.CENTER);
        add(createSidebar(controller), BorderLayout.EAST);

        simulation.addListener(this);
        simulation.addListener(boardPanel);
        centerViewport();
    }

    @Override
    public void onSimulationChanged(SimulationSnapshot snapshot) {
        tickValueLabel.setText(Long.toString(snapshot.tickNumber()));
        conwayValueLabel.setText(Integer.toString(snapshot.conwayCellCount()));
        alternativeValueLabel.setText(Integer.toString(snapshot.alternativeCellCount()));
        totalValueLabel.setText(Integer.toString(snapshot.totalCellCount()));
        speedValueLabel.setText(snapshot.delayMillis() + " ms");
        zoomValueLabel.setText(Math.round(boardPanel.getZoomFactor() * 100) + "%");
        statusLabel.setText(snapshot.running() ? "Simulation running" : "Edit cells and press Start");
    }

    private JComponent createHeader(SimulationController controller) {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(AppPalette.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(16, 18, 0, 18));

        JPanel titlePanel = createCardPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Conway and Alternative Life");
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLabel.setForeground(AppPalette.TEXT);

        JLabel subtitleLabel = new JLabel("Left click = selected brush, right click = alternative, Shift + click = erase, Ctrl + wheel = zoom");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(AppPalette.MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitleLabel);

        JPanel controlsPanel = createCardPanel();
        controlsPanel.add(createActionButton("Start", AppIconFactory.playIcon(), event -> controller.start()));
        controlsPanel.add(createActionButton("Pause", AppIconFactory.pauseIcon(), event -> controller.pause()));
        controlsPanel.add(createActionButton("Resume", AppIconFactory.resumeIcon(), event -> controller.resume()));
        controlsPanel.add(createActionButton("Reset", AppIconFactory.resetIcon(), event -> controller.reset()));
        controlsPanel.add(createActionButton("Faster", AppIconFactory.speedUpIcon(), event -> controller.speedUp()));
        controlsPanel.add(createActionButton("Slower", AppIconFactory.slowDownIcon(), event -> controller.slowDown()));
        controlsPanel.add(createActionButton("Zoom In", AppIconFactory.zoomInIcon(), event -> updateZoomAfter(boardPanel::zoomIn)));
        controlsPanel.add(createActionButton("Zoom Out", AppIconFactory.zoomOutIcon(), event -> updateZoomAfter(boardPanel::zoomOut)));

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(controlsPanel, BorderLayout.EAST);
        return header;
    }

    private JComponent createSidebar(SimulationController controller) {
        JPanel sidebar = createCardPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(260, 560));

        JLabel panelTitle = new JLabel("Simulation panel");
        panelTitle.setFont(new Font("Georgia", Font.BOLD, 22));
        panelTitle.setForeground(AppPalette.TEXT);

        statusLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        statusLabel.setForeground(AppPalette.PRIMARY);

        sidebar.add(panelTitle);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(statusLabel);
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(createBrushSection(controller));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(createStatRow("Ticks", tickValueLabel));
        sidebar.add(createStatRow("Conway cells", conwayValueLabel));
        sidebar.add(createStatRow("Alternative cells", alternativeValueLabel));
        sidebar.add(createStatRow("Total cells", totalValueLabel));
        sidebar.add(createStatRow("Speed", speedValueLabel));
        sidebar.add(createStatRow("Zoom", zoomValueLabel));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createLegendRow("Conway", AppIconFactory.squareIcon(AppPalette.CONWAY)));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createLegendRow("Alternative", AppIconFactory.circleIcon(AppPalette.ALTERNATIVE)));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(createHintLabel("Reset keeps your original pattern, so you can test again quickly."));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 18));
        wrapper.add(sidebar, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent createBrushSection(SimulationController controller) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Brush");
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
        title.setForeground(AppPalette.TEXT);

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton conwayButton = createBrushButton("Conway cell", true);
        JRadioButton alternativeButton = createBrushButton("Alternative cell", false);
        JRadioButton eraseButton = createBrushButton("Erase cell", false);

        conwayButton.addActionListener(event -> controller.setSelectedTool(PlacementTool.CONWAY));
        alternativeButton.addActionListener(event -> controller.setSelectedTool(PlacementTool.ALTERNATIVE));
        eraseButton.addActionListener(event -> controller.setSelectedTool(PlacementTool.ERASE));

        buttonGroup.add(conwayButton);
        buttonGroup.add(alternativeButton);
        buttonGroup.add(eraseButton);

        panel.add(title);
        panel.add(Box.createVerticalStrut(10));
        panel.add(conwayButton);
        panel.add(Box.createVerticalStrut(6));
        panel.add(alternativeButton);
        panel.add(Box.createVerticalStrut(6));
        panel.add(eraseButton);
        return panel;
    }

    private JRadioButton createBrushButton(String text, boolean selected) {
        JRadioButton button = new JRadioButton(text, selected);
        button.setOpaque(false);
        button.setForeground(AppPalette.TEXT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setMargin(new Insets(3, 0, 3, 0));
        return button;
    }

    private JPanel createStatRow(String labelText, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(AppPalette.MUTED);

        row.add(label, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return row;
    }

    private JPanel createLegendRow(String text, javax.swing.Icon icon) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(AppPalette.TEXT);

        row.add(iconLabel, BorderLayout.WEST);
        row.add(textLabel, BorderLayout.CENTER);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return row;
    }

    private JLabel createHintLabel(String text) {
        JLabel label = new JLabel("<html><div style='width:210px;'>" + text + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppPalette.MUTED);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(AppPalette.TEXT);
        return label;
    }

    private StyledButton createActionButton(String label, javax.swing.Icon icon, java.awt.event.ActionListener actionListener) {
        StyledButton button = new StyledButton(label, icon);
        button.addActionListener(actionListener);
        return button;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppPalette.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppPalette.SURFACE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private void configureScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 18, 18, 0),
                BorderFactory.createLineBorder(AppPalette.SURFACE_STRONG, 1, true)
        ));
        scrollPane.getViewport().setBackground(AppPalette.SURFACE);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(32);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
    }

    private void updateZoomAfter(Runnable zoomAction) {
        zoomAction.run();
        zoomValueLabel.setText(Math.round(boardPanel.getZoomFactor() * 100) + "%");
    }

    private void centerViewport() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            int cellSize = boardPanel.getScaledCellSize();
            int x = Math.max(0, (simulation.getCenterColumn() * cellSize) - 420);
            int y = Math.max(0, (simulation.getCenterRow() * cellSize) - 280);
            boardPanel.scrollRectToVisible(new java.awt.Rectangle(x, y, 840, 560));
        });
    }
}
