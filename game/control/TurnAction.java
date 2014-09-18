package tactics.control;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.view.BoardPanel;
import tactics.view.GameUIManager;

public class TurnAction extends AbstractAction {
	private GameLogic gl;
	private JFrame frame;
	private BoardPanel panel;
	private GameUIManager uiManager;
	private int action;

	public static int END_MOVEMENT = 0, RESOLVE_COMBAT = 1, END_TURN = 2;
	public static String[] LABELS = { "End movement", "Resolve combat", "End turn" };

	public TurnAction(int a, GameLogic logic, JFrame f, BoardPanel bp, GameUIManager uim) {
		super(LABELS[a]);
		this.gl = logic;
		this.frame = f;
		this.panel = bp;
		this.uiManager = uim;
		this.action = a;
	}

	public void actionPerformed(ActionEvent e) {
		if (this.action == END_MOVEMENT) {
			if (gl.endMovementPhase()) {
				// beginning combat phase message
				String cstr = "";
				if (gl.getUnitsInCombat().isEmpty()) {
					cstr = "Ending movement and turn.\n(No units in combat.)";
					uiManager.enableEndMovement(true);
					uiManager.enableResolveCombat(false);
					uiManager.enableEndTurn(true);
				} else {
					cstr = "Ending movement.\nBegin combat phase.";
					uiManager.enableEndMovement(false);
					uiManager.enableResolveCombat(true);
					uiManager.enableEndTurn(false);
					panel.setIsMove(false);
				}					
				
				JOptionPane.showMessageDialog(frame,
				    						  cstr,
				    						  "End movement warning",
				    						  JOptionPane.INFORMATION_MESSAGE);
				
				// regardless if there's combat or not
				panel.setUnitHasFocus(false);								
			} else {
				// error message
				JOptionPane.showMessageDialog(frame,
				    "Error. Cannot end movement phase.",
				    "End movement warning",
				    JOptionPane.WARNING_MESSAGE);
			}
		} else if (this.action == RESOLVE_COMBAT) {
			if (gl.resolveCombat()) {
				// combat resolved message
				JOptionPane.showMessageDialog(frame,
				    "All combat resolved. \nNo other actions available.",
				    "Resolve combat warning",
				    JOptionPane.INFORMATION_MESSAGE);
				uiManager.enableEndMovement(false);
				uiManager.enableResolveCombat(false);
				uiManager.enableEndTurn(true);
			} else {
				// error message
				JOptionPane.showMessageDialog(frame,
				    "Error. Cannot resolve combat.",
				    "Resolve combat warning",
				    JOptionPane.WARNING_MESSAGE);
			}
		} else if (this.action == END_TURN) {
			if (gl.endPlayerTurn()) {
				// turn over, other player's turn
				JOptionPane.showMessageDialog(frame,
				    "All combat resolved. \nNo other actions available.",
				    "Resolve combat warning",
				    JOptionPane.INFORMATION_MESSAGE);
				panel.setUnitHasFocus(false);
				panel.setIsMove(true);
				uiManager.enableEndMovement(true);
				uiManager.enableResolveCombat(false);
				uiManager.enableEndTurn(true);
			} else {
				// error message
				JOptionPane.showMessageDialog(frame,
				    "Cannot end turn.",
				    "End turn warning",
				    JOptionPane.WARNING_MESSAGE);
			}
		}

		uiManager.updateLabels();
	}
}