package life.gui;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class AppIconFactory {
    private AppIconFactory() {
    }

    public static Icon playIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY, graphics -> {
            Path2D triangle = new Path2D.Double();
            triangle.moveTo(5, 3);
            triangle.lineTo(15, 9);
            triangle.lineTo(5, 15);
            triangle.closePath();
            graphics.fill(triangle);
        });
    }

    public static Icon pauseIcon() {
        return new PaintedIcon(18, AppPalette.ACCENT_STRONG, graphics -> {
            graphics.fill(new RoundRectangle2D.Double(4, 3, 3.5, 12, 2, 2));
            graphics.fill(new RoundRectangle2D.Double(10.5, 3, 3.5, 12, 2, 2));
        });
    }

    public static Icon resumeIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY_SOFT, graphics -> {
            Path2D triangle = new Path2D.Double();
            triangle.moveTo(5, 3);
            triangle.lineTo(15, 9);
            triangle.lineTo(5, 15);
            triangle.closePath();
            graphics.fill(triangle);
            graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawArc(1, 1, 16, 16, 145, 250);
        });
    }

    public static Icon resetIcon() {
        return new PaintedIcon(18, AppPalette.ACCENT, graphics -> {
            graphics.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawArc(2, 2, 14, 14, 45, 250);
            Path2D arrow = new Path2D.Double();
            arrow.moveTo(10, 1.5);
            arrow.lineTo(16, 2.8);
            arrow.lineTo(13.1, 7.6);
            arrow.closePath();
            graphics.fill(arrow);
        });
    }

    public static Icon speedUpIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY, graphics -> {
            graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawLine(9, 3, 9, 15);
            graphics.drawLine(3, 9, 15, 9);
        });
    }

    public static Icon slowDownIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY, graphics -> {
            graphics.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawLine(3, 9, 15, 9);
        });
    }

    public static Icon zoomInIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY, graphics -> {
            graphics.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.draw(new Ellipse2D.Double(3, 3, 9, 9));
            graphics.drawLine(11, 11, 15, 15);
            graphics.drawLine(8, 5, 8, 10);
            graphics.drawLine(5, 8, 10, 8);
        });
    }

    public static Icon zoomOutIcon() {
        return new PaintedIcon(18, AppPalette.PRIMARY, graphics -> {
            graphics.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.draw(new Ellipse2D.Double(3, 3, 9, 9));
            graphics.drawLine(11, 11, 15, 15);
            graphics.drawLine(5, 8, 10, 8);
        });
    }

    public static Icon squareIcon(Color color) {
        return new PaintedIcon(16, color, graphics -> graphics.fill(new RoundRectangle2D.Double(2, 2, 12, 12, 3, 3)));
    }

    public static Icon alternativeIcon(Color color) {
        return new PaintedIcon(16, color, graphics -> graphics.fill(createAlternativePath(1.5, 1.5, 13)));
    }

    @FunctionalInterface
    private interface IconPainter {
        void paint(Graphics2D graphics);
    }

    private static final class PaintedIcon implements Icon {
        private final int size;
        private final Color color;
        private final IconPainter painter;

        private PaintedIcon(int size, Color color, IconPainter painter) {
            this.size = size;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.translate(x, y);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(color);
            painter.paint(graphics2D);
            graphics2D.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }

    public static Path2D createAlternativePath(double x, double y, double size) {
        double width = size;
        double height = size;
        Path2D heart = new Path2D.Double();
        heart.moveTo(x + width / 2.0, y + height * 0.92);
        heart.curveTo(x + width * 0.03, y + height * 0.60, x + width * 0.02, y + height * 0.18, x + width * 0.28, y + height * 0.18);
        heart.curveTo(x + width * 0.42, y + height * 0.18, x + width * 0.48, y + height * 0.30, x + width / 2.0, y + height * 0.38);
        heart.curveTo(x + width * 0.52, y + height * 0.30, x + width * 0.58, y + height * 0.18, x + width * 0.72, y + height * 0.18);
        heart.curveTo(x + width * 0.98, y + height * 0.18, x + width * 0.97, y + height * 0.60, x + width / 2.0, y + height * 0.92);
        heart.closePath();
        return heart;
    }
}
