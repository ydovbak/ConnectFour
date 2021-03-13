import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {

    static class MainMenu extends JFrame implements ActionListener
    {
        private JButton play = new JButton("Play"), exit = new JButton("Exit");

        public MainMenu()
        {
            setLayout(new GridLayout(2,1));
            setSize(400, 400);
            setTitle("TicTacToe Menu");

            play.addActionListener(this);
            exit.addActionListener(this);

            add(play);
            add(exit);

            this.setVisible(true);

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == play) {
                new ConnectFour().init();
            }
            else
            {
                System.exit(0);
            }

        }
    }

    public static void main(String[] args) {
        new MainMenu();
    }
}