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

public final class AppIcoonFabriek {
    private AppIcoonFabriek() {
    }

    public static Icon speelIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR, grafisch -> {
            Path2D driehoek = new Path2D.Double();
            driehoek.moveTo(5, 3);
            driehoek.lineTo(15, 9);
            driehoek.lineTo(5, 15);
            driehoek.closePath();
            grafisch.fill(driehoek);
        });
    }

    public static Icon pauzeIcoon() {
        return new GeschilderdIcoon(18, AppPalet.ACCENT_STERK, grafisch -> {
            grafisch.fill(new RoundRectangle2D.Double(4, 3, 3.5, 12, 2, 2));
            grafisch.fill(new RoundRectangle2D.Double(10.5, 3, 3.5, 12, 2, 2));
        });
    }

    public static Icon hervatIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR_ZACHT, grafisch -> {
            Path2D driehoek = new Path2D.Double();
            driehoek.moveTo(5, 3);
            driehoek.lineTo(15, 9);
            driehoek.lineTo(5, 15);
            driehoek.closePath();
            grafisch.fill(driehoek);
            grafisch.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.drawArc(1, 1, 16, 16, 145, 250);
        });
    }

    public static Icon herstelIcoon() {
        return new GeschilderdIcoon(18, AppPalet.ACCENT, grafisch -> {
            grafisch.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.drawArc(2, 2, 14, 14, 45, 250);
            Path2D pijl = new Path2D.Double();
            pijl.moveTo(10, 1.5);
            pijl.lineTo(16, 2.8);
            pijl.lineTo(13.1, 7.6);
            pijl.closePath();
            grafisch.fill(pijl);
        });
    }

    public static Icon versnelIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR, grafisch -> {
            grafisch.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.drawLine(9, 3, 9, 15);
            grafisch.drawLine(3, 9, 15, 9);
        });
    }

    public static Icon vertraagIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR, grafisch -> {
            grafisch.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.drawLine(3, 9, 15, 9);
        });
    }

    public static Icon inZoomIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR, grafisch -> {
            grafisch.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.draw(new Ellipse2D.Double(3, 3, 9, 9));
            grafisch.drawLine(11, 11, 15, 15);
            grafisch.drawLine(8, 5, 8, 10);
            grafisch.drawLine(5, 8, 10, 8);
        });
    }

    public static Icon uitZoomIcoon() {
        return new GeschilderdIcoon(18, AppPalet.PRIMAIR, grafisch -> {
            grafisch.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            grafisch.draw(new Ellipse2D.Double(3, 3, 9, 9));
            grafisch.drawLine(11, 11, 15, 15);
            grafisch.drawLine(5, 8, 10, 8);
        });
    }

    public static Icon vierkantIcoon(Color kleur) {
        return new GeschilderdIcoon(16, kleur, grafisch -> grafisch.fill(new RoundRectangle2D.Double(2, 2, 12, 12, 3, 3)));
    }

    public static Icon alternatiefIcoon(Color kleur) {
        return new GeschilderdIcoon(16, kleur, grafisch -> grafisch.fill(maakAlternatieveVorm(1.5, 1.5, 13)));
    }

    @FunctionalInterface
    private interface IcoonSchilder {
        void paint(Graphics2D grafisch);
    }

    private static final class GeschilderdIcoon implements Icon {
        private final int grootte;
        private final Color kleur;
        private final IcoonSchilder schilder;

        private GeschilderdIcoon(int grootte, Color kleur, IcoonSchilder schilder) {
            this.grootte = grootte;
            this.kleur = kleur;
            this.schilder = schilder;
        }

        @Override
        public void paintIcon(Component component, Graphics grafisch, int x, int y) {
            Graphics2D grafisch2D = (Graphics2D) grafisch.create();
            grafisch2D.translate(x, y);
            grafisch2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            grafisch2D.setColor(kleur);
            schilder.paint(grafisch2D);
            grafisch2D.dispose();
        }

        @Override
        public int getIconWidth() {
            return grootte;
        }

        @Override
        public int getIconHeight() {
            return grootte;
        }
    }

    public static Path2D maakAlternatieveVorm(double x, double y, double grootte) {
        double breedte = grootte;
        double hoogte = grootte;
        Path2D hart = new Path2D.Double();
        hart.moveTo(x + breedte / 2.0, y + hoogte * 0.92);
        hart.curveTo(x + breedte * 0.03, y + hoogte * 0.60, x + breedte * 0.02, y + hoogte * 0.18, x + breedte * 0.28, y + hoogte * 0.18);
        hart.curveTo(x + breedte * 0.42, y + hoogte * 0.18, x + breedte * 0.48, y + hoogte * 0.30, x + breedte / 2.0, y + hoogte * 0.38);
        hart.curveTo(x + breedte * 0.52, y + hoogte * 0.30, x + breedte * 0.58, y + hoogte * 0.18, x + breedte * 0.72, y + hoogte * 0.18);
        hart.curveTo(x + breedte * 0.98, y + hoogte * 0.18, x + breedte * 0.97, y + hoogte * 0.60, x + breedte / 2.0, y + hoogte * 0.92);
        hart.closePath();
        return hart;
    }
}
