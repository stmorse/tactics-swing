package tactics.view;

import java.awt.*;
import java.awt.event.*;            // mouse listeners, window events
import javax.swing.*;
import javax.swing.event.*;

import tactics.GameConstants;
import tactics.control.MouseControl;
import tactics.control.CombatControl;
import tactics.control.MoveAction;
import tactics.control.TurnAction;
import tactics.control.ViewAction;
import tactics.model.GameLogic;

public class GameUIManager {
	
	private GameLogic gl;
	
	private JFrame f = new JFrame("Tactics II");
    
    private BoardPanel boardPanel;
    private JScrollPane pane;

    private MiniMap miniMap;
    private JDialog miniMapDialog;

    private CombatDialog combatDialog;
    
	private JMenuBar menuBar;
    private JMenu fileMenu, viewMenu, actionsMenu;
    private JMenuItem newGameItem, openGameItem, 
    				  saveGameItem, preferencesItem, exitGameItem;		// File menu
    private JMenuItem zoomMenuItem, centerBoardItem, showMoveControlsItem,				// View menu 
    				  showCombatOddsItem,  showMiniMapItem;
    private JMenuItem endMovementItem, resolveCombatItem, fightItem, endTurnItem;	// Actions menu
	
	private JPanel statusPanel;
    private JLabel curGridLabel;
	private JLabel terrainLabel;
	private JLabel unitLabel;
	private JLabel messageLabel;
	private JLabel turnLabel;
	private String message;

    private MouseControl mc; 
    private CombatControl cc;
    //private ResizeListener resizeListener;

    private int sw, sh;			// screen width, height adjusted for insets
    private int mm_h, mm_w;		// minimap width, height
    private int cd_h, cd_w;
    private Rectangle miniMapBounds;
    private Rectangle combatBounds;
	
	public GameUIManager(GameLogic logic) {
		this.gl = logic;

		buildGUI();
	}

	///////////////////////////

	public BoardPanel getBoardPanel() { return boardPanel; }

	public void updateLabels() {
		curGridLabel.setText(gl.getCurrentGrid().getString());
		terrainLabel.setText(gl.getCurrentMapNode().getString());
		
		if (gl.getCurrentUnitIndex() > -1)
			unitLabel.setText(gl.getCurrentUnit().getString());
		else
			unitLabel.setText(" ");
		
		messageLabel.setText(message);
		turnLabel.setText(GameConstants.gameStateNames[gl.getGameState()]);

		statusPanel.repaint();
	}

	public void setMessage(String msg) {
		this.message = msg;
		messageLabel.setText(message.trim());
		statusPanel.repaint();
	}

	public void updateZoomMenuItem(boolean isZoomOn) {
		// set zoom text to opposite of current board panel zoom state
		if (isZoomOn)
			zoomMenuItem.setText("Zoom out");
		else
			zoomMenuItem.setText("Zoom in");
	}

	public void enableEndMovement(boolean isEnabled) { endMovementItem.setEnabled(isEnabled); }

	public void enableResolveCombat(boolean isEnabled) { resolveCombatItem.setEnabled(isEnabled); }

    public void enableFight(boolean isEnabled) { fightItem.setEnabled(isEnabled); }

	public void enableEndTurn(boolean isEnabled) { endTurnItem.setEnabled(isEnabled); }

	public void toggleMiniMap() {
		miniMapDialog.setVisible((miniMapDialog.isVisible()) ? false : true);
		showMiniMapItem.setText((miniMapDialog.isVisible()) ? "Hide Minimap" : "Show Minimap");
	}

	public void updateMiniMapView(Rectangle view) {
		miniMap.updateView(view);
	}

    public void toggleCombatOdds() {
        combatDialog.setVisible((combatDialog.isVisible()) ? false : true);
        showCombatOddsItem.setText((combatDialog.isVisible()) ? "Hide Combat odds" : "Show Combat odds");
    }

	///////////////////////////

	static void renderSplashProgress(Graphics2D g, int frame) {
        final String[] comps = {"foo", "bar", "baz"};
        //g.setComposite(AlphaComposite.Clear);
 
        g.setColor(Color.BLACK);
        g.setPaintMode();
        g.fillRect(120, 160, frame * 4, 10);       
        g.drawString("Loading " + comps[(frame / 5) % 3] + "...", 
        			 120, 150);
        g.dispose();
    }

	///////////////////////////

