package sample;
//gui by using https://stackoverflow.com/questions/32916311/how-to-display-a-10-by-10square-matrix-with-javafx

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Controller extends Application {

    @FXML
    public TextArea output;
    ArrayList<ArrayList<Circle>> mat;
    Board board;
    Stage stage;
    @FXML
    TextField input;
    @FXML
    Button button;
    @FXML
    GridPane grid;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("CHECKERS");
        stage.setScene(scene);

        stage.show();
    }

    public void initialize() throws InterruptedException {
        board = new Board(this);
        setup(grid);
        grid.setGridLinesVisible(true);
        button.setOnAction(new EventHandler() {
            @Override
            public void handle(Event event) {
                Controller.this.board.humanMove(input.getText());
            }
        });
        startGame();
    }


    private void startGame() {
        Controller.this.board.start();
    }

    private void setup(GridPane root) throws InterruptedException {
        mat = new ArrayList<ArrayList<Circle>>(Board.DIMEN);
        for (int i = 0; i < Board.DIMEN; ++i) {
            ArrayList<Circle> t = new ArrayList<>(Board.DIMEN);
            for (int j = 0; j < Board.DIMEN; ++j) {

                Circle circle = new Circle(44, Color.rgb(255, 255, 255));
                if (board.mat.get(i).get(j).id == 1)
                    circle.setFill(Color.rgb(255, 0, 0));
                if (board.mat.get(i).get(j).id == 2)
                    circle.setFill(Color.rgb(0, 0, 255));


                t.add(circle);

                Text text = new Text(i + " , " + j);
                text.setBoundsType(TextBoundsType.VISUAL);
                StackPane stack = new StackPane();
                stack.getChildren().addAll(circle, text);

                GridPane.setRowIndex(stack, i);
                GridPane.setColumnIndex(stack, j);
                root.getChildren().add(stack);
            }
            mat.add(t);
        }

    }


    void moveIt(int a1, int b1, int a2, int b2, boolean flag) {

        Color c = (Color) mat.get(a1).get(b1).getFill();
        if (flag) {
            mat.get(a2).get(b2).setFill(Color.rgb((int) c.getRed() * 255, 255, (int) c.getBlue() * 255));
        } else {
            mat.get(a2).get(b2).setFill(c);
        }

        mat.get(a1).get(b1).setFill(Color.rgb(255, 255, 255));
    }

    void removeIt(int a, int b) {
        mat.get(a).get(b).setFill(Color.rgb(255, 255, 255));
    }
}
