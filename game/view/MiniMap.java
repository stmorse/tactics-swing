package tactics.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
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

public class MiniMap extends JPanel  {

	BufferedImage img;
	String path = GameConstants.miniMapImgPath;
	Rectangle view;

	public MiniMap() {
		super();

		try {
            this.img = ImageIO.read(((new File(path)).toURI()).toURL());
        } catch (IOException e) {
            System.out.println("File error for " + path + ". " + e);
        }

        view = new Rectangle(0, 0, img.getWidth(), img.getHeight());
	}

	public Dimension getPreferredSize() {
		return new Dimension(img.getWidth(), img.getHeight());
	}

	public void updateView(Rectangle v) {
		view = v;
		this.repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.drawImage(img, null, 0, 0);

		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(1.0f));

		g2.draw(view);

		g2.dispose();
	}
}