	private void buildGUI() {
		// get usable screen dimensions
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        Insets ins = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        sw = gc.getBounds().width - ins.left - ins.right;
        sh = gc.getBounds().height - ins.top - ins.bottom;

        ////////
        // setup statusPanel
        buildStatusPanel();

        ////////
        // setup center panel/scrollpane

        boardPanel = new BoardPanel(gl);
        boardPanel.setPreferredSize(boardPanel.getPreferredSize());

        pane = new JScrollPane(boardPanel);
        pane.setPreferredSize(new Dimension(sw - 200, sh));
        
        boardPanel.setGridRules(pane);
        boardPanel.setEnclosingPane(pane);
        
        /////////
        // setup minimap
        
        miniMapDialog = new JDialog(f);
        miniMap = new MiniMap();
        miniMapDialog.setPreferredSize(miniMap.getPreferredSize());
        miniMapDialog.add(miniMap);			// flow layout
        mm_w = (int) miniMap.getPreferredSize().getWidth();
        mm_h = (int) miniMap.getPreferredSize().getHeight();
        miniMapBounds = new Rectangle(sw - 200 - mm_w - 50,
        							  sh - mm_h - 10,
        							  mm_w, mm_h);
        miniMapDialog.setBounds(miniMapBounds);
        miniMapDialog.setUndecorated(true);
        miniMapDialog.setFocusableWindowState(false);
        //miniMap.setAlwaysOnTop(true);
        miniMapDialog.setOpacity(0.75f);
        miniMapDialog.setVisible(false);

        /////////
        // setup combat odds view

        combatDialog = new CombatDialog(f);
       
        combatDialog.setPreferredSize(
            new Dimension((int)miniMap.getPreferredSize().getWidth(), 300));
        cd_w = (int) miniMap.getPreferredSize().getWidth();
        cd_h = 300;
        combatBounds = new Rectangle(sw - 200 - cd_w - 50,
                                     sh - mm_h - 10 - cd_h - 10,
                                     cd_w, cd_h);
        combatDialog.setBounds(combatBounds);
        combatDialog.setUndecorated(true);
        combatDialog.setFocusableWindowState(false);
        combatDialog.setOpacity(0.75f);
        combatDialog.setVisible(false);

        
        /////////
        // action listeners and key bindings

        mc = new MouseControl(gl, boardPanel, this);
        cc = new CombatControl(gl, boardPanel, this);
        
        boardPanel.addMouseListener(mc);
        boardPanel.addMouseMotionListener(mc);
        boardPanel.addMouseListener(cc);

        combatDialog.getFightButton().addActionListener(cc);

        pane.getViewport().addChangeListener(new ViewAction("scroll", gl, boardPanel, this));
        
        pane.addComponentListener(new ComponentListener() {
        	@Override
        	public void componentResized(ComponentEvent e) {
        		int w = pane.getWidth();
        		int h = pane.getHeight();
        		miniMapBounds = new Rectangle(w - mm_w - 50,
        									  h - mm_h - 10,
        									  mm_w, mm_h);
        		miniMapDialog.setBounds(miniMapBounds);
        	}

        	public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		    public void componentShown(ComponentEvent e) {}
        });

        // movement key bindings
        String d, k;
        for (int i = 0; i < 8; i++) {
            d = GameConstants.keyMaps[i][0];
            k = GameConstants.keyMaps[i][1];
            boardPanel.getInputMap().put(KeyStroke.getKeyStroke(k), d);
            boardPanel.getActionMap().put(d, new MoveAction(d, gl, boardPanel, this));
        }         
        
        //////////
        // setup menu bar
        buildMenuBar();
        

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

		//////////////

		final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
	        Graphics2D g = splash.createGraphics();
	        if (g == null) {
	            System.out.println("g is null");
	        } else {
		        for(int i = 0; i < 100; i++) {
		            renderSplashProgress(g, i);
		            splash.update();
		            try {
		                Thread.sleep(90);
		            }
		            catch(InterruptedException e) {
		            }
		        }
		        splash.close();
	    	}
        }        

        /////////////////

		/*java.net.URL url = ClassLoader.getSystemResource("tactics/res/img/units/red/red_infantry.jpg");
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage(url);
		f.setIconImage(img);*/

