import Join.JoinGame;
import Master.NewGame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;

public class Menu extends JFrame {
    public Menu() {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 470);
        setLocation(300, 150);

        //наполнение окна
        JPanel root = new JPanel();
        //создание кнопки create
        JButton createPlayButton = new JButton("create game");
        addActionListenerCreate(createPlayButton);
        root.add(createPlayButton);
        //создание кнопки join
        JButton joinPlayButton = new JButton("join in game");
        addActionListenerJoin(joinPlayButton);
        root.add(joinPlayButton);

        add(root);
        setVisible(true);
    }

    //ToDo: создает реакцию на нажитие create
    public void addActionListenerCreate(JButton createPlayButton) {
        createPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new NewGame();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    //ToDo: создает реакцию на нажитие join
    public void addActionListenerJoin(JButton joinPlayButton) {
        joinPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new JoinGame();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] arg) {
        Menu mainWindow = new Menu();
    }
}
