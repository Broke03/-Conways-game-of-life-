package life.main;

import life.clock.Spelklok;
import life.controller.SimulatieBestuurder;
import life.model.StandaardCelFabriek;
import life.model.LevenSimulatie;
import life.model.Regelboek;
import life.gui.SimulatieVenster;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Regelboek regelboek = Regelboek.maakStandaard();
            LevenSimulatie simulatie = new LevenSimulatie(
                    1000,
                    1000,
                    new StandaardCelFabriek(),
                    regelboek,
                    new Spelklok(350, 80, 1200)
            );

            SimulatieBestuurder bestuurder = new SimulatieBestuurder(simulatie);
            SimulatieVenster venster = new SimulatieVenster(simulatie, bestuurder);
            venster.setVisible(true);
        });
    }
}
