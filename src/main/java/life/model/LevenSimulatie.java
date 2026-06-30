package life.model;

import life.clock.Spelklok;
import life.clock.TikLuisteraar;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LevenSimulatie implements TikLuisteraar {
    private final List<SimulatieLuisteraar> luisteraars = new ArrayList<>();
    private final List<SimulatieLogLuisteraar> logLuisteraars = new ArrayList<>();
    private final Spelklok spelKlok;
    private Speelbord startBord;
    private Speelbord actieveBord;

    public LevenSimulatie(int rijen, int kolommen, CelFabriek celFabriek, Regelboek regelboek, Spelklok spelKlok) {
        this.startBord = new Speelbord(rijen, kolommen, celFabriek, regelboek);
        this.actieveBord = startBord.kopieer();
        this.spelKlok = spelKlok;
        this.spelKlok.voegLuisteraarToe(this);
    }

    public void voegLuisteraarToe(SimulatieLuisteraar luisteraar) {
        luisteraars.add(luisteraar);
        luisteraar.opSimulatieGewijzigd(getOpname());
    }

    public void verwijderLuisteraar(SimulatieLuisteraar luisteraar) {
        luisteraars.remove(luisteraar);
    }

    public void voegLogLuisteraarToe(SimulatieLogLuisteraar luisteraar) {
        logLuisteraars.add(luisteraar);
    }

    public void verwijderLogLuisteraar(SimulatieLogLuisteraar luisteraar) {
        logLuisteraars.remove(luisteraar);
    }

    public void plaatsCel(int rij, int kolom, CelSoort celSoort) {
        if (spelKlok.isActief()) {
            return;
        }
        startBord.voegCelToe(rij, kolom, celSoort);
        actieveBord = startBord.kopieer();
        informeerLuisteraars();
    }

    public void verwijderCel(int rij, int kolom) {
        if (spelKlok.isActief()) {
            return;
        }
        startBord.verwijderCel(rij, kolom);
        actieveBord = startBord.kopieer();
        informeerLuisteraars();
    }

    public boolean isBezet(int rij, int kolom) {
        return startBord.isBezet(rij, kolom);
    }

    public void starten() {
        actieveBord = startBord.kopieer();
        spelKlok.starten();
        loggen(String.format("Simulatie gestart op %d ms per tik.", spelKlok.getVertragingMillis()));
        informeerLuisteraars();
    }

    public void pauzeer() {
        spelKlok.pauzeer();
        loggen(String.format("Simulatie gepauzeerd na tik %d.", spelKlok.getTikNummer()));
        informeerLuisteraars();
    }

    public void hervat() {
        spelKlok.hervat();
        loggen(String.format("Simulatie hervat op %d ms per tik.", spelKlok.getVertragingMillis()));
        informeerLuisteraars();
    }

    public void herstel() {
        spelKlok.herstel();
        startBord.wissen();
        actieveBord = startBord.kopieer();
        loggen("Simulatie hersteld — raster gewist.");
        informeerLuisteraars();
    }

    public void versnellen() {
        spelKlok.sneller();
        loggen(String.format("Snelheid verhoogd. Vertraging is nu %d ms.", spelKlok.getVertragingMillis()));
        informeerLuisteraars();
    }

    public void vertragen() {
        spelKlok.langzamer();
        loggen(String.format("Snelheid verlaagd. Vertraging is nu %d ms.", spelKlok.getVertragingMillis()));
        informeerLuisteraars();
    }

    public SimulatieOpname getOpname() {
        Map<Rasterplaats, Cel> gekopieerdeCellen = new HashMap<>(actieveBord.getCellenWeergave());
        return new SimulatieOpname(
                spelKlok.getTikNummer(),
                spelKlok.getVertragingMillis(),
                spelKlok.isActief(),
                actieveBord.getAantalCellen(CelSoort.CONWAY),
                actieveBord.getAantalCellen(CelSoort.ALTERNATIEF),
                actieveBord.getRijen(),
                actieveBord.getKolommen(),
                gekopieerdeCellen
        );
    }

    public int getMiddenRij() {
        return actieveBord.getRijen() / 2;
    }

    public int getMiddenKolom() {
        return actieveBord.getKolommen() / 2;
    }

    @Override
    public void opTik(long tikNummer) {
        actieveBord = actieveBord.volgendeGeneratie();
        SimulatieOpname opnameNaTik = getOpname();

        loggen(String.format(
                "Tik %d | Conway: %d | Alternatief: %d | Totaal: %d | Vertraging: %d ms",
                opnameNaTik.tikNummer(),
                opnameNaTik.conwayAantalCellen(),
                opnameNaTik.alternatiefAantalCellen(),
                opnameNaTik.totaalAantalCellen(),
                opnameNaTik.vertragingMillis()
        ));

        informeerLuisteraars(opnameNaTik);
    }

    private void informeerLuisteraars() {
        informeerLuisteraars(getOpname());
    }

    private void informeerLuisteraars(SimulatieOpname opname) {
        SwingUtilities.invokeLater(() -> {
            for (SimulatieLuisteraar luisteraar : new ArrayList<>(luisteraars)) {
                luisteraar.opSimulatieGewijzigd(opname);
            }
        });
    }

    private void loggen(String bericht) {
        System.out.println(bericht);
        SwingUtilities.invokeLater(() -> {
            for (SimulatieLogLuisteraar luisteraar : new ArrayList<>(logLuisteraars)) {
                luisteraar.opLogBericht(bericht);
            }
        });
    }
}
