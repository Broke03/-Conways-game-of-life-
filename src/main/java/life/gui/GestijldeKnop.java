package life.gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

public final class GestijldeKnop extends JButton {
    public GestijldeKnop(String tekst, Icon icoon) {
        super(tekst, icoon);
        setFocusPainted(false);
        setOpaque(true);
        setBackground(AppPalet.PRIMAIR);
        setForeground(AppPalet.OPPERVLAK);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppPalet.PRIMAIR_DIEP, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        setIconTextGap(8);
        setMargin(new Insets(6, 10, 6, 10));
        setPreferredSize(new Dimension(138, 42));
    }
}
