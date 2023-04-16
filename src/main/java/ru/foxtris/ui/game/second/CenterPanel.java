package ru.foxtris.ui.game.second;

import fox.FoxRender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.ui.game.GameFrame;
import ru.foxtris.ui.game.records.Figure;
import ru.foxtris.ui.game.records.GameField;
import ru.foxtris.utils.MatrixUtil;
import ru.foxtris.utils.RandomUtil;

import javax.management.BadAttributeValueExpException;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class CenterPanel extends JPanel {
    private final GameFrame gameFrame;
    private final Map<String, Figure> figures = new HashMap<>(10);
    private GameField gameField;
    private int bombDestroyLineMarker = -1, fullLineCheck, startX, startY;
    private BufferedImage victoryLabelBuffer, gameoverLabelBuffer, pauseLabelBuffer, finalWinLabelBuffer;

    public CenterPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setOpaque(false);
        setIgnoreRepaint(true);

        try {
            reCreateGameFieldMassive();
        } catch (BadAttributeValueExpException e) {
            throw new RuntimeException(e);
        }

        buildFiguresMatrixes();
        prepareBaseImageBuffers();

        // изначальная установка первой фигуры:
        Constants.setNextFigureFuture(getFiguresMatrixByIndex(RandomUtil.getNextInt()));

        Constants.setSkipOneFrame(true);
        Constants.setReadyToNextFigure(true);
        Constants.setGameOver(false);

        Constants.setPaused(false);
        Constants.setAnimationOn(false);
    }

    public void reCreateGameFieldMassive() throws BadAttributeValueExpException {
        log.debug("Re-creating new gameFieldMassive...");
        if (Constants.getFieldLinesCount() == 0) {
            throw new BadAttributeValueExpException(Constants.getFieldLinesCount());
        }

        gameField = new GameField(new int[Constants.getFieldLinesCount()][Constants.getFieldColumnCount()][Constants.getFieldColumnCount()]);
        if (gameField.field().length == 0) {
            throw new BadAttributeValueExpException(Constants.getFieldLinesCount());
        }

        log.debug("Created the gameFieldMassive. Length: " + gameField.field().length);
    }

    private void buildFiguresMatrixes() {
        figures.put("Z", new Figure("Z", new int[][]{
                        {1, 1, 0},
                        {0, 1, 1},
                        {0, 0, 0}}));

        figures.put("O", new Figure("O", new int[][]{
                        {2, 2, 0},
                        {2, 2, 0},
                        {0, 0, 0}}));

        figures.put("L", new Figure("L", new int[][]{
                        {3, 3, 3},
                        {3, 0, 0},
                        {0, 0, 0}}));

        figures.put("nL", new Figure("nL", new int[][]{
                        {4, 4, 4},
                        {0, 0, 4},
                        {0, 0, 0}}));

        figures.put("nZ", new Figure("nZ", new int[][]{
                        {5, 0, 0},
                        {5, 5, 0},
                        {0, 5, 0}}));

        figures.put("I", new Figure("I", new int[][]{
                        {6, 6, 6},
                        {0, 0, 0},
                        {0, 0, 0}}));

        figures.put("Y", new Figure("Y", new int[][]{
                        {8, 8, 8},
                        {0, 8, 0},
                        {0, 0, 0}}));

        figures.put("B", new Figure("B", new int[][]{
                        {7, 0, 0},
                        {0, 0, 0},
                        {0, 0, 0}}));

        figures.put("D", new Figure("D", new int[][]{
                        {1, 1, 1},
                        {0, 1, 0},
                        {1, 1, 1}}));
    }

    private void prepareBaseImageBuffers() {
        try {
            victoryLabelBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_VICTORY_NAME[0]);
            gameoverLabelBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_GAMEOVER_NAME[0]);
            pauseLabelBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_PAUSE_NAME[0]);
            finalWinLabelBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_WIN_NAME[0]);
        } catch (Exception e) {
            log.error("Image read exception: {}", e.getMessage());
        }
    }


    public void stuckToGround() {
        if (Constants.isPaused() || Constants.isGameOver() || Constants.isShiftLock()) {
            return;
        }
        if (Constants.isReadyToNextFigure() || Constants.isAnimationOn()) {
            return;
        }
        if (Constants.getCurrentFigure().name().equals("B")) {
            return;
        }

        Constants.setShiftLock(true);
        while (checkForMovingAccept(Movements.DOWN)) {
            moveDown();
        }
        Constants.setShiftLock(false);
        onFigureLanding();
    }

    private void moveDown() {
        for (int column = 0; column < Constants.getFieldColumnCount(); column++) {
            for (int line = Constants.getFieldLinesCount() - 1; line >= 0; line--) {
                if (gameField.field()[line][column][1] != 0) {
                    gameField.field()[line + 1][column][1] = gameField.field()[line][column][1];
                    gameField.field()[line][column][1] = 0;
                }
            }
        }
        startY++;
    }

    private void mergeFields() {
        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                if (gameField.field()[i][j][1] != 0) {
                    gameField.field()[i][j][0] = gameField.field()[i][j][1];
                    gameField.field()[i][j][1] = 0;
                }
            }
        }
    }

    private void onFigureLanding() {
        Constants.getSoundPlayer().play(Constants.SOUND_STUCK_NAME[0]);

        try {
            mergeFields(); // сливаем падающую фигуру с игровым полем..
            checkLines(); //проверяем, нет ли какой полной линии..
			checkWin(); // проверка на победу...
        } catch (Exception e) {
            log.error("Exception here: {}", e.getMessage());
        }

        GameFrame.getBasePanel().repaint();
        Constants.setReadyToNextFigure(true);
        Constants.setSkipOneFrame(true);
    }

    public void onRotateFigure() {
        if (Constants.isShiftLock() || Constants.isPaused() || Constants.isGameOver()) {
            return;
        }
        if (Constants.getCurrentFigure().name().equals("B") || Constants.getCurrentFigure().name().equals("O")) {
            return;
        }

        rotation();

        GameFrame.getBasePanel().repaint(); // отрисовываем игру для отображения изменений..
    }

    private void rotation() {
        int minX = Constants.getFieldColumnCount(), minY = Constants.getFieldLinesCount();
        int[][] activePointsMassive = new int[3][3];
        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                if (gameField.field()[i][j][1] != 0) {
                    if (i < minX) {
                        minX = i;
                    }
                    if (j < minY) {
                        minY = j;
                    }
                }
            }
        }

        try {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    activePointsMassive[i][j] = gameField.field()[minX + i][minY + j][1];
                }
            }
        } catch (Exception readFigureException0) {
            try {
                log.debug("\nRead figure exception: wall to right");
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        assert activePointsMassive[i] != null;
                        activePointsMassive[i][j] = gameField.field()[minX + i][minY + (j - 1)][1];
                    }
                }
            } catch (Exception readFigureException1) {
                log.debug("\nRead figure exception: down wall here");
                return;
            }
        }

        // rotate virtual figure:
        activePointsMassive = MatrixUtil.rotate(activePointsMassive);

        // fix empty left column:
        if (activePointsMassive[0][0] == 0 && activePointsMassive[1][0] == 0 && activePointsMassive[2][0] == 0) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    try {
                        activePointsMassive[i][j] = activePointsMassive[i][j + 1];
                    } catch (Exception e) {
                        activePointsMassive[i][j] = 0;
                    }
                }
            }
        }

        try {
            if (minX + 2 >= Constants.getFieldLinesCount()) {
                return;
            }

            // check for other falled block existing:
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (gameField.field()[minX + i][minY + j][0] != 0) {
                        return;
                    }
                }
            }

            // final write to game matrix:
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    gameField.field()[minX + i][minY + j][1] = activePointsMassive[i][j];
                }
            }
        } catch (Exception writeFigureException) {
            try {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        gameField.field()[minX + i][minY + (j - 1)][1] = activePointsMassive[i][j];
                    }
                }
            } catch (Exception writeFigureExceptionYet) {
                return;
            }
        }

        Constants.getSoundPlayer().play(Constants.SOUND_ROUND_NAME[0]);
    }

    public void shiftLeft() {
        if (Constants.isShiftLock() || Constants.isPaused() || Constants.isGameOver()) {
            return;
        }
        Constants.setShiftLock(true);

        if (checkForMovingAccept(Movements.LEFT)) {
            for (int column = 0; column < Constants.getFieldColumnCount(); column++) {
                for (int line = Constants.getFieldLinesCount() - 1; line >= 0; line--) {
                    if (gameField.field()[line][column][1] != 0) {
                        gameField.field()[line][column - 1][1] = gameField.field()[line][column][1];
                        gameField.field()[line][column][1] = 0;
                    }
                }
            }
            startX--;
        }

        GameFrame.getBasePanel().repaint(); // отрисовываем игру для отображения изменений..
        Constants.setShiftLock(false);
    }

    public void shiftRight() {
        if (Constants.isShiftLock() || Constants.isPaused() || Constants.isGameOver()) {
            return;
        }
        Constants.setShiftLock(true);

        if (checkForMovingAccept(Movements.RIGHT)) {
            for (int column = Constants.getFieldColumnCount() - 1; column >= 0; column--) {
                for (int line = 0; line < Constants.getFieldLinesCount(); line++) {
                    if (gameField.field()[line][column][1] != 0) {
                        gameField.field()[line][column + 1][1] = gameField.field()[line][column][1];
                        gameField.field()[line][column][1] = 0;
                    }
                }
            }
            startX++;
        }

        GameFrame.getBasePanel().repaint(); // отрисовываем игру для отображения изменений..
        Constants.setShiftLock(false);
    }

    private boolean checkForMovingAccept(Movements m) {
        switch (m) {
            case DOWN:
                for (int column = 0; column < Constants.getFieldColumnCount(); column++) {
                    for (int line = Constants.getFieldLinesCount() - 1; line >= 0; line--) {
                        if (gameField.field()[line][column][1] != 0) {
                            if (line + 1 >= Constants.getFieldLinesCount()) {
                                return false;
                            }
                            if (gameField.field()[line + 1][column][0] != 0) {
                                return false;
                            }
                        }
                    }
                }
                break;
            case LEFT:
                for (int column = 0; column < Constants.getFieldColumnCount(); column++) {
                    for (int line = Constants.getFieldLinesCount() - 1; line >= 0; line--) {
                        if (gameField.field()[line][column][1] != 0) {
                            if (column - 1 < 0) {
                                return false;
                            }
                            try {
                                if (gameField.field()[line][column - 1][0] != 0) {
                                    return false;
                                }
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    }
                }
                break;
            case RIGHT:
                for (int column = Constants.getFieldColumnCount() - 1; column >= 0; column--) {
                    for (int line = 0; line < Constants.getFieldLinesCount(); line++) {
                        if (gameField.field()[line][column][1] != 0) {
                            if (column + 1 >= Constants.getFieldColumnCount()) {
                                return false;
                            }
                            try {
                                if (gameField.field()[line][column + 1][0] != 0) {
                                    return false;
                                }
                            } catch (Exception e) {
                                return false;
                            }
                        }
                    }
                }
                break;
            default:
                log.warn("Default switch in checkForMovingAccept...");
                return false;
        }

        return true;
    }

    private boolean isLineWasDestroyed;
    private void checkLines() {
        int line;
        int column;
        isLineWasDestroyed = false;
        ExecutorService aniPool = Executors.newFixedThreadPool(1);
        Runnable rn = () -> {
            try {
                while (!aniPool.awaitTermination(30, TimeUnit.MILLISECONDS)) {
                    GameFrame.getBasePanel().repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //bonus check:
            if (Constants.getDestroyedLinesPackSize() >= 3) {
                Constants.setDestroyedLinesPackSize(0);
                Constants.getSoundPlayer().play(Constants.SOUND_ACHIEVE_NAME[0]);
                Constants.setBalls(Constants.getBalls() + 10);
                Constants.setBonusAchieved(true);
                Constants.setPowerfullDamageBonus(true);

                Constants.setBonusCount(Constants.getBonusCount() + 1);
            }

            Constants.setAnimationOn(false);
            cleanFieldCheck();
            checkWin();
        };
        fullLineCheck = 0;

        if (bombDestroyLineMarker > -1) {
            final int marker = bombDestroyLineMarker;
            aniPool.execute(() -> {
                Constants.setAnimationOn(true);
                if (marker < 0) {
                    throw new RuntimeException("Var 'line' can`t be less 0");
                }
                destroyLine(marker);
                Constants.setDestroyedLinesPackSize(Constants.getDestroyedLinesPackSize() + 1);
            });
        } else {
            for (line = 0; line < Constants.getFieldLinesCount(); line++) {
                fullLineCheck = 0;
                for (column = 0; column < Constants.getFieldColumnCount(); column++) {
                    if (gameField.field()[line][0][0] == 0) {
                        break;
                    }
                    if (gameField.field()[line][column][0] != 0) {
                        fullLineCheck++;
                        if (fullLineCheck == Constants.getFieldColumnCount()) {
                            Constants.setAnimationOn(true);
                            final int marker = line;
                            aniPool.execute(() -> {
                                destroyLine(marker);
                                Constants.setDestroyedLinesPackSize(Constants.getDestroyedLinesPackSize() + 1);
                                Constants.setBalls(Constants.getBalls() + 5);
                            });
                            break;
                        }
                    }
                }

            }
        }
        aniPool.shutdown();
        new Thread(rn).start();

        Constants.setSkipOneFrame(true);
        bombDestroyLineMarker = -1;

        if (!isLineWasDestroyed) {
            Constants.setDestroyedLinesPackSize(0);
        }
    }

    private synchronized void destroyLine(int line) {
        isLineWasDestroyed = true;
        Constants.setCollectedLinesCounter(Constants.getCollectedLinesCounter() + 1);
        Constants.getSoundPlayer().play(Constants.SOUND_FULLINE_NAME[0]);

        for (int k = 0; k < Constants.getFieldColumnCount(); k++) {
            gameField.field()[line][k][0] = 6;
            GameFrame.getBasePanel().repaint();
            try {
                TimeUnit.MILLISECONDS.sleep(18);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        try {
            TimeUnit.MILLISECONDS.sleep(150);
        } catch (InterruptedException e) {
            // ignore
        }

        for (int k = 0; k < Constants.getFieldColumnCount(); k++) {
            gameField.field()[line][k][0] = 8;
            GameFrame.getBasePanel().repaint();
            try {
                TimeUnit.MILLISECONDS.sleep(14);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        for (int k = 0; k < Constants.getFieldColumnCount(); k++) {
            gameField.field()[line][k][0] = 0;
            GameFrame.getBasePanel().repaint();
            Thread.yield();

            for (int fallLine = line; fallLine > 1; fallLine--) {
                gameField.field()[fallLine][k][0] = gameField.field()[fallLine - 1][k][0];
                gameField.field()[fallLine - 1][k][0] = 0;
                GameFrame.getBasePanel().repaint();
                try {
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private void checkWin() {
        if (Constants.getBalls() >= Constants.getStages()[Constants.getStageCounter()]) {
            log.info("Is win check success");
            Constants.getSoundPlayer().play(Constants.SOUND_WIN_NAME[0]);
            Constants.setVictory(true);
            return;
        }

        if (Constants.getBonusCount() >= 5) {
            gameFrame.lifeUp();
        }
    }

    private void cleanFieldCheck() {
        //clean bonus check:
        Constants.setCleanFieldBonusFlag(true);
        for (int bonusLineChecker = 0; bonusLineChecker < Constants.getFieldLinesCount(); bonusLineChecker++) {
            for (int column = 0; column < Constants.getFieldColumnCount(); column++) {
                if (gameField.field()[bonusLineChecker][column][0] != 0) {
                    Constants.setCleanFieldBonusFlag(false);
                    break;
                }
            }
        }

        if (Constants.isCleanFieldBonusFlag()) {
            log.info("The bonus achieved");
            Constants.setBonusCount(Constants.getBonusCount() + 1);
            Constants.getSoundPlayer().play(Constants.SOUND_ACHIEVE_NAME[0]);
            Constants.setBalls(Constants.getBalls() + 25);
            Constants.setBonusAchieved(true);
            Constants.setTotalDestroyBonus(true);
        }
    }

    private Figure getFiguresMatrixByIndex(Integer index) {
        switch (index) {
            case 0:
                return figures.get("Z");
            case 1:
                return figures.get("O");
            case 2:
                return figures.get("L");
            case 3:
                return figures.get("nL");
            case 4:
                return figures.get("nZ");
            case 5:
                return figures.get("I");
            case 6:
                return figures.get("Y");
            case 7:
                if (Constants.getConfig().isSpecialBlocksEnabled()) {
                    if (RandomUtil.getNextInt(10) >= 5) {
                        return figures.get("B");
                    } else {
                        return getFiguresMatrixByIndex(RandomUtil.getNextInt(8));
                    }
                } else {
                    return getFiguresMatrixByIndex(RandomUtil.getNextInt(8));
                }
            default:
                return figures.get("D");
        }
    }

    public void createNewFigure() {
        if (gameField.field()[0][Constants.getFieldColumnCount() / 2][0] != 0 ||
                gameField.field()[0][Constants.getFieldColumnCount() / 2 - 1][0] != 0 ||
                gameField.field()[0][Constants.getFieldColumnCount() / 2 + 1][0] != 0) {
            if (Constants.getLives() == 0) {
                Constants.setGameOver(true);
            } else {
                gameFrame.lifeLost();
            }
            Constants.getSoundPlayer().play(Constants.SOUND_LOSE_NAME[0]);
        }

        // берем текущую фигуру из памяти следующей:
        Constants.setCurrentFigure(Constants.getNextFigureFuture());

        // сразу подбираем следующую фигуру:
        Constants.setNextFigureFuture(getFiguresMatrixByIndex(RandomUtil.getNextInt()));

        if (Constants.getCurrentFigure().name().equals("B")) {
            Constants.getSoundPlayer().play(Constants.SOUND_WARN_NAME[0]);
        } else {
            Constants.getSoundPlayer().play(Constants.SOUND_SPAWN_NAME[0]);
        }

        if (Constants.getFieldColumnCount() % 2 == 0) {
            for (int x = 0; x < Constants.getNextFigureFuture().matrix().length; x++) {
                for (int y = 0; y < Constants.getNextFigureFuture().matrix().length; y++) {
                    gameField.field()[x][Constants.getFieldColumnCount() / 2 - 1 + y][1] = Constants.getCurrentFigure().matrix()[x][y];
                }
            }

            startX = Constants.getFieldColumnCount() / 2 - 1;
            startY = 0;
        } else {
            for (int x = 0; x < Constants.getNextFigureFuture().matrix().length; x++) {
                for (int y = 0; y < Constants.getNextFigureFuture().matrix().length; y++) {
                    gameField.field()[x][Constants.getFieldColumnCount() / 2 + y][1] = Constants.getCurrentFigure().matrix()[x][y];
                }
            }

            startX = Constants.getFieldColumnCount() / 2;
            startY = 0;
        }

        Constants.setReadyToNextFigure(false);
    }

    public void removeDownLine() {
        bombDestroyLineMarker = Constants.getFieldLinesCount() - 1;
        checkLines();
    }

    public void shiftDown() {
        if (Constants.isShiftLock() || Constants.isPaused() || Constants.isGameOver() || Constants.isAnimationOn()) {
            return;
        }

        Constants.setShiftLock(true);
        Constants.setIsShifted(false);

        if (checkForMovingAccept(Movements.DOWN)) {
            if (Constants.getCurrentFigure().name().equals("B")) {
                Constants.getSoundPlayer().play(Constants.SOUND_TIP_NAME[0]);
            }
            moveDown();
            Constants.setIsShifted(true);
        } else {
            if (Constants.getCurrentFigure().name().equals("B")) {
                Constants.setCurrentFigure(null);
                bombDestroyLineMarker = startY;
                gameField.field()[startY][startX][1] = 0;
                if (Constants.getBalls() >= 5) {
                    Constants.setBalls(Constants.getBalls() - 5);
                }
            }
        }

        GameFrame.getBasePanel().repaint(); // отрисовываем игру для отображения изменений..

        Constants.setShiftLock(false);
        if (!Constants.isShifted()) {
            onFigureLanding();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2D = (Graphics2D) g;
        Constants.getRender().setRender(g2D, FoxRender.RENDER.MED);

        if (!Constants.isAnimationOn()) {
            if (Constants.isGameOver()) {
                Constants.setSpeedUp(true);
                drawGameover(g2D);
                g2D.dispose();
                return;
            } else {
                Constants.setSpeedUp(false);
            }

            if (Constants.isGameFinished()) {
                drawFinVictory(g2D);
                g2D.dispose();
                return;
            }

            if (Constants.isVictory()) {
                drawVictory(g2D);
                g2D.dispose();
                return;
            }

            if (Constants.isPaused()) {
                drawPauseLabel(g2D);
                g2D.dispose();
                return;
            }
        }

        try {
            backFieldDraw(g2D);
        } catch (Exception e1) {
            log.error("Draw exception 01: {}", e1.getMessage());
        }
        try {
            drawLandedFigures(g2D);
        } catch (Exception e1) {
            log.error("Draw exception 02: {}", e1.getMessage());
        }
        try {
            drawActiveFigure(g2D);
        } catch (Exception e1) {
            log.error("Draw exception 03: {}", e1.getMessage());
        }

        if (!Constants.isAnimationOn()) {
            drawBonus(g2D);
        }

        g2D.dispose();
    }

    private void drawPauseLabel(Graphics2D g2D) {
        g2D.drawImage(
                pauseLabelBuffer,
                getWidth() / 2 - pauseLabelBuffer.getWidth() / 2,
                getHeight() / 2 - pauseLabelBuffer.getHeight() / 2,
                this);
    }

    private void backFieldDraw(Graphics2D g2D) {
        if (Constants.getConfig().isUseBackImage()) {
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        }

        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                g2D.drawImage(
                        Constants.getImageService().getProto(),
                        (int) ((j * Constants.getBrickDim() * 1f) + Constants.getGamePanelsSpacingLR()),
                        (int) ((i * Constants.getBrickDim() * 1f) + (Constants.getGamePanelsSpacingUp() / Constants.getFieldLinesCount())),
                        Constants.getBrickDim(), Constants.getBrickDim(),
                        null);
            }
        }

        if (Constants.getConfig().isUseBackImage()) {
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        }
    }

    private void drawLandedFigures(Graphics2D g2D) {
        if (Constants.getConfig().getGameThemeName().equals(GameFrame.THEME.GLASS.name())) {
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
        }

        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                if (gameField.field()[i][j][0] != 0) {
                    g2D.drawImage(
                            Constants.getBrickByIndex(gameField.field()[i][j][0]),
                            (int) (j * Constants.getBrickDim() + Constants.getGamePanelsSpacingLR()),
                            (int) (i * Constants.getBrickDim() + (Constants.getGamePanelsSpacingUp() / Constants.getFieldLinesCount())),
                            Constants.getBrickDim(), Constants.getBrickDim(),
                            null
                    );
                }
            }
        }
    }

    private void drawActiveFigure(Graphics2D g2D) {
        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                if (gameField.field()[i][j][1] != 0) {
                    g2D.drawImage(
                            Constants.getBrickByIndex(gameField.field()[i][j][1]),
                            (int) (j * Constants.getBrickDim() + Constants.getGamePanelsSpacingLR()),
                            (int) (i * Constants.getBrickDim() + (Constants.getGamePanelsSpacingUp() / Constants.getFieldLinesCount())),
                            Constants.getBrickDim(), Constants.getBrickDim(),
                            null
                    );
                }
            }
        }

        if (Constants.getConfig().getGameThemeName().equals(GameFrame.THEME.GLASS.name())) {
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        }
    }

    private void drawGameover(Graphics2D g2D) {
        try {
            fillAreaRandomBricks(g2D);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        g2D.drawImage(
                gameoverLabelBuffer,
                getWidth() / 2 - gameoverLabelBuffer.getWidth() / 2,
                getHeight() / 2 - gameoverLabelBuffer.getHeight() / 2,
                null);

        Constants.setPaused(true);
    }

    private void drawFinVictory(Graphics2D g2D) {
        try {
            backFieldDraw(g2D);
            recolorAllToWhite(g2D);
        } catch (Exception e) {
            e.printStackTrace();
        }

        g2D.drawImage(
                finalWinLabelBuffer,
                getWidth() / 2 - finalWinLabelBuffer.getWidth() / 2,
                getHeight() / 2 - finalWinLabelBuffer.getHeight() / 2,
                null);

        Constants.setPaused(true);
    }

    private void drawVictory(Graphics2D g2D) {
        try {
            backFieldDraw(g2D);
            recolorAllToWhite(g2D);
        } catch (Exception e) {
            log.error("Draw exception: {}", e.getMessage());
        }

        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2D.drawImage(
                victoryLabelBuffer,
                getWidth() / 2 - victoryLabelBuffer.getWidth() / 2,
                getHeight() / 2 - victoryLabelBuffer.getHeight() / 2,
                null);

        Constants.setPaused(true);
    }

    private void drawBonus(Graphics2D g2D) {
        if (!Constants.isBonusAchieved()) {
            return;
        }
        Dimension frameSize = gameFrame.getGameFrameSize();
        Constants.setTipLifeTime(Constants.getTipLifeTime() - 1);

        if (Constants.getTipLifeTime() <= 0) {
            GameFrame.getBasePanel().repaint();
            resetBonusFlags();
        } else {
            if (Constants.isTotalDestroyBonus()) {
                g2D.setColor(Color.DARK_GRAY);
                g2D.setFont(Constants.getFont3());
                g2D.drawString(
                        "TOTAL DESTROY!",
                        frameSize.width / 6 - 2 + Constants.getTipLifeTime(),
                        frameSize.height / 3 + 2 + Constants.getTipLifeTime()
                );

                g2D.setColor(Color.RED);
                g2D.setFont(Constants.getFont3());
                g2D.drawString(
                        "TOTAL DESTROY!",
                        frameSize.width / 6 + Constants.getTipLifeTime(),
                        frameSize.height / 3 + Constants.getTipLifeTime()
                );
            }

            if (Constants.isPowerfullDamageBonus()) {
                g2D.setColor(Color.DARK_GRAY);
                g2D.setFont(Constants.getFont3());
                g2D.drawString("POWERFUL!!!", frameSize.width / 6 - 2 + Constants.getTipLifeTime(), frameSize.height / 2 + 2 + Constants.getTipLifeTime());

                g2D.setColor(Color.RED);
                g2D.setFont(Constants.getFont3());
                g2D.drawString("POWERFUL!!!", frameSize.width / 6 + Constants.getTipLifeTime(), frameSize.height / 2 + Constants.getTipLifeTime());
            }
        }
    }

    private void resetBonusFlags() {
        Constants.setTipLifeTime(7);
        Constants.setBonusAchieved(false);
        Constants.setTotalDestroyBonus(false);
        Constants.setPowerfullDamageBonus(false);
    }

    private void recolorAllToWhite(Graphics2D g2D) {
        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                if (gameField.field()[i][j][0] != 0) {
                    gameField.field()[i][j][0] = 8;
                    g2D.drawImage(
                            Constants.getBrickByIndex(gameField.field()[i][j][0]),
                            j * Constants.getBrickDim(), i * Constants.getBrickDim(),
                            Constants.getBrickDim(), Constants.getBrickDim(),
                            null
                    );
                }
            }
        }
    }

    private void fillAreaRandomBricks(Graphics2D g2D) {
        for (int i = 0; i < Constants.getFieldLinesCount(); i++) {
            for (int j = 0; j < Constants.getFieldColumnCount(); j++) {
                g2D.drawImage(
                        Constants.getBrickByIndex(RandomUtil.getNextInt(7)),
                        j * Constants.getBrickDim(), i * Constants.getBrickDim(),
                        Constants.getBrickDim(), Constants.getBrickDim(),
                        null
                );
            }
        }
    }

    private enum Movements {
        DOWN, LEFT, RIGHT
    }
}
