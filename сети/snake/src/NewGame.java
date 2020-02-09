import javax.swing.*;
import java.awt.*;

public class NewGame extends JFrame {
    public NewGame() {
        setTitle("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 470);
        setLocation(300, 150);
        //наполнение окна
        JPanel root = new JPanel();
        GameField gameField = new GameField();
        gameField.setPreferredSize(new Dimension(325, 340));
        root.add(gameField);
        add(root);
        setVisible(true);
    }
}
