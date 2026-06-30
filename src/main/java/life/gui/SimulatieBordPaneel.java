package life.gui;

import life.controller.SimulatieBestuurder;
import life.model.Cel;
import life.model.CelVorm;
import life.model.CelSoort;
import life.model.Rasterplaats;
import life.model.SimulatieLuisteraar;
import life.model.SimulatieOpname;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;

public final class SimulatieBordPaneel extends JPanel implements SimulatieLuisteraar {
    private static final int BASIS_CEL_GROOTTE = 20;
    private static final double MINIMALE_ZOOM = 0.45;
    private static final double MAXIMALE_ZOOM = 2.4;

    private final SimulatieBestuurder controller;
    private SimulatieOpname snapshot;
    private double zoomFactor = 1.0;
    private JScrollPane scrollPane;

    public SimulatieBordPaneel(SimulatieBestuurder controller, SimulatieOpname beginOpname) {
        this.controller = controller;
        this.snapshot = beginOpname;
        setBackground(Color.WHITE);
        setOpaque(true);

        MouseAdapter muisAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent gebeurtenis) {
                verwerkBordKlik(gebeurtenis);
            }

            @Override
            public void mouseDragged(MouseEvent gebeurtenis) {
                if (SwingUtilities.isLeftMouseButton(gebeurtenis)) {
                    verwerkBordKlik(gebeurtenis);
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent gebeurtenis) {
                if (gebeurtenis.isControlDown()) {
                    double factor = gebeurtenis.getWheelRotation() < 0 ? 1.12 : 0.88;
                    zoomMet(factor, gebeurtenis.getPoint());
                    gebeurtenis.consume();
                }
            }
        };

