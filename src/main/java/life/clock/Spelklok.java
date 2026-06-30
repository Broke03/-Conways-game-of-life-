package life.clock;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public final class Spelklok {
    private final List<TikLuisteraar> luisteraars = new ArrayList<>();
    private final int minimumVertragingMillis;
    private final int maximumVertragingMillis;
    private final int vertragingStapMillis = 50;

    private Timer timer;
    private long tikNummer;
    private int vertragingMillis;
    private boolean actief;

    public Spelklok(int initieleVertragingMillis, int minimumVertragingMillis, int maximumVertragingMillis) {
        this.vertragingMillis = initieleVertragingMillis;
        this.minimumVertragingMillis = minimumVertragingMillis;
        this.maximumVertragingMillis = maximumVertragingMillis;
    }

    public void voegLuisteraarToe(TikLuisteraar luisteraar) {
        luisteraars.add(luisteraar);
    }

    public void verwijderLuisteraar(TikLuisteraar luisteraar) {
        luisteraars.remove(luisteraar);
    }

    public long getTikNummer() {
        return tikNummer;
    }

    public int getVertragingMillis() {
        return vertragingMillis;
    }

    public boolean isActief() {
        return actief;
    }

    public void starten() {
        tikNummer = 0;
        actief = true;
        planTikken();
    }

    public void pauzeer() {
        actief = false;
        annuleerGeplandeTaak();
    }

    public void hervat() {
        if (actief) {
            return;
        }
        actief = true;
        planTikken();
    }

    public void herstel() {
        actief = false;
        annuleerGeplandeTaak();
        tikNummer = 0;
    }

    public void sneller() {
        vertragingMillis = Math.max(minimumVertragingMillis, vertragingMillis - vertragingStapMillis);
        if (actief && timer != null) {
            timer.setDelay(vertragingMillis);
        }
    }

    public void langzamer() {
        vertragingMillis = Math.min(maximumVertragingMillis, vertragingMillis + vertragingStapMillis);
        if (actief && timer != null) {
            timer.setDelay(vertragingMillis);
        }
    }

    public void tikNu() {
        long huidigeTik = ++tikNummer;
        for (TikLuisteraar luisteraar : new ArrayList<>(luisteraars)) {
            luisteraar.opTik(huidigeTik);
        }
    }

    public void afsluiten() {
        actief = false;
        annuleerGeplandeTaak();
    }

    private void planTikken() {
        annuleerGeplandeTaak();
        timer = new Timer(vertragingMillis, gebeurtenis -> tikNu());
        timer.setCoalesce(true);
        timer.setInitialDelay(vertragingMillis);
        timer.start();
    }

    private void annuleerGeplandeTaak() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}
