package tactics.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import tactics.GameConstants;

public class GameImage {

	private BufferedImage img, img_small;
    private BufferedImage gray_img, gray_img_small;

	public GameImage(String path, boolean def) {
		// load image
		try {
            this.img = ImageIO.read(((new File(path)).toURI()).toURL());
        } catch (IOException e) {
            System.out.println("File error for " + path + ". " + e);
            if (def) {
            	System.out.print("Creating default.");
            	this.img = new BufferedImage(GameConstants.LARGE_SQUARE_SIZE - 6,
            								 GameConstants.LARGE_SQUARE_SIZE - 6,
            								 BufferedImage.TYPE_INT_RGB);
            } else
            	System.exit(1);
        }

        // load small image
        String path_small = path.substring(0, path.length() - 4) 
        					+ "_small"
        					+ path.substring(path.length() - 4, path.length());
        try {
			//System.out.println("Loading " + path_small);
            this.img_small = ImageIO.read(((new File(path_small)).toURI()).toURL());
        } catch (IOException e) {
            System.out.println("File error for " + path_small + ". " + e);
            if (def) {
            	System.out.print("Creating default.");
            	this.img = new BufferedImage(GameConstants.SMALL_SQUARE_SIZE - 2,
            								 GameConstants.SMALL_SQUARE_SIZE - 2,
            								 BufferedImage.TYPE_INT_RGB);
            } else
            	System.exit(1);
        }

        gray_img = new BufferedImage(img.getHeight(), img.getWidth(), BufferedImage.TYPE_BYTE_GRAY);
        gray_img_small = new BufferedImage(img_small.getHeight(), img_small.getWidth(), BufferedImage.TYPE_BYTE_GRAY);

        Graphics gi = gray_img.getGraphics();
        gi.drawImage(img, 0, 0, null);
        gi.dispose();
        gi = gray_img_small.getGraphics();
        gi.drawImage(img_small, 0, 0, null);
        gi.dispose();

	}

	public Dimension getDimension(boolean isZoom) {
		return (isZoom) ? new Dimension(img.getWidth(), img.getHeight()) :
						  new Dimension(img_small.getWidth(), img_small.getHeight());
	}

	public BufferedImage getImage(boolean isZoom) {
		return (isZoom) ? img : img_small;
	}

    public BufferedImage getGrayImage(boolean isZoom) {
        return (isZoom) ? gray_img : gray_img_small;
    } 
}