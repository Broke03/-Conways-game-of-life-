package life.gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

public final class StyledButton extends JButton {
    public StyledButton(String text, Icon icon) {
        super(text, icon);
        setFocusPainted(false);
        setOpaque(true);
        setBackground(AppPalette.SURFACE);
        setForeground(AppPalette.TEXT);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppPalette.SURFACE_STRONG, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        setIconTextGap(8);
        setPreferredSize(new Dimension(140, 40));
    }
}
