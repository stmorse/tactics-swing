package tactics.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.ArrayList;
import java.util.Iterator;

import tactics.BoardTools2;
import tactics.GameConstants;
import tactics.model.Battle;
import tactics.model.GameLogic;
import tactics.model.Grid;
import tactics.model.Unit;

public class BoardPanel extends JPanel  {

    private JScrollPane enclosingPane;
    private GridRule columnRule, rowRule;
    private Point mousePos;
    private GameImage boardImage;
    private GameImage[][] unitImages;
    private int sqSize, unitOffset;
    private boolean isZoomOn;
    private boolean unitHasFocus, isMove;

    private GameLogic gl;

    private float blink, count;
    private int timerSpeed;
    Action outlineTimer = new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            count -= 0.1f;
            if (count < -1.0f)
                count = 1.0f;
            blink = Math.abs(count);
            repaint();
        }
    };

    public BoardPanel(GameLogic gLogic) {
        // load images
        this.boardImage = new GameImage(GameConstants.boardImgPath, false);
        //this.outlineImage = new GameImage(GameConstants.outlineImgPath, false);
        this.unitImages = new GameImage[GameConstants.NUM_ARMIES][GameConstants.NUM_UNIT_TYPES];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 8; j++) 
                    unitImages[i][j] = new GameImage(GameConstants.unitImgPath[i][j], true);
        
        this.mousePos = new Point(0,0);
        this.isZoomOn = true;
        this.sqSize = GameConstants.LARGE_SQUARE_SIZE;
        this.unitOffset = GameConstants.LARGE_UNIT_OFFSET;
        this.unitHasFocus = false;
        this.isMove = true;

        this.blink = 1.0f;
        this.count = 1.0f;
        this.timerSpeed = 100;
        new Timer(timerSpeed, outlineTimer).start();

        this.gl = gLogic;
    }

    public void paintComponent(Graphics g) {
        //super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        /*  // clipping coords
        int upperLeftX = g.getClipBounds().x;
        int upperLeftY = g.getClipBounds().y;
        int visibleWidth = g.getClipBounds().width;
        int visibleHeight = g.getClipBounds().height;
        */

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        
        g2.drawImage(boardImage.getImage(isZoomOn), null, 0, 0);

        if (isMove)
            paintMovementPhase(g2);
        else
            paintCombatPhase(g2);

        // draw units
        Unit u;
        for (int i = 0; i < GameConstants.NUM_UNITS; i++) {
            u = gl.getUnit(i);
            if (u != null && u.isAlive()) {
                g2.drawImage(unitImages[u.getArmy()][u.getType()].getImage(isZoomOn), null, 
                          u.getGridX() * sqSize + unitOffset, 
                          u.getGridY() * sqSize + unitOffset);    
            } else if (u != null && u.isJustKilled()) {
                g2.drawImage(unitImages[u.getArmy()][u.getType()].getGrayImage(isZoomOn), null, 
                          u.getGridX() * sqSize + unitOffset, 
                          u.getGridY() * sqSize + unitOffset);
            }            
        }

        // draw outline
        // only have it blink if it's on a unit
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (unitHasFocus) ? blink : 1.0f));
        g2.setColor(Color.black);
        g2.setStroke(new BasicStroke(3.0f));
        g2.draw(new Rectangle(gl.getCurrentGrid().x * sqSize - 1, 
                              gl.getCurrentGrid().y * sqSize - 1,
                              sqSize + 2,
                              sqSize + 2));
        
        g2.dispose();
    }

    private void paintMovementPhase(Graphics2D g2) {
        // draw path squares if a unit is selected
        if (gl.getCurrentUnitIndex() >= 0) {
            Unit cu = gl.getCurrentUnit();
            
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f));
            
            Iterator pi = cu.unitPath.iterator();
            String str;
            Grid p;
            while (pi.hasNext()) {
                // draw the white transparent square
                g2.setColor(Color.white);
                p = (Grid) pi.next();
                g2.fill(new Rectangle(p.x * sqSize + 1, p.y * sqSize + 1, 
                                       sqSize - 1, sqSize - 1));
                //bc2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                
                // draw how many moves it has taken to that point
                g2.setColor(Color.black);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                str = new Integer((int)(p.num / 3)).toString(); 
                str += ((p.num % 3) == 2) ? "-2" : "";
                str += ((p.num % 3) == 1) ? "-1" : "";
                g2.drawString(str, (p.x * sqSize) + (sqSize / 2) - 3, 
                                    (p.y * sqSize) + (sqSize / 2) + 3);
            }
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        return;
    }

    private void paintCombatPhase(Graphics2D g2) {
        // draw highlight squares if unit is in combat
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2.setColor(Color.red);
        
        Iterator ci = gl.getUnitsInCombat().iterator();
        Grid c;
        while (ci.hasNext()) {
            // draw the red transparent square            
            c = ((Unit) ci.next()).getGrid();
            g2.fill(new Rectangle(c.x * sqSize - 4, c.y * sqSize - 4, 
                                       sqSize + 8, sqSize + 8));
        }

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // draw combat polygons

        ArrayList<Battle> ob = gl.getCombatLogic().getOpenBattles();
        Battle currentBattle = gl.getCombatLogic().getCurrentBattle();
        ArrayList<Edge> poly;
        Edge e;
        float dash1[] = {10.0f};

        g2.setColor(Color.black);

        ob = gl.getCombatLogic().getOpenBattles();
        for (int i = 0; i < ob.size(); i++) {
            poly = ob.get(i).getBoundingPolygon(isZoomOn);
            
            if (ob.get(i).equals(currentBattle)) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, blink));
                g2.setStroke(new BasicStroke(3.0f,
                                     BasicStroke.CAP_BUTT,
                                     BasicStroke.JOIN_MITER,
                                     10.0f, dash1, 0.0f));
            } else {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2.setStroke(new BasicStroke(3.0f));
            }
            
            for (int j = 0; j < poly.size(); j++) {
                e = poly.get(j);
                g2.drawLine(e.start.x, e.start.y, e.end.x, e.end.y);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }

    /////////////////////////

    public void updateHoverGrid(Grid g) {
        columnRule.setHoverGrid(g);
        rowRule.setHoverGrid(g);
        enclosingPane.repaint();
    }

    public void setEnclosingPane(JScrollPane pane) {
        enclosingPane = pane;
    }

    public JScrollPane getEnclosingPane() { return enclosingPane; }
    
    public void setGridRules(JScrollPane pane) {
        columnRule = new GridRule(GridRule.HORIZONTAL, true);
        columnRule.setPreferredWidth((int)this.getPreferredSize().getWidth());
        rowRule = new GridRule(GridRule.VERTICAL, true);
        rowRule.setPreferredHeight((int)this.getPreferredSize().getHeight());
        pane.setColumnHeaderView(columnRule);
        pane.setRowHeaderView(rowRule);
    }

    public Dimension getPreferredSize() {
        return new Dimension(boardImage.getDimension(isZoomOn));
    }

    public void setZoom() {
        setZoom((isZoomOn) ? false : true);
    }

    public void setZoom(boolean s) {
        isZoomOn = s;
        columnRule.setZoom();
        rowRule.setZoom();
        sqSize = (isZoomOn) ? GameConstants.LARGE_SQUARE_SIZE : GameConstants.SMALL_SQUARE_SIZE;
        unitOffset = (isZoomOn) ? GameConstants.LARGE_UNIT_OFFSET : GameConstants.SMALL_UNIT_OFFSET;
    }

    public boolean isZoomOn() { return isZoomOn; }

    public void setUnitHasFocus(boolean f) { this.unitHasFocus = f; }

    public boolean isUnitHasFocus() { return unitHasFocus; }

    public void setIsMove(boolean s) {
        this.isMove = s;
    }

}