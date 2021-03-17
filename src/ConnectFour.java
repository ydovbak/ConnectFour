import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class ConnectFour extends JFrame {

    private ArrayList<Player> players = new ArrayList<>();

    private PlayArea playArea = new PlayArea();

    enum Player {
        ONE, TWO
    }

    public ConnectFour() {
        setSize(750, 750);
        setTitle("Connect 4");


    }

    public void init() {
        add(playArea);
        this.setVisible(true);
    }

    class PlayArea extends JPanel implements MouseListener {

        // connect four has 7 columns and 6 rows
        final int ROWS = 6;
        final int COLS = 7;

        // this array will save the moves of both players "R" - player ONE, "B" - player TWO
        private char[][] playersMoves = new char[ROWS][COLS];

        // saving width and height
        private int w, h;

        // set the Player.ONE goes first
        private Player player = Player.ONE;

        // flag for checking winner
        private boolean winner = false;
        private boolean firstTime = true; // flag to avoid painting circles when program is starting

        // set of points that define where next discs will be dropped
        private Point[] points = new Point[COLS];

        public PlayArea() {
            // this class will catch mouse clicks
            addMouseListener(this);

            // background of the play area
            setBackground(Color.lightGray);

            // initialise set of starting points for each of the playing columns
            w = 750;
            h = 642;
            int y = h - (h / ROWS - 1);
            int startingX = 2;
            int offsetX = w / COLS;
            for (int i = 0; i < points.length; i++) {
                points[i] = new Point(startingX + (offsetX * i), y);
            }

//            for(int i = 0; i < rows; i++) {
//                for (int j = 0; j < cols; j++) {
//                    if (playersMoves[j][i] == (char)0 ) {
//                        System.out.println("true");
//                    }
//                }
//            }
        }

        public void drawGrid(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            int offset = 2;
            g2d.setStroke(new BasicStroke(offset * 2));

            // horizontal lines, by getting num of cols
            for (int i = 1; i < playersMoves[0].length; i++) {
                g2d.drawLine(((w / playersMoves[0].length) * i) + offset, 0, ((w / playersMoves[0].length) * i) + offset, h);
            }

            // vertical Lines, by getting num or rows
            for (int i = 1; i < playersMoves.length; i++) {
                g2d.drawLine(0, ((h / playersMoves.length) * i) + offset, w, ((h / playersMoves.length) * i) + offset);
            }


        }

        public void drawValues(Graphics g) {
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

        public void gridClicked(MouseEvent e) {
            Point p = e.getPoint();
            int colClicked = getColumnClicked(p);

            paintDisk(colClicked);

            // fill the playing matrix and get the row pos of the click
            int row = playerDroppedDisc(colClicked);

            // set the color
            char color;
            if (player == Player.ONE) {
                color = 'R';
            } else {
                color = 'B';
            }

            // check if won
            if (isConnected(row, colClicked, color)) {
                // player won
                System.out.println("Player " + player + " has won");
                showMessageDialog(null, "Player " + player + " has won");
                System.exit(0);
            }
            else {
                // switch player turns
                if (player == Player.ONE) {
                    player = Player.TWO;
                } else {
                    player = Player.ONE;
                }
            }

        }

        public void paintDisk(int col) {
            Graphics2D g2d = (Graphics2D) this.getGraphics();

            // set color
            if (player == Player.ONE) {
                g2d.setPaint(Color.RED);
            } else {
                g2d.setPaint(Color.BLUE);
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
                    System.out.println("Drawing on point : " + points[i].getX() + ", " + points[i].getY());
                    g2d.fill(new Ellipse2D.Double(points[i].getX(), points[i].getY(), 105, 105));
                    points[i].y -= h / ROWS;

                }
            }

        }

        /**
         * Method is analysing the coordinates of the Point and checks
         * what column of the connect four was clicked
         *
         * @param p point that was clicked by the user
         * @return id of the column
         */
        public int getColumnClicked(Point p) {
            int col = (int) (p.getX() / (w / COLS));
            return col;
        }

        @Override
        public void paintComponent(Graphics g) {
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

        /**
         * Player ONE is starting the game with Red color disks
         * we need to fill the matrix with values "R" or "B"
         * where R will correspond to Player ONE action
         * and B to Player TWO action
         * @param col the column that corresponds to user click
         * @return position of the row
         */
        public int playerDroppedDisc(int col) {
            // used to save the matrix row position of user click
            int row = 0;

            // get the color of the current move pf the player
            char playerColor;
            if (player == Player.ONE) {
                playerColor = 'R';
            } else {
                playerColor = 'B';
            }

            // set the player move on to player moves matrix
            boolean keepGoing = true;
            for (int i = 0; i < playersMoves.length && keepGoing; i++) {
                if (playersMoves[i][col] == (char) 0) {
                    // if the element at pos [i][col] is empty, initialise it with current user move
                    playersMoves[i][col] = playerColor;
                    keepGoing = false;
                    row = i;
                }
            }
            return row;
        }



        public boolean isConnected(int row, int col, char playerColor) {
            // "score" for current cell
            int score = 1;

            int top = scanDir(playerColor, row, col, -1, 0);
            int bottom = scanDir(playerColor, row, col, +1, 0);

            if (score + top + bottom >= 4) {
                return true;
            }

            int left = scanDir(playerColor, row, col, 0, -1);
            int right = scanDir(playerColor, row, col, 0, +1);

            if (score + left + right >= 4) {
                return true;
            }

            int topLeft = scanDir(playerColor, row, col, -1, -1);
            int bottomRight = scanDir(playerColor, row, col, +1, +1);

            if (score + topLeft + bottomRight >= 4) {
                return true;
            }

            int topRight = scanDir(playerColor, row, col, -1, +1);
            int bottomLeft = scanDir(playerColor, row, col, +1, -1);

            if (score + topRight + bottomLeft >= 4) {
                return true;
            }

            return false;
        }

        public int scanDir(char playerColor, int prevRow, int prevCol, int rowShift, int colShift) {
            int row = prevRow + rowShift;
            int col = prevCol + colShift;
            // if we are outside the boundaries, we don't get a score
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                return 0;
            }

            // if we "scored" the current cell, keep scanning in the same direction
            if (playersMoves[row][col] == playerColor) {
                return 1 + scanDir(playerColor, row, col, rowShift, colShift);
            } else {
                return 0;
            }
        }


        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

            gridClicked(e);
            Point p = e.getPoint();
            System.out.println("Drawing on point : " + p.getX() + ", " + p.getY());
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }




        public boolean checkDiagonal() {
            char centre = playersMoves[1][1];

            winner = false;

            if (playersMoves[0][0] == centre && playersMoves[2][2] == centre && centre != '\0')
                winner = true;

            if (playersMoves[2][0] == centre && playersMoves[0][2] == centre && centre != '\0')
                winner = true;

            return winner;
        }

        public boolean checkAccross() {
            boolean playerWon = false;

            // 4 consequent disks are needed to win
            int winCond = 4;

            for (int r = 0; r < ROWS; r++) {

                char curr = playersMoves[r][0];      // initialise current element, can be 'B' or 'R'
                int count = 1;                       // counting consequent discs
                for (int c = 1; c < COLS; c++) {
                    if (playersMoves[r][c] == curr && playersMoves[r][c] != '\0') {
                        count++;
                    } else {
                        curr = playersMoves[r][c];
                    }
                }

                // if consequent disks reached 5, we have a winner
                if (count == winCond) {
                    playerWon = true;
                    System.out.println("PLAYER WON!!!");
                }
            }

            return playerWon;
        }


        public boolean checkDown() {
            boolean playerWon = false;

            // 4 consequent disks are needed to win
            int winCond = 4;

            for (int c = 0; c < COLS; c++) {

                char curr = playersMoves[0][c];      // initialise current element, can be 'B' or 'R'
                int count = 1;                       // counting consequent discs
                for (int r = 1; r < ROWS; r++) {
                    if (playersMoves[r][c] == curr && playersMoves[r][c] != '\0') {
                        count++;
                    } else {
                        curr = playersMoves[r][c];
                    }
                }

                // if consequent disks reached 5, we have a winner
                if (count == winCond) {
                    playerWon = true;
                    System.out.println("PLAYER WON!!!");
                }
            }

            return playerWon;
        }

    }
}