        boardPanel.requestFocusInWindow();
        f.setVisible(true); 
	}

	private void buildStatusPanel() {
		this.statusPanel = new JPanel();

		this.curGridLabel = new JLabel();
		this.terrainLabel = new JLabel();
		this.unitLabel = new JLabel();
		this.messageLabel = new JLabel();
		this.turnLabel = new JLabel();

		this.message = " ";

		Font f = new Font("SansSerif", Font.PLAIN, 10);
		curGridLabel.setFont(f);
		terrainLabel.setFont(f);
		unitLabel.setFont(f);
		messageLabel.setFont(f);
		turnLabel.setFont(f);

		updateLabels();

		statusPanel.setBorder(BorderFactory.createEmptyBorder());
        statusPanel.setPreferredSize(new Dimension(1100, 15));
        statusPanel.setLayout(new GridLayout(1,0, 5, 0));

        statusPanel.add(curGridLabel);
        statusPanel.add(terrainLabel);
        statusPanel.add(unitLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(50, 0)));
        statusPanel.add(messageLabel);
        statusPanel.add(turnLabel);
	}

	private void buildMenuBar() {
		menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        newGameItem = new JMenuItem("New Game...");
        fileMenu.add(newGameItem);

        openGameItem = new JMenuItem("Open Game...");
        fileMenu.add(openGameItem);

        saveGameItem = new JMenuItem("Save game...");
        fileMenu.add(saveGameItem);

        fileMenu.addSeparator();

        preferencesItem = new JMenuItem("Preferences...");
        fileMenu.add(preferencesItem);

        fileMenu.addSeparator();

        exitGameItem = new JMenuItem("Exit...");
        //exitGameItem.addWindowListener(new WindowAdapter() {
        //    public void windowClosing(WindowEvent e) {System.exit(0);}
        //});
        fileMenu.add(exitGameItem);

        /////

        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menuBar.add(viewMenu);

        zoomMenuItem = new JMenuItem(new ViewAction("Zoom out", gl, boardPanel, this));
        //zoomMenuItem.setMnemonic(KeyEvent.VK_Z);
        zoomMenuItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        zoomMenuItem.setActionCommand("zoom");
        viewMenu.add(zoomMenuItem);

        centerBoardItem = new JMenuItem(new ViewAction("Center board", gl, boardPanel, this));
        centerBoardItem.setAccelerator(KeyStroke.getKeyStroke("control C"));
        centerBoardItem.setActionCommand("center");
        viewMenu.add(centerBoardItem);

        viewMenu.addSeparator();

        showMoveControlsItem = new JMenuItem("Show controls");
        showMoveControlsItem.setAccelerator(KeyStroke.getKeyStroke("control N"));
        viewMenu.add(showMoveControlsItem);

        showCombatOddsItem = new JMenuItem("Show combat odds");
        showCombatOddsItem.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        showCombatOddsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                toggleCombatOdds();
            }
        });
        viewMenu.add(showCombatOddsItem);

        showMiniMapItem = new JMenuItem("Show mini-map");
        showMiniMapItem.setAccelerator(KeyStroke.getKeyStroke("control M"));
        showMiniMapItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                toggleMiniMap();
            }
        });
        viewMenu.add(showMiniMapItem);

        //////

        actionsMenu = new JMenu("Actions");
        actionsMenu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(actionsMenu);

        endMovementItem = new JMenuItem(new TurnAction(TurnAction.END_MOVEMENT, gl, f, boardPanel, this));
        endMovementItem.setAccelerator(KeyStroke.getKeyStroke("control shift M"));
        endMovementItem.setActionCommand("end move");
        actionsMenu.add(endMovementItem);

        resolveCombatItem = new JMenuItem(new TurnAction(TurnAction.RESOLVE_COMBAT, gl, f, boardPanel, this));
        resolveCombatItem.setAccelerator(KeyStroke.getKeyStroke("control shift C"));
        resolveCombatItem.setActionCommand("resolve combat");
        actionsMenu.add(resolveCombatItem);

        fightItem = new JMenuItem("Fight battle");
        fightItem.setAccelerator(KeyStroke.getKeyStroke("control alt F"));
        fightItem.setActionCommand("fight");
        fightItem.setActionListener(cc);
        actionsMenu.add(fightItem);

        endTurnItem = new JMenuItem(new TurnAction(TurnAction.END_TURN, gl, f, boardPanel, this));
        endTurnItem.setAccelerator(KeyStroke.getKeyStroke("control shift T"));
        endTurnItem.setActionCommand("end turn");
        actionsMenu.add(endTurnItem);
	}

}