package tactics.view;

import java.awt.*;
import java.awt.event.*;            // mouse listeners, window events
import javax.swing.*;

import tactics.GameConstants;
import tactics.control.MouseControl;
import tactics.control.CombatControl;
import tactics.control.MoveAction;
import tactics.control.TurnAction;
import tactics.control.ViewAction;
import tactics.model.GameLogic;

public class UIManager {
	
	private GameLogic gameLogic;
	
	JFrame f = new JFrame("Tactics II");
    BoardPanel boardPanel;
    JScrollPane pane;
    StatusPanel statusPanel;
	JMenuBar menuBar;
    JMenu fileMenu, viewMenu, actionsMenu;
    JMenuItem zoomMenuItem, endMovementItem, resolveCombatItem, endTurnItem;

    MouseControl mc; 
    CombatControl cc;
	
	public UIManager(GameLogic gl) {
		this.gameLogic = gl;

		buildGUI();
	}

	///////////////////////////

	public BoardPanel getBoardPanel() { return boardPanel; }

	public void updateLabels() {
		statusPanel.updateLabels();
	}

	public void updateZoomMenuItem(boolean isZoomOn) {
		// set zoom text to opposite of current board panel zoom state
		if (isZoomOn)
			zoomMenuItem.setText("Zoom out");
		else
			zoomMenuItem.setText("Zoom in");
	}

	public void enableEndMovement(boolean isEnabled) {
		endMovementItem.setEnabled(isEnabled);
	}

	public void enableResolveCombat(boolean isEnabled) {
		resolveCombatItem.setEnabled(isEnabled);
	}

	public void enableEndTurn(boolean isEnabled) {
		endTurnItem.setEnabled(isEnabled);
	}

	///////////////////////////

	private void buildGUI() {
        // setup statusPanel
        statusPanel = new StatusPanel(gameLogic);

        ////////
        // setup center panel/scrollpane

        boardPanel = new BoardPanel(gameLogic);
        boardPanel.setPreferredSize(boardPanel.getPreferredSize());

        pane = new JScrollPane(boardPanel);
        pane.setPreferredSize(new Dimension(1100, 1000));
        
        boardPanel.setGridRules(pane);
        boardPanel.setEnclosingPane(pane);
        
        /////////
        // action listeners and key bindings

        mc = new MouseControl(gameLogic, boardPanel, this);
        cc = new CombatControl(gameLogic, boardPanel, this);

        boardPanel.addMouseListener(mc);
        boardPanel.addMouseMotionListener(mc);
        boardPanel.addMouseListener(cc);

        // movement key bindings
        String d, k;
        for (int i = 0; i < 8; i++) {
            d = GameConstants.keyMaps[i][0];
            k = GameConstants.keyMaps[i][1];
            boardPanel.getInputMap().put(KeyStroke.getKeyStroke(k), d);
            boardPanel.getActionMap().put(d, new MoveAction(d, gameLogic, boardPanel, this));
        }         
        
        //////////
        // setup menu bar
        
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu);

        zoomMenuItem = new JMenuItem(new ViewAction("Zoom out", gameLogic, boardPanel, this));
        //zoomMenuItem.setMnemonic(KeyEvent.VK_Z);
        zoomMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        zoomMenuItem.setActionCommand("zoom");
        viewMenu.add(zoomMenuItem);

        actionsMenu = new JMenu("Actions");
        actionsMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(actionsMenu);

        endMovementItem = new JMenuItem(new TurnAction(TurnAction.END_MOVEMENT, gameLogic, f, boardPanel, this));
        endMovementItem.setAccelerator(KeyStroke.getKeyStroke("control shift M"));
        endMovementItem.setActionCommand("end move");
        actionsMenu.add(endMovementItem);

        resolveCombatItem = new JMenuItem(new TurnAction(TurnAction.RESOLVE_COMBAT, gameLogic, f, boardPanel, this));
        resolveCombatItem.setAccelerator(KeyStroke.getKeyStroke("control shift C"));
        resolveCombatItem.setActionCommand("resolve combat");
        actionsMenu.add(resolveCombatItem);

        endTurnItem = new JMenuItem(new TurnAction(TurnAction.END_TURN, gameLogic, f, boardPanel, this));
        endTurnItem.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
        endTurnItem.setActionCommand("end turn");
        actionsMenu.add(endTurnItem);

		
        ///////////
        // setup main frame

        f.setLayout(new BorderLayout());
        f.setPreferredSize(pane.getPreferredSize());
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });

        f.setJMenuBar(menuBar);
        f.add(pane, BorderLayout.CENTER);
        f.add(statusPanel, BorderLayout.PAGE_END);
        
        f.pack();
        //boardPanel.requestFocusInWindow();
        f.setVisible(true); 
	}
}