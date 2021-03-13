import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class ConnectFour extends JFrame{

    private ArrayList<Player> players = new ArrayList<>();

    private PlayArea playArea =  new PlayArea();

    enum Player{
        ONE, TWO
    }

    public ConnectFour(){
        setSize(750,750);
        setTitle("Connect 4");



    }

    public void init()
    {
        add(playArea);
        this.setVisible(true);
    }

    class PlayArea extends JPanel implements MouseListener {

        // connect four has 7 columns and 6 rows
        int rows = 6;
        int cols = 7;
        private char [][] values = new char[cols][rows];

        // saving width and height
        private int w, h;

        // set the Player.ONE goes first
        private Player player = Player.ONE;

        // flag for checking winner
        private boolean winner = false;
        private boolean firstTime = true; // flag to avoid painting circles when program is starting

        // set of points that define where next discs will be dropped
        private Point [] points = new Point[cols];

        public PlayArea()
        {
            // this class will catch mouse clicks
            addMouseListener(this);

            // background of the play area
            setBackground(Color.lightGray);

            // initialise set of starting points for each of the playing columns
            w = 750;
            h = 642;
            int y =  h - (h / rows - 1);
            int startingX = 2;
            int offsetX = w / cols;
            for (int i = 0; i < points.length; i++) {
                points[i] = new Point(startingX + (offsetX * i),y);
            }
        }

        public void drawGrid(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            int offset = 2;
            g2d.setStroke(new BasicStroke(offset * 2));

            // horizontal lines
            for(int i = 1; i < values.length; i++)
            {
                g2d.drawLine(((w/values.length)*i)+offset, 0, ((w/values.length)*i)+offset, h);
            }

            // vertical Lines
            for(int i = 1; i < values[0].length; i++)
            {
                g2d.drawLine(0, ((h/values[0].length)*i)+offset, w, ((h/values[0].length)*i)+offset);
            }


        }

        public void drawValues(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(new Color(150, 150, 150));

            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setRenderingHints(rh);

            g2d.fill(new Ellipse2D.Double(10, 100, 107, 107));

        }

        public void gridClicked(MouseEvent e)
        {
            Point p = e.getPoint();
            int colClicked = getColumnClicked(p);

            paintDisk(colClicked);

        }

        public void paintDisk(int col) {
            Graphics2D g2d = (Graphics2D) this.getGraphics();

            // set color
            if (player == Player.ONE) {
                g2d.setPaint(Color.RED);
                player = Player.TWO;
            }
            else {
                g2d.setPaint(Color.BLUE);
                player = Player.ONE;
            }


            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setRenderingHints(rh);

            // reset starting points and draw circle
            for (int i = 0; i < points.length; i++) {
                if (i == col) {
                    System.out.println("Drawing on point : " + points[i].getX() + ", " +  points[i].getY());
                    g2d.fill(new Ellipse2D.Double(points[i].getX(), points[i].getY(), 105, 105));
                    points[i].y -= h/rows;

                }
            }

        }

        /**
         * Method is analysing the coordinates of the Point and checks
         * what column of the connect four was clicked
         * @param p point that was clicked by the user
         * @return id of the column
         */
        public int getColumnClicked(Point p ) {
            int col = (int) (p.getX() / (w / cols));
            return col;
        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            w = this.getWidth();
            h = this.getHeight();
            System.out.println("W " + w);
            System.out.println("H " + h);

            drawGrid(g);

            if (!firstTime) {
                drawValues(g);
            }

            //next time repaint is called, the shapes will be drawn
            firstTime = false;
        }

        public boolean checkWin()
        {
            if (checkDiagonal())
                return true;

            if (checkAccross())
                return true;


            if (checkDown())
                return true;

            return  false;
        }

        public boolean checkDiagonal()
        {
            char centre = values[1][1];

            winner = false;

            if (values[0][0] == centre && values[2][2] == centre && centre != '\0')
                winner = true;

            if (values[2][0] == centre && values[0][2] == centre && centre != '\0')
                winner = true;

            return winner;
        }

        public boolean checkAccross()
        {
            for(int i = 0; i < values.length; i++)
            {
                char current = values[i][0];

                if(current !='\0')
                {
                    for (int j = 1; j < values.length; j++)
                    {
                        winner = true;
                        if (values[i][j] != current ||  values[i][j]  == '\0')
                        {
                            winner = false;
                            break;
                        }

                    }
                    if (winner)
                    {
                        return true;
                    }
                }
            }


            return false;
        }

        public boolean checkDown()
        {
            for(int i = 0; i < values.length; i++) {
                char current = values[0][i];

                if (current != '\0') {
                    for (int j = 1; j < values.length; j++) {
                        winner = true;
                        if (values[j][i] != current || values[j][i] == '\0') {
                            winner = false;
                            break;
                        }

                    }
                    if (winner) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("here");
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

            gridClicked(e);
            Point p = e.getPoint();
            System.out.println("Drawing on point : " + p.getX() + ", " +  p.getY());
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
