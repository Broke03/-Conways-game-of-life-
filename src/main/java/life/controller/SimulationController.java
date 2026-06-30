package life.controller;

import life.model.CellType;
import life.model.GridPosition;
import life.model.LifeSimulation;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;

public final class SimulationController {
    private final LifeSimulation simulation;
    private PlacementTool selectedTool = PlacementTool.CONWAY;

    public SimulationController(LifeSimulation simulation) {
        this.simulation = simulation;
    }

    public void setSelectedTool(PlacementTool selectedTool) {
        this.selectedTool = selectedTool;
    }

    public PlacementTool getSelectedTool() {
        return selectedTool;
    }

    public void start() {
        simulation.start();
    }

    public void pause() {
        simulation.pause();
    }

    public void resume() {
        simulation.resume();
    }

    public void reset() {
        simulation.reset();
    }

    public void speedUp() {
        simulation.speedUp();
    }

    public void slowDown() {
        simulation.slowDown();
    }

    public void handleBoardInteraction(GridPosition position, MouseEvent event) {
        if (simulation.isOccupied(position.row(), position.column())) {
            simulation.removeCell(position.row(), position.column());
            return;
        }

        if (SwingUtilities.isRightMouseButton(event)) {
            simulation.placeCell(position.row(), position.column(), CellType.ALTERNATIVE);
            return;
        }

        if (event.isShiftDown() || selectedTool == PlacementTool.ERASE) {
            simulation.removeCell(position.row(), position.column());
            return;
        }

        CellType typeToPlace = selectedTool == PlacementTool.ALTERNATIVE
                ? CellType.ALTERNATIVE
                : CellType.CONWAY;

        simulation.placeCell(position.row(), position.column(), typeToPlace);
    }
}
