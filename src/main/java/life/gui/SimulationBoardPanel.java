package life.gui;

import life.controller.SimulationController;
import life.model.Cell;
import life.model.CellShape;
import life.model.CellType;
import life.model.GridPosition;
import life.model.SimulationListener;
import life.model.SimulationSnapshot;

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

public final class SimulationBoardPanel extends JPanel implements SimulationListener {
    private static final int BASE_CELL_SIZE = 20;
    private static final double MIN_ZOOM = 0.45;
    private static final double MAX_ZOOM = 2.4;

    private final SimulationController controller;
    private SimulationSnapshot snapshot;
    private double zoomFactor = 1.0;
    private JScrollPane scrollPane;

    public SimulationBoardPanel(SimulationController controller, SimulationSnapshot initialSnapshot) {
        this.controller = controller;
        this.snapshot = initialSnapshot;
        setBackground(Color.WHITE);
        setOpaque(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                handleBoardClick(event);
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    handleBoardClick(event);
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                if (event.isControlDown()) {
                    double factor = event.getWheelRotation() < 0 ? 1.12 : 0.88;
                    zoomBy(factor, event.getPoint());
                    event.consume();
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
        updatePreferredSize();
    }

    public void attachScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void zoomIn() {
        zoomBy(1.15, getVisibleCenter());
    }

    public void zoomOut() {
        zoomBy(0.87, getVisibleCenter());
    }

    public void resetZoom() {
        applyZoom(1.0, getVisibleCenter());
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public int getScaledCellSize() {
        return Math.max(6, (int) Math.round(BASE_CELL_SIZE * zoomFactor));
    }

    @Override
    public void onSimulationChanged(SimulationSnapshot snapshot) {
        this.snapshot = snapshot;
        updatePreferredSize();
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cellSize = getScaledCellSize();
        Rectangle clipBounds = graphics2D.getClipBounds();
        int startColumn = Math.max(0, clipBounds.x / cellSize);
        int endColumn = Math.min(snapshot.columns() - 1, (clipBounds.x + clipBounds.width) / cellSize + 1);
        int startRow = Math.max(0, clipBounds.y / cellSize);
        int endRow = Math.min(snapshot.rows() - 1, (clipBounds.y + clipBounds.height) / cellSize + 1);

        drawBackgroundGrid(graphics2D, startRow, endRow, startColumn, endColumn, cellSize);
        drawCells(graphics2D, cellSize);

        graphics2D.dispose();
    }

    private void drawBackgroundGrid(Graphics2D graphics2D, int startRow, int endRow, int startColumn, int endColumn, int cellSize) {
        graphics2D.setColor(AppPalette.GRID_DARK);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());

        graphics2D.setColor(AppPalette.GRID);
        graphics2D.setStroke(new BasicStroke(1f));
        for (int row = startRow; row <= endRow; row++) {
            int y = row * cellSize;
            graphics2D.drawLine(startColumn * cellSize, y, (endColumn + 1) * cellSize, y);
        }
        for (int column = startColumn; column <= endColumn; column++) {
            int x = column * cellSize;
            graphics2D.drawLine(x, startRow * cellSize, x, (endRow + 1) * cellSize);
        }
    }

    private void drawCells(Graphics2D graphics2D, int cellSize) {
        for (Cell cell : snapshot.cells().values()) {
            int x = cell.getPosition().column() * cellSize;
            int y = cell.getPosition().row() * cellSize;
            int inset = Math.max(2, cellSize / 10);
            int drawSize = cellSize - (inset * 2);

            graphics2D.setColor(resolveColor(cell.getType()));
            if (cell.getShape() == CellShape.SQUARE) {
                graphics2D.fillRoundRect(x + inset, y + inset, drawSize, drawSize, 6, 6);
            } else {
                Path2D heart = AppIconFactory.createHeartPath(x + inset, y + inset, drawSize);
                graphics2D.fill(heart);
            }

            graphics2D.setColor(resolveOutline(cell.getType()));
            graphics2D.setStroke(new BasicStroke(Math.max(1.5f, cellSize / 11f)));
            if (cell.getShape() == CellShape.SQUARE) {
                graphics2D.drawRoundRect(x + inset, y + inset, drawSize, drawSize, 6, 6);
            } else {
                Path2D heart = AppIconFactory.createHeartPath(x + inset, y + inset, drawSize);
                graphics2D.draw(heart);
            }
        }
    }

    private void handleBoardClick(MouseEvent event) {
        GridPosition position = translateToPosition(event.getPoint());
        if (position != null) {
            controller.handleBoardInteraction(position, event);
        }
    }

    private GridPosition translateToPosition(Point point) {
        int cellSize = getScaledCellSize();
        int row = point.y / cellSize;
        int column = point.x / cellSize;
        if (row < 0 || row >= snapshot.rows() || column < 0 || column >= snapshot.columns()) {
            return null;
        }
        return new GridPosition(row, column);
    }

    private void zoomBy(double factor, Point focusPoint) {
        applyZoom(zoomFactor * factor, focusPoint);
    }

    private void applyZoom(double requestedZoom, Point focusPoint) {
        double previousZoom = zoomFactor;
        zoomFactor = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, requestedZoom));
        if (Math.abs(previousZoom - zoomFactor) < 0.0001) {
            return;
        }

        Point stablePoint = focusPoint == null ? getVisibleCenter() : focusPoint;
        double relativeX = stablePoint.x / previousZoom;
        double relativeY = stablePoint.y / previousZoom;

        updatePreferredSize();
        revalidate();

        if (scrollPane != null) {
            int newX = (int) Math.round(relativeX * zoomFactor - scrollPane.getViewport().getWidth() / 2.0);
            int newY = (int) Math.round(relativeY * zoomFactor - scrollPane.getViewport().getHeight() / 2.0);
            int boundedX = Math.max(0, Math.min(newX, Math.max(0, getWidth() - scrollPane.getViewport().getWidth())));
            int boundedY = Math.max(0, Math.min(newY, Math.max(0, getHeight() - scrollPane.getViewport().getHeight())));
            scrollPane.getViewport().setViewPosition(new Point(boundedX, boundedY));
        }

        repaint();
    }

    private Point getVisibleCenter() {
        if (scrollPane == null) {
            return new Point(getWidth() / 2, getHeight() / 2);
        }

        Rectangle viewRectangle = scrollPane.getViewport().getViewRect();
        return new Point(
                viewRectangle.x + (viewRectangle.width / 2),
                viewRectangle.y + (viewRectangle.height / 2)
        );
    }

    private void updatePreferredSize() {
        int cellSize = getScaledCellSize();
        setPreferredSize(new Dimension(snapshot.columns() * cellSize, snapshot.rows() * cellSize));
    }

    private Color resolveColor(CellType type) {
        return type == CellType.CONWAY ? AppPalette.CONWAY : AppPalette.ALTERNATIVE;
    }

    private Color resolveOutline(CellType type) {
        return type == CellType.CONWAY ? AppPalette.PRIMARY : AppPalette.ACCENT_STRONG;
    }
}
