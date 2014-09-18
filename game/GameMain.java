package tactics;

import java.awt.*;
import java.awt.event.*;            // mouse listeners, window events
import javax.swing.*;

import tactics.control.MouseControl;
import tactics.control.CombatControl;
import tactics.control.MoveAction;
import tactics.control.TurnAction;
import tactics.control.ViewAction;
import tactics.model.GameLogic;
import tactics.view.BoardPanel;
import tactics.view.GridRule;
import tactics.view.GameUIManager;

public class GameMain {
	
	public static void init() {
		// the single "model" object instance
        final GameLogic gameLogic = new GameLogic();  

        // the single "view/ui" object instance
        GameUIManager guim = new GameUIManager(gameLogic);    
	}

	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                init();
            }
        });
	}
}