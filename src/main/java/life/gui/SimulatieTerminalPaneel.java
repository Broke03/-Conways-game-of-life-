package life.gui;

import life.model.SimulatieLogLuisteraar;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class SimulatieTerminalPaneel extends JPanel implements SimulatieLogLuisteraar {
    private final JTextArea tekstVak = new JTextArea();

    public SimulatieTerminalPaneel() {
        super(new BorderLayout());
        setOpaque(true);
        setBackground(AppPalet.OPPERVLAK);

        TitledBorder titelRand = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AppPalet.OPPERVLAK_STERK, 1, true),
                "Simulatie-terminal"
        );
        titelRand.setTitleColor(AppPalet.PRIMAIR_DIEP);
        titelRand.setTitleFont(new Font("Georgia", Font.BOLD, 14));
        setBorder(BorderFactory.createCompoundBorder(
                titelRand,
                BorderFactory.createEmptyBorder(6, 8, 8, 8)
        ));

        tekstVak.setEditable(false);
        tekstVak.setFocusable(false);
        tekstVak.setLineWrap(true);
        tekstVak.setWrapStyleWord(true);
        tekstVak.setBackground(AppPalet.TERMINAL_ACHTERGROND);
        tekstVak.setForeground(AppPalet.TERMINAL_TEKST);
        tekstVak.setCaretColor(AppPalet.TERMINAL_TEKST);
        tekstVak.setFont(new Font("Consolas", Font.PLAIN, 13));
        tekstVak.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(tekstVak);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppPalet.PRIMAIR_DIEP, 1, true));
        scrollPane.setPreferredSize(new Dimension(240, 160));

        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void opLogBericht(String bericht) {
        SwingUtilities.invokeLater(() -> {
            if (!tekstVak.getText().isEmpty()) {
                tekstVak.append(System.lineSeparator());
            }
            tekstVak.append(bericht);
            tekstVak.setCaretPosition(tekstVak.getDocument().getLength());
        });
    }
}
