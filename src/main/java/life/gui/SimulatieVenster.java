package life.gui;

import life.controller.PlaatsingsHulpmiddel;
import life.controller.SimulatieBestuurder;
import life.model.LevenSimulatie;
import life.model.SimulatieLuisteraar;
import life.model.SimulatieOpname;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

public final class SimulatieVenster extends JFrame implements SimulatieLuisteraar {
    private final LevenSimulatie simulatie;
    private final SimulatieBordPaneel bordPaneel;
    private final SimulatieTerminalPaneel terminalPaneel = new SimulatieTerminalPaneel();
    private final JLabel tikWaardeLabel = maakWaardeLabel();
    private final JLabel conwayWaardeLabel = maakWaardeLabel();
    private final JLabel alternatiefWaardeLabel = maakWaardeLabel();
    private final JLabel totaalWaardeLabel = maakWaardeLabel();
    private final JLabel snelheidWaardeLabel = maakWaardeLabel();
    private final JLabel zoomWaardeLabel = maakWaardeLabel();
    private final JLabel statusLabel = new JLabel("Bewerkingsmodus");

    public SimulatieVenster(LevenSimulatie simulatie, SimulatieBestuurder bestuurder) {
        super("Conway en Alternatief Leven");
        this.simulatie = simulatie;
        this.bordPaneel = new SimulatieBordPaneel(bestuurder, simulatie.getOpname());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 700));
        getContentPane().setBackground(AppPalet.ACHTERGROND);
        setLayout(new BorderLayout(10, 10));

        JScrollPane bordScrollPaneel = new JScrollPane(bordPaneel);
        bordPaneel.koppelScrollPaneel(bordScrollPaneel);
        configureerScrollPaneel(bordScrollPaneel);

        terminalPaneel.setMinimumSize(new Dimension(0, 120));

        add(maakKoptekst(bestuurder), BorderLayout.NORTH);
        add(maakHoofdgebied(bordScrollPaneel, bestuurder), BorderLayout.CENTER);

        simulatie.voegLuisteraarToe(this);
        simulatie.voegLuisteraarToe(bordPaneel);
        simulatie.voegLogLuisteraarToe(terminalPaneel);
        centreerWeergave();
    }

    @Override
    public void opSimulatieGewijzigd(SimulatieOpname opname) {
        tikWaardeLabel.setText(Long.toString(opname.tikNummer()));
        conwayWaardeLabel.setText(Integer.toString(opname.conwayAantalCellen()));
        alternatiefWaardeLabel.setText(Integer.toString(opname.alternatiefAantalCellen()));
        totaalWaardeLabel.setText(Integer.toString(opname.totaalAantalCellen()));
        snelheidWaardeLabel.setText(opname.vertragingMillis() + " ms");
        zoomWaardeLabel.setText(Math.round(bordPaneel.getZoomFactor() * 100) + "%");
        statusLabel.setText(opname.actief() ? "Simulatie actief" : "Bewerk cellen en druk op Starten");
    }

    private JComponent maakKoptekst(SimulatieBestuurder bestuurder) {
        JPanel koptekst = new JPanel(new BorderLayout(16, 0));
        koptekst.setBackground(AppPalet.ACHTERGROND);
        koptekst.setBorder(BorderFactory.createEmptyBorder(8, 18, 0, 18));

        JPanel titelPaneel = maakKaartPaneel();
        titelPaneel.setLayout(new BoxLayout(titelPaneel, BoxLayout.Y_AXIS));

        JLabel titelLabel = new JLabel("Conways Spel des Levens");
        titelLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titelLabel.setForeground(AppPalet.TEKST);

        JLabel ondertitelLabel = new JLabel("Linksklik = geselecteerd penseel, rechtsklik = alternatieve cel, Shift + klik = wissen, Ctrl + wiel = zoom");
        ondertitelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ondertitelLabel.setForeground(AppPalet.GEDEMPT);

        titelPaneel.add(titelLabel);
        titelPaneel.add(Box.createVerticalStrut(6));
        titelPaneel.add(ondertitelLabel);

        JPanel bedieningsPaneel = maakKaartPaneel();
        bedieningsPaneel.add(maakActieKnop("Starten", AppIcoonFabriek.speelIcoon(), gebeurtenis -> bestuurder.starten()));
        bedieningsPaneel.add(maakActieKnop("Pauzeren", AppIcoonFabriek.pauzeIcoon(), gebeurtenis -> bestuurder.pauzeer()));
        bedieningsPaneel.add(maakActieKnop("Hervatten", AppIcoonFabriek.hervatIcoon(), gebeurtenis -> bestuurder.hervat()));
        bedieningsPaneel.add(maakActieKnop("Herstellen", AppIcoonFabriek.herstelIcoon(), gebeurtenis -> bestuurder.herstel()));
        bedieningsPaneel.add(maakActieKnop("Sneller", AppIcoonFabriek.versnelIcoon(), gebeurtenis -> bestuurder.versnellen()));
        bedieningsPaneel.add(maakActieKnop("Langzamer", AppIcoonFabriek.vertraagIcoon(), gebeurtenis -> bestuurder.vertragen()));
        bedieningsPaneel.add(maakActieKnop("Inzoomen", AppIcoonFabriek.inZoomIcoon(), gebeurtenis -> werkZoomBijNa(bordPaneel::inZoomen)));
        bedieningsPaneel.add(maakActieKnop("Uitzoomen", AppIcoonFabriek.uitZoomIcoon(), gebeurtenis -> werkZoomBijNa(bordPaneel::uitZoomen)));

        koptekst.add(titelPaneel, BorderLayout.CENTER);
        koptekst.add(bedieningsPaneel, BorderLayout.EAST);
        return koptekst;
    }

    private JComponent maakZijbalk(SimulatieBestuurder bestuurder) {
        JPanel zijbalk = maakKaartPaneel();
        zijbalk.setLayout(new BoxLayout(zijbalk, BoxLayout.Y_AXIS));
        zijbalk.setPreferredSize(new Dimension(240, 320));

        JLabel paneelTitel = new JLabel("Bedieningspaneel");
        paneelTitel.setFont(new Font("Georgia", Font.BOLD, 22));
        paneelTitel.setForeground(AppPalet.TEKST);

        statusLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        statusLabel.setForeground(AppPalet.PRIMAIR);

        zijbalk.add(paneelTitel);
        zijbalk.add(Box.createVerticalStrut(4));
        zijbalk.add(statusLabel);
        zijbalk.add(Box.createVerticalStrut(10));
        zijbalk.add(new JSeparator(SwingConstants.HORIZONTAL));
        zijbalk.add(Box.createVerticalStrut(10));
        zijbalk.add(maakPenseelGedeelte(bestuurder));
        zijbalk.add(Box.createVerticalStrut(10));
        zijbalk.add(new JSeparator(SwingConstants.HORIZONTAL));
        zijbalk.add(Box.createVerticalStrut(10));
        zijbalk.add(maakStatistiekRij("Tikken", tikWaardeLabel));
        zijbalk.add(maakStatistiekRij("Conway-cellen", conwayWaardeLabel));
        zijbalk.add(maakStatistiekRij("Alternatieve cellen", alternatiefWaardeLabel));
        zijbalk.add(maakStatistiekRij("Totaal cellen", totaalWaardeLabel));
        zijbalk.add(maakStatistiekRij("Snelheid", snelheidWaardeLabel));
        zijbalk.add(maakStatistiekRij("Zoom", zoomWaardeLabel));
        zijbalk.add(Box.createVerticalGlue());
        zijbalk.add(maakLegendeRij("Conway-cel", AppIcoonFabriek.vierkantIcoon(AppPalet.CONWAY)));
        zijbalk.add(Box.createVerticalStrut(6));
        zijbalk.add(maakLegendeRij("Alternatieve cel", AppIcoonFabriek.alternatiefIcoon(AppPalet.ALTERNATIEF)));
        zijbalk.add(Box.createVerticalStrut(8));
        zijbalk.add(maakTipLabel("De terminal hieronder toont simulatiegebeurtenissen, maar blijft alleen-lezen."));

        JPanel omhulsel = new JPanel(new BorderLayout());
        omhulsel.setOpaque(false);
        omhulsel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        omhulsel.add(zijbalk, BorderLayout.CENTER);
        return omhulsel;
    }

    private JComponent maakPenseelGedeelte(SimulatieBestuurder bestuurder) {
        JPanel paneel = new JPanel();
        paneel.setOpaque(false);
        paneel.setLayout(new BoxLayout(paneel, BoxLayout.Y_AXIS));

        JLabel titel = new JLabel("Penseel");
        titel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 17));
        titel.setForeground(AppPalet.TEKST);

        ButtonGroup knoppenGroep = new ButtonGroup();
        JRadioButton conwayKnop = maakPenseelKnop("Conway-cel", true);
        JRadioButton alternatiefKnop = maakPenseelKnop("Alternatieve cel", false);
        JRadioButton wisKnop = maakPenseelKnop("Cel wissen", false);

        conwayKnop.addActionListener(gebeurtenis -> bestuurder.setGeselecteerdHulpmiddel(PlaatsingsHulpmiddel.CONWAY));
        alternatiefKnop.addActionListener(gebeurtenis -> bestuurder.setGeselecteerdHulpmiddel(PlaatsingsHulpmiddel.ALTERNATIEF));
        wisKnop.addActionListener(gebeurtenis -> bestuurder.setGeselecteerdHulpmiddel(PlaatsingsHulpmiddel.WISSEN));

        knoppenGroep.add(conwayKnop);
        knoppenGroep.add(alternatiefKnop);
        knoppenGroep.add(wisKnop);

        paneel.add(titel);
        paneel.add(Box.createVerticalStrut(6));
        paneel.add(conwayKnop);
        paneel.add(Box.createVerticalStrut(4));
        paneel.add(alternatiefKnop);
        paneel.add(Box.createVerticalStrut(4));
        paneel.add(wisKnop);
        return paneel;
    }

    private JRadioButton maakPenseelKnop(String tekst, boolean geselecteerd) {
        JRadioButton knop = new JRadioButton(tekst, geselecteerd);
        knop.setOpaque(false);
        knop.setForeground(AppPalet.TEKST);
        knop.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        knop.setMargin(new Insets(3, 0, 3, 0));
        return knop;
    }

    private JPanel maakStatistiekRij(String labelTekst, JLabel waardeLabel) {
        JPanel rij = new JPanel(new BorderLayout());
        rij.setOpaque(false);

        JLabel label = new JLabel(labelTekst);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(AppPalet.GEDEMPT);

        rij.add(label, BorderLayout.WEST);
        rij.add(waardeLabel, BorderLayout.EAST);
        rij.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return rij;
    }

    private JPanel maakLegendeRij(String tekst, Icon icoon) {
        JPanel rij = new JPanel(new BorderLayout(8, 0));
        rij.setOpaque(false);

        JLabel icoonLabel = new JLabel(icoon);
        JLabel tekstLabel = new JLabel(tekst);
        tekstLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tekstLabel.setForeground(AppPalet.TEKST);

        rij.add(icoonLabel, BorderLayout.WEST);
        rij.add(tekstLabel, BorderLayout.CENTER);
        rij.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        return rij;
    }

    private JLabel maakTipLabel(String tekst) {
        JLabel label = new JLabel("<html><div style='width:210px;'>" + tekst + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppPalet.GEDEMPT);
        return label;
    }

    private GestijldeKnop maakActieKnop(String label, Icon icoon, ActionListener actieLuisteraar) {
        GestijldeKnop knop = new GestijldeKnop(label, icoon);
        knop.addActionListener(actieLuisteraar);
        return knop;
    }

    private JLabel maakWaardeLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(AppPalet.TEKST);
        return label;
    }

    private JPanel maakKaartPaneel() {
        JPanel paneel = new JPanel();
        paneel.setBackground(AppPalet.OPPERVLAK);
        paneel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppPalet.OPPERVLAK_STERK, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return paneel;
    }

    private JComponent maakHoofdgebied(JScrollPane bordScrollPaneel, SimulatieBestuurder bestuurder) {
        JSplitPane rechterSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, maakZijbalk(bestuurder), terminalPaneel);
        rechterSplit.setResizeWeight(0.55);
        rechterSplit.setDividerSize(6);
        rechterSplit.setBorder(BorderFactory.createEmptyBorder());
        rechterSplit.setOpaque(false);
        rechterSplit.setBackground(AppPalet.ACHTERGROND);

        JPanel rechterKolom = new JPanel(new BorderLayout());
        rechterKolom.setOpaque(false);
        rechterKolom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        rechterKolom.add(rechterSplit, BorderLayout.CENTER);

        JSplitPane splitPaneel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bordScrollPaneel, rechterKolom);
        splitPaneel.setResizeWeight(0.76);
        splitPaneel.setDividerSize(10);
        splitPaneel.setBorder(BorderFactory.createEmptyBorder());
        splitPaneel.setOpaque(false);
        splitPaneel.setBackground(AppPalet.ACHTERGROND);
        return splitPaneel;
    }

    private void configureerScrollPaneel(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 18, 18, 0),
                BorderFactory.createLineBorder(AppPalet.OPPERVLAK_STERK, 1, true)
        ));
        scrollPane.getViewport().setBackground(AppPalet.OPPERVLAK);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(32);
        scrollPane.getVerticalScrollBar().setUnitIncrement(32);
    }

    private void werkZoomBijNa(Runnable zoomActie) {
        zoomActie.run();
        zoomWaardeLabel.setText(Math.round(bordPaneel.getZoomFactor() * 100) + "%");
    }

    private void centreerWeergave() {
        SwingUtilities.invokeLater(() -> {
            int celGrootte = bordPaneel.getGeschaaldeCelGrootte();
            int x = Math.max(0, (simulatie.getMiddenKolom() * celGrootte) - 420);
            int y = Math.max(0, (simulatie.getMiddenRij() * celGrootte) - 280);
            bordPaneel.scrollRectToVisible(new Rectangle(x, y, 840, 560));
        });
    }
}
