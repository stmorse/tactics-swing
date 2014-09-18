package tactics.control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JViewport;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.view.BoardPanel;
import tactics.view.GameUIManager;

public class MoveAction extends AbstractAction {
	private GameLogic gl;
	private BoardPanel panel;
	private GameUIManager uiManager;
	private int dir;

	public MoveAction(String direction, GameLogic logic, BoardPanel p, GameUIManager uim) {
		super(direction);
		this.gl = logic;
		this.panel = p;
		this.uiManager = uim;
		this.dir = BoardTools2.convertDirection(direction);
	}

	public void actionPerformed(ActionEvent e) {
		Grid cg = gl.getCurrentGrid();
		Grid ng = new Grid(BoardTools2.coords(cg.x, cg.y, dir)[0],
						   BoardTools2.coords(cg.x, cg.y, dir)[1]);

		// check if we are trying to move off the board
		if (ng.x < 0 || ng.x > (GameConstants.N_COLS - 1) ||
			ng.y < 0 || ng.y > (GameConstants.N_ROWS - 1))
			return;

		// check if we are moving a unit
		if (panel.isUnitHasFocus()) {
			// attempt to move the current unit
			if (!gl.moveCurrentUnit(ng, dir))
				return;
		}

		// regardless of unit/terrain, set the new current square
		gl.setCurrentGrid(ng);

		// check if currentGrid moved outside visible screen
		JViewport view = panel.getEnclosingPane().getViewport();
		Grid[] b = BoardTools2.getGridBoundaries(view.getViewRect(), 
												 panel.isZoomOn());
		if ((ng.x > 0 && ng.x <= b[0].x) || 
			(ng.x < GameConstants.N_COLS && ng.x >= b[1].x) ||
			(ng.y > 0 && ng.y <= b[0].y) || 
			(ng.y < GameConstants.N_ROWS && ng.y >= b[1].y)) {			
            view.setViewPosition(
                BoardTools2.centerBoard(gl.getCurrentGrid(),
                                        view.getWidth(), view.getHeight(),
                                        panel.isZoomOn()));
		}

		uiManager.updateLabels();

		panel.repaint();
	}
}