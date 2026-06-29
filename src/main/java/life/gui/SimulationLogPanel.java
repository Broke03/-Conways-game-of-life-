package life.gui;

import life.model.SimulationLogListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public final class SimulationLogPanel extends JPanel implements SimulationLogListener {
    private final JTextArea textArea = new JTextArea();

    public SimulationLogPanel() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(AppPalette.SURFACE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppPalette.SURFACE_STRONG, 1, true),
                "Simulator Terminal"
        );
        titledBorder.setTitleColor(AppPalette.PRIMARY_DEEP);
        titledBorder.setTitleFont(new Font("Georgia", Font.BOLD, 14));
        setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(6, 8, 8, 8)
        ));

        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(AppPalette.TERMINAL_BACKGROUND);
        textArea.setForeground(AppPalette.TERMINAL_TEXT);
        textArea.setCaretColor(AppPalette.TERMINAL_TEXT);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        textArea.setMargin(new java.awt.Insets(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppPalette.PRIMARY_DEEP, 1, true));
        scrollPane.setPreferredSize(new Dimension(240, 160));

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void onLogMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (!textArea.getText().isEmpty()) {
                textArea.append(System.lineSeparator());
            }
            textArea.append(message);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}
