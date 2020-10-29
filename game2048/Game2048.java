package com.javarush.games.game2048;

import com.javarush.engine.cell.*;

public class Game2048 extends Game {

    private static final int SIDE = 4;
    private boolean isGameStopped = false;
    private int[][] gameField = new int[SIDE][SIDE];
    private int score = 0;

    private void createGame(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                gameField[i][j] = 0;
            }
        }
        createNewNumber();
        createNewNumber();
    }
    private boolean compressRow(int[] row){
        boolean shift = false;
        for (int i = 0; i < row.length; i++) {
            for (int j = i + 1; j < row.length; j++) {
                if (row[i] == 0 && row[j] != 0){
                    shift = true;
                    int temp = row[i];
                    row[i] = row[j];
                    row[j] = temp;
                }
            }
        }
        return shift;
    }

    private boolean mergeRow(int[] row){
        boolean merge = false;
        for (int i = 0; i < row.length - 1; i++) {
            if (row[i] == row[i+1] && row[i] != 0){
                score += row[i] * 2;
                setScore(score);
                row[i] *=2;
                row[i+1] = 0;
                merge = true;
            }
        }
        return merge;
    }
    private void drawScene(){
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                setCellColoredNumber(i,j,gameField[j][i]);
            }
        }
    }


    private void createNewNumber(){
        if (getMaxTileValue() >= 2048){
            win();
        }
//        Генерация координат для x,y
        int x = getRandomNumber(SIDE);
        int y = getRandomNumber(SIDE);
//       Повторная генерация, если полученные координаты указывают на не нулевой элемент
        while(gameField[y][x] != 0){
             x = getRandomNumber(SIDE);
             y = getRandomNumber(SIDE);
        }
//        Присвоить либо 4 с 10% шансом (Если chance == 9), либо 2 с 90% шансом
        int chance = getRandomNumber(10);
        int twoOrFour = (chance == 9) ? 4 : 2;
//        Присвоить полученное число ячейке матрицы
        gameField[y][x] = twoOrFour;
    }

    private Color getColorByValue(int value){
        switch (value){
            case 2: return Color.GREEN;
            case 4: return Color.ALICEBLUE;
            case 8: return Color.ANTIQUEWHITE;
            case 16: return Color.AQUA;
            case 32: return Color.AQUAMARINE;
            case 64: return Color.AZURE;
            case 128: return Color.BEIGE;
            case 256: return Color.BISQUE;
            case 512: return Color.BLUEVIOLET;
            case 1024: return Color.BLANCHEDALMOND;
            case 2048: return Color.BLUE;
            default: return Color.RED;
        }
    }

    private void setCellColoredNumber(int x, int y, int value){
        Color color = getColorByValue(value);
        if (value == 0) setCellValueEx(x,y, color, "");
        else setCellValueEx(x,y, color, Integer.toString(value));

    }

    @Override
    public void initialize(){
        setScreenSize(SIDE,SIDE);
        createGame();
        drawScene();
    }

    @Override
    public void onKeyPress(Key key){
        if (isGameStopped){
            switch (key){
                case SPACE: isGameStopped = false;
                createGame();
                drawScene();
                score = 0;
                setScore(score);
                break;
            }
        }

//       Если возможности походить нет, Game Over
        if (!canUserMove()){
            gameOver();
            return;
        }
        if (!isGameStopped) {
            switch (key) {
                case LEFT:
                    moveLeft();
                    drawScene();
                    break;
                case RIGHT:
                    moveRight();
                    drawScene();
                    break;
                case DOWN:
                    moveDown();
                    drawScene();
                    break;
                case UP:
                    moveUp();
                    drawScene();
                    break;
            }
        }
    }

    private void moveLeft(){
        boolean flag1 = false, flag2 = false, flag3 = false;
        int count = 0;
        for (int i = 0; i < SIDE; i++) {
            flag1 = compressRow(gameField[i]);
            flag2 = mergeRow(gameField[i]);
            flag3 = compressRow(gameField[i]);
//            Если хоть одно действие вернуло true, прибавить 1 к счетчику действий.
            if (flag1 || flag2 || flag3){
                count++;
                flag1 = false;
                flag2 = false;
                flag3 = false;
            }
        }
//        Если хоть один сдвиг был, ход считается совершенным и создается новое число
        if (count > 0){
            createNewNumber();
        }

    }
    private void moveRight(){
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
    }
    private void moveUp(){
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
        moveLeft();
        rotateClockwise();
    }
    private void moveDown(){
        rotateClockwise();
        moveLeft();
        rotateClockwise();
        rotateClockwise();
        rotateClockwise();
    }

    private void rotateClockwise(){
        int[][] copyArr = new int[SIDE][SIDE];
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField[i].length; j++) {
                copyArr[i][j] = gameField[(gameField[i].length -1) - j][i];
            }
        }
        gameField = copyArr;
    }

// Поиск максимального числа на поле
    private int getMaxTileValue(){
        int maxValue = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (maxValue < gameField[i][j]){
                    maxValue = gameField[i][j];
                }
            }
        }
        return maxValue;
    }

    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU WIN", Color.GREEN, 24);
    }

    private boolean canUserMove(){
        int countZero = 0;
        int countMerge = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == 0){
                    countZero++;
                }
            }
        }
// Проверка, возможен ли горизонтальное обьединение одинаковых ячеек
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE - 1; j++) {
                if (gameField[i][j] == gameField[i][j + 1]){
                    countMerge++;
                }
            }
        }
//        Проверка, возможно ли ветрикальное объединение одинаковых ячеек
        for (int i = 0; i < SIDE - 1; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (gameField[i][j] == gameField[i + 1][j]){
                    countMerge++;
                }
            }
        }
//        Если количество пустых ячеек (то есть с нулем) > 0, то возвращаем true
        if (countZero > 0){
            return true;
//         Если количество возможных соединений > 0, возвращаем true
        } else if (countMerge > 0){
            return true;
        } else return false;
    }

    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "GAME OVER", Color.RED, 24);
    }

    }