        addMouseListener(muisAdapter);
        addMouseMotionListener(muisAdapter);
        addMouseWheelListener(muisAdapter);
        werkVoorkeurGrootteBij();
    }

    public void koppelScrollPaneel(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void inZoomen() {
        zoomMet(1.15, getZichtbaarMiddelpunt());
    }

    public void uitZoomen() {
        zoomMet(0.87, getZichtbaarMiddelpunt());
    }

    public void herstelZoom() {
        pasZoomToe(1.0, getZichtbaarMiddelpunt());
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public int getGeschaaldeCelGrootte() {
        return Math.max(6, (int) Math.round(BASIS_CEL_GROOTTE * zoomFactor));
    }

    @Override
    public void opSimulatieGewijzigd(SimulatieOpname opname) {
        this.snapshot = opname;
        werkVoorkeurGrootteBij();
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics grafisch) {
        super.paintComponent(grafisch);
        Graphics2D grafisch2D = (Graphics2D) grafisch.create();
        grafisch2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int celGrootte = getGeschaaldeCelGrootte();
        Rectangle knipGrenzen = grafisch2D.getClipBounds();
        int startKolom = Math.max(0, knipGrenzen.x / celGrootte);
        int eindKolom = Math.min(snapshot.kolommen() - 1, (knipGrenzen.x + knipGrenzen.width) / celGrootte + 1);
        int startRij = Math.max(0, knipGrenzen.y / celGrootte);
        int eindRij = Math.min(snapshot.rijen() - 1, (knipGrenzen.y + knipGrenzen.height) / celGrootte + 1);

        tekenAchtergrondRaster(grafisch2D, startRij, eindRij, startKolom, eindKolom, celGrootte);
        tekenCellen(grafisch2D, celGrootte);

        grafisch2D.dispose();
    }

    private void tekenAchtergrondRaster(Graphics2D grafisch2D, int startRij, int eindRij, int startKolom, int eindKolom, int celGrootte) {
        grafisch2D.setColor(AppPalet.RASTER_DONKER);
        grafisch2D.fillRect(0, 0, getWidth(), getHeight());

        grafisch2D.setColor(AppPalet.RASTER);
        grafisch2D.setStroke(new BasicStroke(1f));
        for (int rij = startRij; rij <= eindRij; rij++) {
            int y = rij * celGrootte;
            grafisch2D.drawLine(startKolom * celGrootte, y, (eindKolom + 1) * celGrootte, y);
        }
        for (int kolom = startKolom; kolom <= eindKolom; kolom++) {
            int x = kolom * celGrootte;
            grafisch2D.drawLine(x, startRij * celGrootte, x, (eindRij + 1) * celGrootte);
        }
    }

    private void tekenCellen(Graphics2D grafisch2D, int celGrootte) {
        for (Cel cel : snapshot.cellen().values()) {
            int x = cel.getPositie().kolom() * celGrootte;
            int y = cel.getPositie().rij() * celGrootte;
            int inzet = Math.max(2, celGrootte / 10);
            int tekenGrootte = celGrootte - (inzet * 2);

            grafisch2D.setColor(bepaalKleur(cel.getSoort()));
            if (cel.getVorm() == CelVorm.VIERKANT) {
                grafisch2D.fillRoundRect(x + inzet, y + inzet, tekenGrootte, tekenGrootte, 6, 6);
            } else {
                Path2D alternatieveVorm = AppIcoonFabriek.maakAlternatieveVorm(x + inzet, y + inzet, tekenGrootte);
                grafisch2D.fill(alternatieveVorm);
            }

            grafisch2D.setColor(bepaalKontour(cel.getSoort()));
            grafisch2D.setStroke(new BasicStroke(Math.max(1.5f, celGrootte / 11f)));
            if (cel.getVorm() == CelVorm.VIERKANT) {
                grafisch2D.drawRoundRect(x + inzet, y + inzet, tekenGrootte, tekenGrootte, 6, 6);
            } else {
                Path2D alternatieveVorm = AppIcoonFabriek.maakAlternatieveVorm(x + inzet, y + inzet, tekenGrootte);
                grafisch2D.draw(alternatieveVorm);
            }
        }
    }

    private void verwerkBordKlik(MouseEvent gebeurtenis) {
        Rasterplaats positie = vertaalNaarPositie(gebeurtenis.getPoint());
        if (positie != null) {
            controller.verwerkBordInteractie(positie, gebeurtenis);
        }
    }

    private Rasterplaats vertaalNaarPositie(Point punt) {
        int celGrootte = getGeschaaldeCelGrootte();
        int rij = punt.y / celGrootte;
        int kolom = punt.x / celGrootte;
        if (rij < 0 || rij >= snapshot.rijen() || kolom < 0 || kolom >= snapshot.kolommen()) {
            return null;
        }
        return new Rasterplaats(rij, kolom);
    }

    private void zoomMet(double factor, Point focusPunt) {
        pasZoomToe(zoomFactor * factor, focusPunt);
    }

    private void pasZoomToe(double gevraagdeZoom, Point focusPunt) {
        double vorigeZoom = zoomFactor;
        zoomFactor = Math.max(MINIMALE_ZOOM, Math.min(MAXIMALE_ZOOM, gevraagdeZoom));
        if (Math.abs(vorigeZoom - zoomFactor) < 0.0001) {
            return;
        }

        Point stabielPunt = focusPunt == null ? getZichtbaarMiddelpunt() : focusPunt;
        double relatieveX = stabielPunt.x / vorigeZoom;
        double relatieveY = stabielPunt.y / vorigeZoom;

        werkVoorkeurGrootteBij();
        revalidate();

        if (scrollPane != null) {
            int nieuweX = (int) Math.round(relatieveX * zoomFactor - scrollPane.getViewport().getWidth() / 2.0);
            int nieuweY = (int) Math.round(relatieveY * zoomFactor - scrollPane.getViewport().getHeight() / 2.0);
            int begrensdeX = Math.max(0, Math.min(nieuweX, Math.max(0, getWidth() - scrollPane.getViewport().getWidth())));
            int begrensdeY = Math.max(0, Math.min(nieuweY, Math.max(0, getHeight() - scrollPane.getViewport().getHeight())));
            scrollPane.getViewport().setViewPosition(new Point(begrensdeX, begrensdeY));
        }

        repaint();
    }

    private Point getZichtbaarMiddelpunt() {
        if (scrollPane == null) {
            return new Point(getWidth() / 2, getHeight() / 2);
        }

        Rectangle zichtRechthoek = scrollPane.getViewport().getViewRect();
        return new Point(
                zichtRechthoek.x + (zichtRechthoek.width / 2),
                zichtRechthoek.y + (zichtRechthoek.height / 2)
        );
    }

    private void werkVoorkeurGrootteBij() {
        int celGrootte = getGeschaaldeCelGrootte();
        setPreferredSize(new Dimension(snapshot.kolommen() * celGrootte, snapshot.rijen() * celGrootte));
    }

    private Color bepaalKleur(CelSoort celSoort) {
        return celSoort == CelSoort.CONWAY ? AppPalet.CONWAY : AppPalet.ALTERNATIEF;
    }

    private Color bepaalKontour(CelSoort celSoort) {
        return celSoort == CelSoort.CONWAY ? AppPalet.PRIMAIR : AppPalet.ACCENT_STERK;
    }
}
