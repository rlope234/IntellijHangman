package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import Difficulty.DifficultyLevel;
//import javafx.animation.FadeTransition;
//import javafx.scene.shape.Line;
//import javafx.scene.shape.Circle;+
//import java.util.Random;

public class HangmanGame extends Application {
    private Stage mainStage;
    private DifficultyLevel difficultyLevel = new DifficultyLevel();
    private String secretWord;
    private char[] displayedWord;
    private int lives;
    private Label wordLabel;
    private Label livesLabel;
    private Label wrongGuessesLabel;
    private String wrongGuesses;
    private Canvas hangmanCanvas;
    private TextField guessField;
    private Button guessButton;
    private Color themeColor;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        showDifficultySelection(); // Start with difficulty selection
    }

    // first page when entering to select which difficulty
    private void showDifficultySelection() {
        // scene layout
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label label = new Label("Select Difficulty:");
        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");

        // depending on the button selected, moves to appropriate selection.
        easyButton.setOnAction(e -> startGame("easy"));
        mediumButton.setOnAction(e -> startGame("medium"));
        hardButton.setOnAction(e -> startGame("hard"));

        layout.getChildren().addAll(label, easyButton, mediumButton, hardButton);

        Scene scene = new Scene(layout, 300, 250);
        mainStage.setTitle("Hangman - Choose Difficulty");
        mainStage.setScene(scene);
        mainStage.show();
    }
// added
    // when player misses a word, the screen will shake
    private void shakeScreen() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), mainStage.getScene().getRoot());
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }

    private void startGame(String difficulty) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 10; -fx-alignment: center;");

        wordLabel = new Label();
        livesLabel = new Label();
        wrongGuessesLabel = new Label();

        guessField = new TextField();
        guessField.setPromptText("Enter a letter");

        guessButton = new Button("Guess");
        guessButton.setOnAction(e -> {
            handleGuess(guessField.getText());
            guessField.clear();
        });
        hangmanCanvas = new Canvas(200, 250);
        // === Theme based on difficulty ===
        switch (difficulty.toLowerCase()) {
            case "easy":
                themeColor = Color.FORESTGREEN;
                layout.setStyle("-fx-background-color: #d4f8d4; -fx-padding: 10; -fx-alignment: center;");
                break;
            case "medium":
                themeColor = Color.ORANGE;
                layout.setStyle("-fx-background-color: #f3eb95; -fx-padding: 10; -fx-alignment: center;");
                break;
            case "hard":
                themeColor = Color.DARKRED;
                layout.setStyle("-fx-background-color: #ffd6d6; -fx-padding: 10; -fx-alignment: center;");
                break;
        }
        // Set label colors
        wordLabel.setTextFill(themeColor);
        livesLabel.setTextFill(themeColor);
        wrongGuessesLabel.setTextFill(themeColor);

        layout.getChildren().addAll(wordLabel, guessField, guessButton, livesLabel, wrongGuessesLabel, hangmanCanvas);

        Scene scene = new Scene(layout, 300, 450);
        mainStage.setScene(scene);

        startNewGame(difficulty);
    }

    private void startNewGame(String difficulty) {
        // getting words
        switch (difficulty.toLowerCase()) {
            case "easy":
                secretWord = difficultyLevel.getEasyWord().toUpperCase();
                break;
            case "medium":
                secretWord = difficultyLevel.getMediumWord().toUpperCase();
                break;
            case "hard":
                secretWord = difficultyLevel.getHardWord().toUpperCase();
                break;
            default:
                secretWord = "UNKNOWN";
                break;
        }

        displayedWord = new char[secretWord.length()];
        for (int i = 0; i < displayedWord.length; i++) {
            displayedWord[i] = '_';
        }

        lives = 6;
        wrongGuesses = "";

        wordLabel.setText(new String(displayedWord));
        livesLabel.setText("Lives: " + lives);
        wrongGuessesLabel.setText("Wrong guesses: " + wrongGuesses);

        drawHangman();
        guessButton.setDisable(false);
        guessField.setDisable(false);
    }

    private void handleGuess(String input) {
        if (input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return; // Ignore invalid input
        }

        char guessedLetter = Character.toUpperCase(input.charAt(0));
        boolean found = false;

        // if right
        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) == guessedLetter) {
                displayedWord[i] = guessedLetter;
                found = true;
            }
        }

        // if wrong
        if (!found) {
            if (!wrongGuesses.contains(String.valueOf(guessedLetter))) {
                lives--;
                wrongGuesses += guessedLetter + " ";
                // SHAKE effect on wrong guess
                shakeScreen();
            }
        }

        wordLabel.setText(new String(displayedWord));
        livesLabel.setText("Lives: " + lives);
        wrongGuessesLabel.setText("Wrong guesses: " + wrongGuesses);
        drawHangman();

        if (new String(displayedWord).equals(secretWord)) {
            showEndDialog("You won!");
        } else if (lives <= 0) {
            showEndDialog("You lost! Word was: " + secretWord);
        }
    }

    private void drawHangman() {
        GraphicsContext gc = hangmanCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, hangmanCanvas.getWidth(), hangmanCanvas.getHeight());

        gc.setStroke(themeColor);

        // Draw base
        gc.strokeLine(20, 230, 180, 230); // ground
        gc.strokeLine(50, 230, 50, 50);   // pole
        gc.strokeLine(50, 50, 120, 50);   // top
        gc.strokeLine(120, 50, 120, 70);  // rope

        int parts = 6 - lives;

        if (parts >= 1) gc.strokeOval(100, 70, 40, 40); // head
        if (parts >= 2) gc.strokeLine(120, 110, 120, 170); // body
        if (parts >= 3) gc.strokeLine(120, 120, 100, 150); // left arm
        if (parts >= 4) gc.strokeLine(120, 120, 140, 150); // right arm
        if (parts >= 5) gc.strokeLine(120, 170, 100, 200); // left leg
        if (parts >= 6) gc.strokeLine(120, 170, 140, 200); // right leg



    }







    private void showEndDialog(String message) {
        guessButton.setDisable(true);
        guessField.setDisable(true);


        if (lives <= 0) {
            RotateTransition swing = new RotateTransition(Duration.millis(1000), hangmanCanvas);
            swing.setByAngle(30);
            swing.setCycleCount(6);
            swing.setAutoReverse(true);
            swing.play();
        }




        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(message);
        alert.setContentText("Do you want to play again?");

        ButtonType playAgain = new ButtonType("Play Again");
        ButtonType exit = new ButtonType("Exit");

        alert.getButtonTypes().setAll(playAgain, exit);

        alert.showAndWait().ifPresent(response -> {
            if (response == playAgain) {
                showDifficultySelection(); // Go back to difficulty selection
            } else {
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
