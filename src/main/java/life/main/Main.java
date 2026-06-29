package life.main;

import life.clock.GameClock;
import life.controller.SimulationController;
import life.model.DefaultCellFactory;
import life.model.LifeSimulation;
import life.model.RuleBook;
import life.gui.SimulationFrame;

import javax.swing.SwingUtilities;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RuleBook ruleBook = RuleBook.createDefault();
            LifeSimulation simulation = new LifeSimulation(
                    1000,
                    1000,
                    new DefaultCellFactory(),
                    ruleBook,
                    new GameClock(350, 80, 1200)
            );

            SimulationController controller = new SimulationController(simulation);
            SimulationFrame frame = new SimulationFrame(simulation, controller);
            frame.setVisible(true);
        });
    }
}
