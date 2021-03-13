import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;

public class TicTacToe extends JFrame {

    private ArrayList<Player> players = new ArrayList<>();

    private PlayArea playArea =  new PlayArea();

    enum Player{
        ONE, TWO
    }

    public TicTacToe(){
        setSize(750,750);
        setTitle("Tic Tac Toe");



    }

    public void init()
    {
        add(playArea);
        this.setVisible(true);
    }

    class PlayArea extends JPanel implements MouseListener {

        private char [][] values = new char[3][3];

        private int w, h;

        private Player player = Player.ONE;

        private boolean winner = false;

        public PlayArea()
        {

            addMouseListener(this);
            setBackground(Color.gray);
        }

        public void drawGrid(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            int offset = 2;
            g2d.setStroke(new BasicStroke(offset * 2));

            //Horizontal lines
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

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);



            int offsetAcross = w/values[0].length;
            int offsetDown = h/values.length;

            Font f = new Font("TimesRoman", Font.BOLD, offsetAcross);

            FontMetrics metrics = g2d.getFontMetrics(f);

            g2d.setFont(f);

            for(int i = 0; i < values.length; i++)
            {
                for (int j = 0; j < values[i].length; j++)
                {
                    if(values[i][j] == 'X')
                    {
                        g2d.setColor(Color.RED);
                    }
                    else
                    {
                        g2d.setColor(Color.BLUE);
                    }

                    int x = (j*offsetAcross) + ((offsetAcross - metrics.stringWidth("X"))/2);
                    int y = (i*offsetDown) +  ((offsetDown - metrics.getHeight()) / 2) + metrics.getAscent();

                    //g2d.drawString(""+values[i][j], (j *offsetAcross) + 2,(i*offsetDown) + offsetDown - 2);

                    g2d.drawString(""+values[i][j], x,y);
                }
            }

        }

        public void clickedGridValue(MouseEvent e)
        {
            Point p = e.getPoint();

            int horizontalIndex = values[0].length -1;
            int verticalIndex = values.length - 1;

            for(int i = 0; i < values[0].length -1; i++)
            {
                if (p.getX() >=  (w/values.length)*i && p.getX() < (w/values.length)*(i+1))
                {
                    horizontalIndex = i;
                    break;
                }
            }

            for(int i = 0; i < values.length -1; i++)
            {
                if (p.getY() >=  (h/values.length)*i && p.getY() < (h/values.length)*(i+1))
                {
                    verticalIndex = i;
                    break;
                }
            }

            if(player == Player.ONE)
            {
                values[verticalIndex][horizontalIndex] = 'X';
                repaint();

                if(checkWin())
                {
                    System.out.println("Player one has won");
                    showMessageDialog(null, "Player One has won");
                    System.exit(0);
                }

                player = Player.TWO;
            }
            else
            {
                values[verticalIndex][horizontalIndex] = 'O';
                repaint();

                if(checkWin())
                {
                    System.out.println("Player two has won");
                    showMessageDialog(null, "Player Two has won");
                    System.exit(0);
                }
                player = Player.ONE;
            }

            /*for (char [] row : values)
            {
                for (char c : row)
                {
                    System.out.print(c + " ");
                }
                System.out.println();
            }*/


        }

        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            w = this.getWidth();
            h = this.getHeight();

            drawGrid(g);

            drawValues(g);

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
            clickedGridValue(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
