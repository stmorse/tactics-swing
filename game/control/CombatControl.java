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
import tactics.model.CombatLogic;
import tactics.model.Battle;
import tactics.model.Grid;
import tactics.view.BoardPanel;
import tactics.view.GameUIManager;

public class CombatControl extends AbstractAction
						  implements MouseListener  {
	
	private GameLogic gl;
	private CombatLogic combat;
	private BoardPanel panel;
	private GameUIManager uiManager;

	public CombatControl(GameLogic logic, BoardPanel bp, GameUIManager uim) {
		super();
		this.gl = logic;
		this.combat = gl.getCombatLogic();
		this.panel = bp;
		this.uiManager = uim;
	}

	public void actionPerformed(ActionEvent e) {
		return;
	}

	public void mouseClicked(MouseEvent e) {
		// check if in combat
		if (gl.getGameState() == GameConstants.RED_MOVE ||
			gl.getGameState() == GameConstants.BLUE_MOVE ||
			gl.getGameState() == GameConstants.GAME_OVER)
			return;

		int x = e.getX();
        int y = e.getY();
        int mask = InputEvent.CTRL_MASK | InputEvent.BUTTON1_MASK;

        // set current grid on this square regardless of type of click
        gl.setCurrentGrid(BoardTools2.getGridFromReal(new Point(x,y), panel.isZoomOn()));

        // check click (double-click) on unit
        int index = gl.getUnitIndexAtGrid(gl.getCurrentGrid());
        if (index >= 0 && e.getClickCount() == 2) {
        	// double clicked on unit
        	combat.handleBattleToggle(gl.getUnit(index));
        // check ctrl-click if current battle and click on a unit
        } else if (combat.getCurrentBattle() != null && 
        		   (e.getModifiers() & mask) == mask &&
        		   index >= 0) {
        	// user attempting to add/remove unit 
        	combat.handleUnitToggle(gl.getUnit(index));
        }

        Battle b = combat.getCurrentBattle();
        if (b != null)
        	uiManager.setMessage(b.getString());

        panel.repaint();
        uiManager.updateLabels();
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}