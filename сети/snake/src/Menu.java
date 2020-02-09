import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    public Menu(){
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 470);
        setLocation(300, 150);
        //наполнение окна
        JPanel root = new JPanel();
        JButton createPlayButton = new JButton("create game");
        createPlayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                NewGame newGame = new NewGame();
            }
        });
        root.add(createPlayButton);
        add(root);
        setVisible(true);
    }
    public static void main(String[] arg){
        Menu mainWindow = new Menu();
    }
}
