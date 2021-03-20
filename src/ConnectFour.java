import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class ConnectFour extends JFrame {

    private PlayArea playArea;

    enum Player {
        ONE, TWO
    }

    public ConnectFour() {
        this.setResizable(false);
        setSize(750, 750);
        setTitle("Connect 4");
    }

    public void init(int playerOneScore, int playerTwoScore) {
        playArea = new PlayArea(playerOneScore, playerTwoScore);
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

        // starting scores
        private int playerOneScore;
        private int playerTwoScore;

        // set of points that define where next discs will be dropped
        private Point[] points = new Point[COLS];

        public PlayArea( int playerOneScore, int playerTwoScore) {
            // this class will catch mouse clicks
            addMouseListener(this);

            // background of the play area
            setBackground(new Color(241, 250, 238));

            // set the score
            this.playerOneScore = playerOneScore;
            this.playerTwoScore = playerTwoScore;

            // initialise set of starting points for each of the playing columns
            w = 750;
            h = 642;

            // using trial and error method, this is best version for Mac
            //int y = h - (h / ROWS - 1);
            //int startingX = 2;
            //int offsetX = w / COLS;

            // using trial and error method, this is best version for Windows
            int y = h  - (h / ROWS / 2 - 10);
            int startingX = 1;
            int offsetX = w / COLS - 2;
            for (int i = 0; i < points.length; i++) {
                points[i] = new Point(startingX + (offsetX * i), y);
            }

        }

        /**
         * Drawing lines that form the grid of connect4 game
         * @param g Graphisc of the JPanel
         */
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

        /**
         * Method is handling click on the grid, calls draw disc on grid,
         * checks if game is not won yet
         * @param e click event
         */
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
                if (player == Player.ONE) {
                    playerOneScore++;
                } else {
                    playerTwoScore++;
                }

                int result = JOptionPane.showConfirmDialog(null,
                        "Player 1 score: " + playerOneScore + "\nPlayer 2 score: " + playerTwoScore + "\nWould you like to play again? ",
                        "Game Finished",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                // 0=yes, 1=no, 2=cancel
                if(result == JOptionPane.YES_OPTION){
                   this.setVisible(false);
                   dispose();
                    new ConnectFour().init(playerOneScore, playerTwoScore);
                }else if (result == JOptionPane.NO_OPTION){
                    System.exit(0);
                }

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

        /**
         * Method is painting a colored circle in the lowest possible point
         * of the column that was clicked by user.
         * If circles were drawn below, the new circle appears on top of it
         * @param col index of culumn that was clicked
         */
        public void paintDisk(int col) {
            Graphics2D g2d = (Graphics2D) this.getGraphics();

            // set color
            if (player == Player.ONE) {
                g2d.setPaint(new Color(230, 57, 70));
            } else {
                g2d.setPaint(new Color(29, 53, 87));
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
        }

        /**
         * Player ONE is starting the game with Red color disks
         * we need to fill the matrix with values "R" or "B"
         * where R will correspond to Player ONE action
         * and B to Player TWO action. The playersMoves matrix represents player
         * moves and helps to check win conditions.
         *
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


        /**
         * Method checks if recently dropped move connects 4 discs of the
         * same color in one of the directions: vertical, horizontal or diagonal
         * @param row coordinate of player move in rows
         * @param col coordinate of player move in columns
         * @param playerColor color of the disc that was dropped: 'B' blue or 'R' red
         * @return true if the move connects four discs of the same color, false of not
         */
        public boolean isConnected(int row, int col, char playerColor) {
            // "score" for current cell
            int score = 1;

            // vertical check
            int top = scanDir(playerColor, row, col, -1, 0);
            int bottom = scanDir(playerColor, row, col, +1, 0);

            if (score + top + bottom >= 4) {
                return true;
            }

            // horizontal check
            int left = scanDir(playerColor, row, col, 0, -1);
            int right = scanDir(playerColor, row, col, 0, +1);

            if (score + left + right >= 4) {
                return true;
            }

            // diagonal check from top left to bottom right \
            int topLeft = scanDir(playerColor, row, col, -1, -1);
            int bottomRight = scanDir(playerColor, row, col, +1, +1);

            if (score + topLeft + bottomRight >= 4) {
                return true;
            }

            // diagonal check from top rigtt to bottom left /
            int topRight = scanDir(playerColor, row, col, -1, +1);
            int bottomLeft = scanDir(playerColor, row, col, +1, -1);

            if (score + topRight + bottomLeft >= 4) {
                return true;
            }

            return false;
        }

        /**
         * Method is checking if discs with coordinates [prevRow][prevCol] and [rowShift][colShift]
         * connect with the same color
         * @param playerColor color that is checked 'B' blue or 'R' red
         * @param prevRow row coordinate
         * @param prevCol column coordinate
         * @param rowShift shift row coordinate
         * @param colShift shift column coordinate
         * @return number of connected discs of the same color
         */
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



        // First attempts to check across and down. These worked, but
        // I could not adapt them to cover all diagonal chances.
        public boolean checkAcross() {
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
