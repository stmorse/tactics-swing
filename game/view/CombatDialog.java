package tactics.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.Battle;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.model.Unit;

public class CombatDialog extends JDialog  {
	CombatPanel combatPanel;
	JLabel diceLabel;
	JButton fightButton;
	JScrollPane combatScroll;

	BufferedImage combatOddsImg;
	ImageIcon[] diceIcons;
	String dicePath = "tactics/res/img/dice/die";

	public CombatDialog(JFrame f) {
		super(f);

		diceIcons = new ImageIcon[6];
		for (int i = 0; i < 6; i++) {
			diceIcons[i] = new ImageIcon(dicePath + (i+1) + ".png", "Roll" + (i+1));
		}

		combatPanel = new CombatPanel();
		diceLabel = new JLabel(diceIcons[1]);
		fightButton = new JButton("Fight");

		combatScroll = new JScrollPane(combatPanel);
		combatScroll.setPreferredSize(combatPanel.getPreferredSize());
		combatScroll.setMinimumSize(
			new Dimension(100, (int) combatPanel.getPreferredSize().getHeight() + 20));
		combatScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		this.setPreferredSize(new Dimension(100, 300));

		// do layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		this.add(fightButton, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		this.add(diceLabel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		//c.weightx = 0.75;
		//c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		this.add(combatScroll, c);
	}

	public JButton getFightButton() {
		return fightButton;
	}

	protected ImageIcon createImageIcon(String path, String description) {
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	        return new ImageIcon(imgURL, description);
	    } else {
	        System.err.println("Couldn't find file: " + path);
	        return null;
	    }
	}
}

/////////////////

class CombatPanel extends JPanel {
	BufferedImage combatOddsImg;
	String path = GameConstants.combatOddsImgPath;

	public CombatPanel() {
		super();

		// load combat odds image
		try {
            this.combatOddsImg = ImageIO.read(((new File(path)).toURI()).toURL());
        } catch (IOException e) {
            System.out.println("File error for " + path + ". " + e);
        }
	}

	public Dimension getPreferredSize() {
		return new Dimension(combatOddsImg.getWidth(), combatOddsImg.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.drawImage(combatOddsImg, null, 0, 0);

		// for now...
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		g2.setColor(Color.yellow);
		g2.fillRect(10, 10, 100, 100);

		g2.dispose();
	}
}