package com.example.demo10;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.Random;

public class GameController {

    @FXML
    private Pane gameArea;
    @FXML
    private Button startButton;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timerLabel;

    private int score = 0;
    private boolean running = false;
    private final Random random = new Random();
    private Timeline spawnTargets;
    private Timeline countdown;
    private int timeLeft = 30; // секунд на игру

    public void startGame() {
        if (running) return;
        running = true;
        score = 0;
        timeLeft = 30;
        scoreLabel.setText("Score: 0");
        timerLabel.setText("Time: " + timeLeft);
        startButton.setDisable(true);
        gameArea.getChildren().clear();

        // таймер обратного отсчета
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                endGame();
            }
        }));
        countdown.setCycleCount(Timeline.INDEFINITE);
        countdown.play();

        // спавн целей каждые 1.2 секунды
        spawnTargets = new Timeline(new KeyFrame(Duration.seconds(1.2), e -> spawnTarget()));
        spawnTargets.setCycleCount(Timeline.INDEFINITE);
        spawnTargets.play();
    }

    private void spawnTarget() {
        if (!running) return;

        double radius = 15 + random.nextDouble() * 20;
        double x = random.nextDouble() * (gameArea.getWidth() - 2 * radius);
        double y = random.nextDouble() * (gameArea.getHeight() - 2 * radius);

        boolean isBad = random.nextDouble() < 0.25; // 25% целей — вредные
        Color color;

        if (isBad) {
            color = Color.RED; // вредная цель
        } else {
            // случайный цвет, кроме красного
            color = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
            if (color.equals(Color.RED)) {
                color = Color.BLUE;
            }
        }

        Circle target = new Circle(x + radius, y + radius, radius, color);

        target.setOnMouseClicked(e -> {
            if (!running) return;
            if (isBad) {
                score = Math.max(0, score - 2); // отнимаем очки
            } else {
                score++;
            }
            scoreLabel.setText("Score: " + score);
            gameArea.getChildren().remove(target);
        });

        gameArea.getChildren().add(target);

        // движение цели
        TranslateTransition move = new TranslateTransition(Duration.seconds(1.5), target);
        move.setByX(random.nextDouble() * 100 - 50);
        move.setByY(random.nextDouble() * 100 - 50);
        move.setAutoReverse(true);
        move.setCycleCount(2);
        move.play();

        // исчезновение через 2 секунды
        PauseTransition life = new PauseTransition(Duration.seconds(2));
        life.setOnFinished(e -> gameArea.getChildren().remove(target));
        life.play();
    }

    private void endGame() {
        running = false;
        if (spawnTargets != null) spawnTargets.stop();
        if (countdown != null) countdown.stop();
        gameArea.getChildren().clear();
        startButton.setDisable(false);
        timerLabel.setText("Game Over!");

    }
}