package tactics.control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JViewport;
import javax.swing.event.*;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.view.BoardPanel;
import tactics.view.GameUIManager;

public class MouseControl extends MouseInputAdapter {
	private GameLogic gl;
	private BoardPanel panel;
	private GameUIManager uiManager;
	private Point mousePos;
	
	public MouseControl(GameLogic logic, BoardPanel p, GameUIManager uim) {
		this.gl = logic;
		this.panel = p;
		this.uiManager = uim;
		this.mousePos = new Point(0,0);
	}

	public void mouseClicked(MouseEvent e) {
		if (gl.getGameState() == GameConstants.RED_COMBAT ||
			gl.getGameState() == GameConstants.BLUE_COMBAT ||
			gl.getGameState() == GameConstants.GAME_OVER)
			return;

		int x = e.getX();
        int y = e.getY();
        int mask = InputEvent.CTRL_MASK | InputEvent.BUTTON1_MASK;

        // put current grid outline on this square
        gl.setCurrentGrid(BoardTools2.getGridFromReal(new Point(x,y), panel.isZoomOn()));

        // check click (double-click) on unit, unitPath, or neither
        int index = gl.getUnitIndexAtGrid(gl.getCurrentGrid());
        if (index >= 0 && e.getClickCount() == 2) {
            // double-click was on a unit, check if it's the current player's unit
            if (gl.getUnit(index).getArmy() == gl.getCurrentPlayerArmy()) {
                panel.setUnitHasFocus(true);
                gl.setCurrentUnit(index);
            } else {
                // otherwise, he double-clicked on either no or enemy unit
                panel.setUnitHasFocus(false);
            }
        // check ctrl-click
        } else if (panel.isUnitHasFocus() && (e.getModifiers() & mask) == mask) {
            // user is holding CTRL+click, and unit is already selected
            // attempting to move unit to previous place on path
            if (gl.resetCurrentUnitAlongPath(gl.getCurrentGrid())) {
                panel.setUnitHasFocus(true);
            } else {
                panel.setUnitHasFocus(false);
                gl.deselectAllUnits();
            }
        } else {
            panel.setUnitHasFocus(false);
            gl.deselectAllUnits();
        }

        uiManager.updateLabels();
        
        panel.repaint();
	}

	public void mousePressed(MouseEvent e) {
        mousePos.x = e.getX();
        mousePos.y = e.getY();
    }

    public void mouseDragged(MouseEvent e) {
        // compute the offset from the last mouse location.
        int dx = e.getX() - mousePos.x;
        int dy = e.getY() - mousePos.y;
        
        // adjust the scroll pane by the delta. (view moves in opposite direction of delta)
        JViewport view = panel.getEnclosingPane().getViewport();
        Point pos = view.getViewPosition();
        view.setViewPosition(new Point(Math.max(0, pos.x - dx), 
        							   Math.max(0, pos.y - dy)));

        // update mouse location       
        mousePos.x = e.getX() - dx;
        mousePos.y = e.getY() - dy;
        
        // make sure the JViewPort fully repaints.  
        view.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    	Grid g = BoardTools2.getGridFromReal(new Point(e.getX(), e.getY()), panel.isZoomOn());
    	panel.updateHoverGrid(g);
    }
}