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

public class ViewAction extends AbstractAction 
						implements ChangeListener {
	private GameLogic gl;
	private BoardPanel panel;
	private GameUIManager uiManager;
	private String cmd;

	public ViewAction(String c, GameLogic logic, BoardPanel p, GameUIManager uim) {
		super(c);
		this.gl = logic;
		this.panel = p;
		this.uiManager = uim;
		this.cmd = c;
	}

	public void actionPerformed(ActionEvent e) {
		// handle zoom action		
		if (e.getActionCommand().equals("zoom")) {
			panel.setZoom();

			JViewport view = panel.getEnclosingPane().getViewport();
			view.setViewPosition(
	                BoardTools2.centerBoard(gl.getCurrentGrid(),
	                                        view.getWidth(), view.getHeight(),
	                                        panel.isZoomOn()));

			view.repaint();

			uiManager.updateZoomMenuItem(panel.isZoomOn());
		}
	}

	public void stateChanged(ChangeEvent e) {
		// handle jscrollpane change (drag/scroll)
		// update minimap

		Rectangle r =  panel.getEnclosingPane()
							.getViewport()
							.getViewRect();
		int scale = (panel.isZoomOn()) ? 15 : 7;

		r.setSize((int) r.getWidth() / scale, (int) r.getHeight() / scale);
		r.setLocation((int) r.getX() / scale, (int) r.getY() / scale);

		uiManager.updateMiniMapView(r);
	}
}