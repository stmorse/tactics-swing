package tactics.view;

import java.awt.*;
import javax.swing.*;

import tactics.GameConstants;
import tactics.model.Grid;

public class GridRule extends JComponent {

    public static final int HORIZONTAL = 0, VERTICAL = 1;
    public static final int SIZE = 12;

    public int orientation;
    public boolean isZoom;
    private int increment;
    private Grid hoverGrid;

    public GridRule(int or, boolean z) {
        orientation = or;
        isZoom = z;
        hoverGrid = new Grid(0, 0);
        setIncrement();
    }

    public void setZoom() {
        this.isZoom = (isZoom) ? false : true;
        setIncrement();
        repaint();
    }

    public void setZoom(boolean z) {
        this.isZoom = z;
        setIncrement();
        repaint();
    }

    private void setIncrement() {
        if (isZoom) {
            increment = GameConstants.LARGE_SQUARE_SIZE;
        } else {
            increment = GameConstants.SMALL_SQUARE_SIZE;
        }
    }

    public void setHoverGrid(Grid g) {
        this.hoverGrid = g;
        repaint();
    }

    public boolean isZoom() {
        return this.isZoom;
    }

    public int getIncrement() {
        return increment;
    }

    public void setPreferredHeight(int ph) {
        setPreferredSize(new Dimension(SIZE, ph));
    }

    public void setPreferredWidth(int pw) {
        setPreferredSize(new Dimension(pw, SIZE));
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Rectangle drawHere = g2.getClipBounds();

        // Fill clipping area with white.
        g2.setColor(Color.white);
        g2.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        // Some vars we need.
        int end = 0;
        int start = 0;
        int tickLength = SIZE - 2;
        String text = null;
        int txtOffset = 0;

        // Use clipping bounds to calculate first and last tick locations.
        if (orientation == HORIZONTAL) {
            start = (drawHere.x / increment) * increment;
            end = (((drawHere.x + drawHere.width) / increment) + 1) * increment;
        } else {
            start = (drawHere.y / increment) * increment;
            end = (((drawHere.y + drawHere.height) / increment) + 1) * increment;
        }

        // draw the hover grid highlight
        g2.setColor(Color.lightGray);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
        if (orientation == HORIZONTAL) {
            g2.fillRect((hoverGrid.x * increment) + 1, 0, 
                        increment - 1, SIZE - 1);
        } else {
            g2.fillRect(0, (hoverGrid.y * increment) + 1, 
                       SIZE - 1, increment - 1);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // ticks and labels
        g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
        g2.setColor(Color.black);
        for (int i = start; i < end; i += increment) {
            text = Integer.toString(i/increment);

            if (orientation == HORIZONTAL) {
                g2.drawLine(i, SIZE - 1, i, SIZE - tickLength - 1);
                if (text != null) {
                    txtOffset = (increment / 2);
                    txtOffset += (i < 10) ? 2 : 4;
                    g2.drawString(text, i - txtOffset, (SIZE / 2) + 2);
                }                    
            } else {
                g2.drawLine(SIZE-1, i, SIZE-tickLength-1, i);
                if (text != null) {
                    txtOffset = (SIZE / 2);
                    txtOffset -= (i < 10) ? 2 : 4;
                    g2.drawString(text, txtOffset, i + (increment/2));
                }                   
            }
        }
    }
}