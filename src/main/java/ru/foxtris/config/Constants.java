package ru.foxtris.config;

import fox.FoxFontBuilder;
import fox.FoxRender;
import fox.VideoMonitor;
import fox.images.FoxSpritesCombiner;
import fox.player.FoxPlayer;
import fox.utils.InputAction;
import fox.utils.MediaCache;
import lombok.Data;
import ru.foxtris.dto.UserConfigDTO;
import ru.foxtris.service.GameConfigService;
import ru.foxtris.service.ImageService;
import ru.foxtris.ui.game.records.Figure;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

@Data
public final class Constants {
    private static final String name = "Foxtris-2023";
    private static final String version = "0.0.1";
    private static final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
    private static final ImageService imageService = new ImageService();
    private static final GameConfigService configService = new GameConfigService();
    private static final Path savesDirectory = Path.of("./save");
    private static final FoxFontBuilder ffb = new FoxFontBuilder();
    private static final MediaCache cache = MediaCache.getInstance();
    private static final FoxRender render = new FoxRender();
    private static final VideoMonitor mon = new VideoMonitor();
    private static final InputAction inputAction = new InputAction();
    private static final FoxPlayer soundPlayer = new FoxPlayer("soundPlayer");
    private static final FoxPlayer musicPlayer = new FoxPlayer("musicPlayer");
    private static final FoxSpritesCombiner spritesCombiner = new FoxSpritesCombiner();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final Font font2 = ffb.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 18, true);
    private static final Font font3 = ffb.setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 26, true);
    private static final Font downInfoPaneFont = ffb.setFoxFont(FoxFontBuilder.FONT.CONSOLAS, 16 + 1f, true);
    private static final Font aboutDialogFont = ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 24, true);
    private static final Font aboutDialogFontB = ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 16, false);

    public static final String DEFAULT_SAVE_FILE_NAME = "/default.save";
    public static final String IMAGE_EXTENSION = ""; // .png
    public static final String AUDIO_EXTENSION = ".wav"; // .png

    // SOUNDS LOCAL:
    public static final String[] SOUND_LAUNCH_NAME = {"launch", "./resources/sounds/launch".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_CLICK_NAME = {"click", "./resources/sounds/click".concat(AUDIO_EXTENSION)};

    // SOUNDS GLOBAL:
    public static final String[] SOUND_TIP_NAME = {"tip", "./resources/sounds/THEME/tip".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_WIN_NAME = {"win", "./resources/sounds/THEME/win".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_ACHIEVE_NAME = {"achieve", "./resources/sounds/THEME/achieve".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_FULLINE_NAME = {"fullLine", "./resources/sounds/THEME/fullLine".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_LOSE_NAME = {"lose", "./resources/sounds/THEME/lose".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_WARN_NAME = {"warn", "./resources/sounds/THEME/warn".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_SPAWN_NAME = {"spawn", "./resources/sounds/THEME/spawn".concat(AUDIO_EXTENSION)};
    // public static final String[] SOUND_FINAL_NAME = {"final", "./resources/sounds/THEME/final".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_STUCK_NAME = {"stuck", "./resources/sounds/THEME/stuck".concat(AUDIO_EXTENSION)};
    public static final String[] SOUND_ROUND_NAME = {"round", "./resources/sounds/THEME/round".concat(AUDIO_EXTENSION)};

    // MUSIC:
    public static final String MUSIC_START_MENU = "Mu0".concat(AUDIO_EXTENSION);
    public static final String MUSIC_START_GAME = "Mu1".concat(AUDIO_EXTENSION);
    // IMAGE:
    public static final String[] IMAGE_LOGO_NAME = {"logo", "/pictures/logo0".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_HARDCORE_NAME = {"hardcore", "/pictures/icons/hardcore".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_HARDCORE_OFF_NAME = {"hardcore_off", "/pictures/icons/hardcore_off".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_SPEC_NAME = {"spec", "/pictures/icons/spec".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_SPEC_OFF_NAME = {"spec_off", "/pictures/icons/spec_off".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_TIPS_NAME = {"tips", "/pictures/icons/tips".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_TIPS_OFF_NAME = {"tips_off", "/pictures/icons/tips_off".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_LIFE_NAME = {"life", "/pictures/icons/life".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BONUS_NAME = {"bonus", "/pictures/icons/bonus".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_AUTO_MUSIC_NAME = {"autoMusic", "/pictures/icons/autoMusic".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_AUTO_MUSIC_OFF_NAME = {"autoMusic_off", "/pictures/icons/autoMusic_off".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_LITECORE_NAME = {"litecore", "/pictures/icons/litecore".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_LITECORE_OFF_NAME = {"litecore_off", "/pictures/icons/litecore_off".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_GAME_ICO_NAME = {"gameIcon", "/pictures/gameIcon".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BACKABOUT_NAME = {"backAbout", "/pictures/about/000".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_STARSABOUT_NAME = {"starsAbout", "/pictures/about/001".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BABOUT_NAME = {"bAbout", "/pictures/about/002".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BUTTON_PROTO_NAME = {"buttonProto", "/pictures/buttonProto".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BUTTON_PROTO_OVER_NAME = {"buttonProtoOver", "/pictures/buttonProtoOver".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BUTTON_PROTO_PRESS_NAME = {"buttonProtoPress", "/pictures/buttonProtoPress".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_MAIN_BKG_NAME = {"mainBackground", "/pictures/mainBackground".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_VICTORY_NAME = {"victory", "/pictures/victory".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_GAMEOVER_NAME = {"gameover", "/pictures/gameover".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_PAUSE_NAME = {"pause", "/pictures/pause".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_WIN_NAME = {"finalWin", "/pictures/final".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_SWITCH_OFF_NAME = {"switchOff", "/pictures/switchOff".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_SWITCH_ON_NAME = {"switchOn", "/pictures/switchOn".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_STAGE_NAME = {"stage", "/pictures/stage".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_LOGOFOX_NAME = {"logoFoxList", "/pictures/sprites/logoFoxList".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_MBSL_NAME = {"MBSL", "/pictures/sprites/MBSL".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_BUTTON_UNIVERSAL_NAME = {"unibutton", "/pictures/sprites/unibutton".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_NUMBERS_NAME = {"numbers", "/pictures/sprites/numbers".concat(IMAGE_EXTENSION)};
    public static final String[] IMAGE_THEME = {"theme", "./resources/pictures/themes/"};
    public static Font font0 = ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 21, false);
    public static Font font1 = ffb.setFoxFont(FoxFontBuilder.FONT.ARIAL_NARROW, 20, true);

    private static UserConfigDTO config;

    private static Figure nextFigureFuture;
    private static Figure currentFigure;
    private static int[] stages = new int[]{
            90, 100, 120, 150, 190,
            240, 300, 370, 450, 540,
            640, 750, 870, 1000, 1150,
            1300, 1450, 1600, 2000, 3000,
            4500, 6000, 9999};

    private static boolean paused = false;
    private static boolean speedUp = false;
    private static boolean shifted = false;
    private static boolean victory = false;
    private static boolean gameOver = false;
    private static boolean shiftLock = false;
    private static boolean gameActive = false;
    private static boolean animationOn = true;
    private static boolean gameFinished = false;
    private static boolean skipOneFrame = false;
    private static boolean bonusAchieved = false;
    private static boolean readyToNextFigure = true;
    private static boolean totalDestroyBonus = false;
    private static boolean powerfullDamageBonus = false;
    private static boolean cleanFieldBonusFlag = false;

    private static int brickDim;
    private static int stageCounter;
    private static int nextBrickFieldDimension;
    private static int balls = 0;
    private static int collectedLinesCounter = 0;
    private static int destroyedLinesPackSize = 0;
    private static int bonusCount = 0;
    private static int columnCount = 0;
    private static int rowCount = 0;
    private static int tipLifeTime = 7;
    private static int lives;

    private static float gamePanelsSpacingUp;
    private static float gamePanelsSpacingLR;

    private Constants() {
    }

    public static FoxPlayer getSoundPlayer() {
        return soundPlayer;
    }

    public static UserConfigDTO getConfig() {
        return config;
    }

    public static void setConfig(UserConfigDTO config) {
        Constants.config = config;
    }

    public static int getBalls() {
        return balls;
    }

    public static void setBalls(int balls) {
        Constants.balls = balls;
    }

    public static int getCollectedLinesCounter() {
        return collectedLinesCounter;
    }

    public static void setCollectedLinesCounter(int i) {
        collectedLinesCounter = i;
    }

    public static int getDestroyedLinesPackSize() {
        return destroyedLinesPackSize;
    }

    public static void setDestroyedLinesPackSize(int i) {
        destroyedLinesPackSize = i;
    }

    public static int getBonusCount() {
        return bonusCount;
    }

    public static void setBonusCount(int i) {
        bonusCount = i;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean b) {
        paused = b;
    }

    public static boolean isGameOver() {
        return gameOver;
    }

    public static void setGameOver(boolean gameOver) {
        Constants.gameOver = gameOver;
    }

    public static boolean isVictory() {
        return victory;
    }

    public static void setVictory(final boolean victory) {
        Constants.victory = victory;
    }

    public static boolean isShiftLock() {
        return shiftLock;
    }

    public static void setShiftLock(final boolean b) {
        shiftLock = b;
    }

    public static int getFieldColumnCount() {
        return columnCount;
    }

    public static void setFieldColumnCount(final int i) {
        columnCount = i;
    }

    public static int getFieldLinesCount() {
        return rowCount;
    }

    public static void setFieldLinesCount(final int i) {
        rowCount = i;
    }

    public static void resetVictory() {
        if (stageCounter < stages.length - 1) {
            stageCounter++;
            victory = false;
            balls = 0;
        } else {
            // Constants.getSoundPlayer().play(Constants.SOUND_FINAL_NAME[0]);
            gameFinished = true;
        }
    }

    public static boolean isAnimationOn() {
        return animationOn;
    }

    public static void setAnimationOn(boolean animationOn) {
        Constants.animationOn = animationOn;
    }

    public static boolean isSkipOneFrame() {
        return skipOneFrame;
    }

    public static void setSkipOneFrame(boolean skipOneFrame) {
        Constants.skipOneFrame = skipOneFrame;
    }

    public static boolean isReadyToNextFigure() {
        return readyToNextFigure;
    }

    public static void setReadyToNextFigure(boolean readyToNextFigure) {
        Constants.readyToNextFigure = readyToNextFigure;
    }

    public static int getBrickDim() {
        return brickDim;
    }

    public static void setBrickDim(int brickDim) {
        Constants.brickDim = brickDim;
    }

    public static int getNextBrickFieldDimension() {
        return nextBrickFieldDimension;
    }

    public static void setNextBrickFieldDimension(int nextBrickFieldDimension) {
        Constants.nextBrickFieldDimension = nextBrickFieldDimension;
    }

    public static float getGamePanelsSpacingUp() {
        return gamePanelsSpacingUp;
    }

    public static void setGamePanelsSpacingUp(float gamePanelsSpacingUp) {
        Constants.gamePanelsSpacingUp = gamePanelsSpacingUp;
    }

    public static float getGamePanelsSpacingLR() {
        return gamePanelsSpacingLR;
    }

    public static void setGamePanelsSpacingLR(float gamePanelsSpacingLR) {
        Constants.gamePanelsSpacingLR = gamePanelsSpacingLR;
    }

    public static MediaCache getCache() {
        return cache;
    }

    public static FoxFontBuilder getFfb() {
        return ffb;
    }

    public static Font getFont0() {
        return font0;
    }

    public static void setFont0(Font font0) {
        Constants.font0 = font0;
    }

    public static Font getFont1() {
        return font1;
    }

    public static void setFont1(Font font1) {
        Constants.font1 = font1;
    }

    public static boolean isGameIsActive() {
        return gameActive;
    }

    public static void setGameIsActive(final boolean b) {
        gameActive = b;
    }

    public static Font getFont2() {
        return font2;
    }

    public static Font getFont3() {
        return font3;
    }

    public static InputAction getInputAction() {
        return inputAction;
    }

    public static FoxPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static FoxRender getRender() {
        return render;
    }

    public static SimpleDateFormat getDateFormat() {
        return dateFormat;
    }

    public static String getName() {
        return name;
    }

    public static String getVersion() {
        return version;
    }

    public static VideoMonitor getMon() {
        return mon;
    }

    public static FoxSpritesCombiner getSpritesCombiner() {
        return spritesCombiner;
    }

    public static Font getDownInfoPaneFont() {
        return downInfoPaneFont;
    }

    public static Path getSavesDirectory() {
        return savesDirectory;
    }

    public static void setIsShifted(final boolean b) {
        shifted = b;
    }

    public static boolean isShifted() {
        return shifted;
    }

    public static void setShifted(final boolean shifted) {
        Constants.shifted = shifted;
    }

    public static Dimension getScreenDimension() {
        return screenDimension;
    }

    public static boolean isSpeedUp() {
        return speedUp;
    }

    public static void setSpeedUp(final boolean b) {
        speedUp = b;
    }

    public static int getLives() {
        return lives;
    }

    public static void setLives(final int i) {
        lives = i;
    }

    public static ImageService getImageService() {
        return imageService;
    }

    public static int[] getStages() {
        return stages;
    }

    public static void setStages(final int[] stages) {
        Constants.stages = stages;
    }

    public static boolean isGameActive() {
        return gameActive;
    }

    public static void setGameActive(final boolean gameActive) {
        Constants.gameActive = gameActive;
    }

    public static boolean isGameFinished() {
        return gameFinished;
    }

    public static void setGameFinished(final boolean gameFinished) {
        Constants.gameFinished = gameFinished;
    }

    public static int getStageCounter() {
        return stageCounter;
    }

    public static void setStageCounter(final int stageCounter) {
        Constants.stageCounter = stageCounter;
    }

    public static void setColumnCount(final int columnCount) {
        Constants.columnCount = columnCount;
    }

    public static int getRowCount() {
        return rowCount;
    }

    public static void setRowCount(final int rowCount) {
        Constants.rowCount = rowCount;
    }

    public static int getTipLifeTime() {
        return tipLifeTime;
    }

    public static void setTipLifeTime(final int tipLifeTime) {
        Constants.tipLifeTime = tipLifeTime;
    }

    public static boolean isBonusAchieved() {
        return bonusAchieved;
    }

    public static void setBonusAchieved(final boolean b) {
        bonusAchieved = b;
    }

    public static boolean isTotalDestroyBonus() {
        return totalDestroyBonus;
    }

    public static void setTotalDestroyBonus(boolean b) {
        totalDestroyBonus = b;
    }

    public static boolean isPowerfullDamageBonus() {
        return powerfullDamageBonus;
    }

    public static void setPowerfullDamageBonus(boolean b) {
        powerfullDamageBonus = b;
    }

    public static boolean isCleanFieldBonusFlag() {
        return cleanFieldBonusFlag;
    }

    public static void setCleanFieldBonusFlag(boolean b) {
        cleanFieldBonusFlag = b;
    }

    public static Figure getNextFigureFuture() {
        return nextFigureFuture;
    }

    public static void setNextFigureFuture(Figure figuresMatrixByIndex) {
        nextFigureFuture = figuresMatrixByIndex;
    }

    public static BufferedImage getBrickByIndex(Integer integer) {
        return switch (integer) {
            case 1 -> imageService.getGreenOneBrick();
            case 2 -> imageService.getOrangeOneBrick();
            case 3 -> imageService.getPurpleOneBrick();
            case 4 -> imageService.getYellowOneBrick();
            case 5 -> imageService.getBlueOneBrick();
            case 6 -> imageService.getRedOneBrick();
            case 7 -> imageService.getBlackOneBrick();
            default -> imageService.getNoneOneBrick();
        };
    }

    public static Figure getCurrentFigure() {
        return currentFigure;
    }

    public static void setCurrentFigure(Figure current) {
        currentFigure = current;
    }

    private static boolean exitButOver, exitButPress, musButOver, musButPress, optButOver, optButPress, aboButOver, aboButPress;

    public static void setOptButPress(boolean b) {
        optButPress = b;
    }

    public static boolean isMusButOver() {
        return musButOver;
    }

    public static boolean isOptButOver() {
        return optButOver;
    }

    public static boolean isAboButOver() {
        return aboButOver;
    }

    public static boolean isExitButOver() {
        return exitButOver;
    }

    public static boolean isMusButPress() {
        return musButPress;
    }

    public static boolean isOptButPress() {
        return optButPress;
    }

    public static boolean isAboButPress() {
        return aboButPress;
    }

    public static boolean isExitButPress() {
        return exitButPress;
    }

    public static void setMusButPress(boolean b) {
        musButPress = b;
    }

    public static void setAboButPress(boolean b) {
        aboButPress = b;
    }

    public static void setExitButPress(boolean b) {
        exitButPress = b;
    }

    public static void setMusButOver(boolean b) {
        musButOver = b;
    }

    public static void setExitButOver(boolean exitButOver) {
        Constants.exitButOver = exitButOver;
    }

    public static void setOptButOver(boolean optButOver) {
        Constants.optButOver = optButOver;
    }

    public static void setAboButOver(boolean aboButOver) {
        Constants.aboButOver = aboButOver;
    }
}
