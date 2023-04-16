package ru.foxtris.ui.game;

import fox.FoxFontBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.ui.game.modal.AboutDialog;
import ru.foxtris.ui.game.second.CenterPanel;
import ru.foxtris.ui.game.second.DownPanel;
import ru.foxtris.ui.game.second.LeftPanel;
import ru.foxtris.ui.game.second.RightPanel;

import javax.imageio.ImageIO;
import javax.management.BadAttributeValueExpException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Data
public class GameFrame extends JFrame {
    private static ExecutorService tickPool;
    private static JPanel basePane;
    private static CenterPanel centerPanel;
    private static long deltaTime = 1000L;
    private static AboutDialog aboutDialog;
    private static GameFrame current;
    private final LeftPanel leftPanel;
    private final RightPanel rightPanel;
    private final DownPanel downPanel;
    private VolatileImage wallpaper;
    private long was;
    private float fontIncreaseMod = 1;
    private boolean isInitialized = false;
    private float FRAME_HEIGHT_ETALON;
    private float gameFrameWidth, panelsLeftAndRightWidth, gameFrameHeight;

    public GameFrame() {
        setName("gameFrame");
        current = this;

        preInitialization();
        if (wallpaper == null) {
            reloadTheme();
        }

        centerPanel = new CenterPanel(current);
        leftPanel = new LeftPanel(current);
        rightPanel = new RightPanel(current);
        downPanel = new DownPanel();

        basePane = new JPanel(new BorderLayout()) {
            {
                setIgnoreRepaint(true);

                add(leftPanel, BorderLayout.WEST);
                add(centerPanel, BorderLayout.CENTER);
                add(rightPanel, BorderLayout.EAST);
                add(downPanel, BorderLayout.SOUTH);
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (!Constants.getConfig().isUseBackImage()) {
                    getParent().repaint();
                    return;
                }
                if (wallpaper == null) {
                    reloadTheme();
                }
                g.drawImage(wallpaper, 0, 0, (int) gameFrameWidth, (int) gameFrameHeight, null);
            }
        };
        add(basePane);
        setJMenuBar(new FrameMenuBar(current));

        startGame();
        setVisible(true);
    }

    public static JPanel getBasePanel() {
        return basePane;
    }

    private static void nextLevel() {
        if (Constants.isGameOver() && !Constants.isVictory()) {
            return;
        }

        Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
        speedUp();
        Constants.resetVictory();
        Constants.setPaused(false);

        basePane.repaint(); // отрисовываем игру для отображения изменений..
        if (Constants.getConfig().isAutoChangeMelody()) {
            Constants.getMusicPlayer().playNext();
        }
        centerPanel.removeDownLine();
    }

    private static void restartGame() {
        log.warn("== RESTARTING ==");

        Constants.setGameIsActive(false);
        Constants.getInputAction().clearAll();
        current.dispose();
        if (Constants.getConfig().getGameThemeName() != null) {
            current.setVisible(true);
        } else {
            if (current.isVisible()) {
                current.exit();
            }
        }
    }

    public static void exitConfirm() {
        Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
        Constants.setPaused(true);

        Object[] choices = {"Выход", "Отмена"};
        Object defaultChoice = choices[0];

        int closeChoise = JOptionPane.showOptionDialog(
                current,
                "<html>Пауза<br>(нажми 'Отмена' для возврата) <hr> Выйти в меню?<br>(нажми 'Выход' для выхода)</html>",
                "Выбор за тобой:",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, choices, defaultChoice
        );

        if (closeChoise == 0) {
            log.debug("Dispose and back to the StartMenuFrame...");
            if (current != null && current.isVisible()) {
                current.exit();
            }
        } else {
            Constants.setPaused(false);
        }
    }

    private void exit() {
        log.debug("De-initialization...");
        Constants.setGameIsActive(false);

        Constants.getInputAction().clearAll();
        tickPool.shutdownNow();
        setVisible(false);
        //dispose();

        new StartMenuFrame();
    }

    public static void speedUp() {
        if (deltaTime > 100) {
            deltaTime -= 50;
        }
    }

    public void inputActionCharger() {
        final String aimName = current.getName();
        Constants.getInputAction().add(aimName, current);

        Constants.getInputAction().set(aimName, "arrowLeft", Constants.getConfig().getKeyLeft(), Constants.getConfig().getKeyLeftMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.shiftLeft();
            }
        });

        Constants.getInputAction().set(aimName, "arrowRight", Constants.getConfig().getKeyRight(), Constants.getConfig().getKeyRightMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.shiftRight();
            }
        });

        Constants.getInputAction().set(aimName, "arrowDown", Constants.getConfig().getKeyDown(), Constants.getConfig().getKeyDownMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.shiftDown();
                Constants.setSkipOneFrame(true);
            }
        });

        Constants.getInputAction().set(aimName, "arrowUp", Constants.getConfig().getKeyStuck(), Constants.getConfig().getKeyStuckMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.stuckToGround();
            }
        });

        Constants.getInputAction().set(aimName, "rotateZ", Constants.getConfig().getKeyRotate(), Constants.getConfig().getKeyRotateMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerPanel.onRotateFigure();
            }
        });

        Constants.getInputAction().set(aimName, "fullscreen", Constants.getConfig().getKeyFullscreen(), Constants.getConfig().getKeyFullscreenMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Constants.getConfig().setFullscreen(!Constants.getConfig().isFullscreen());
                setFullscreen(Constants.getConfig().isFullscreen());
            }
        });

        Constants.getInputAction().set(aimName, "pause", Constants.getConfig().getKeyPause(), Constants.getConfig().getKeyPauseMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Constants.setPaused(!Constants.isPaused());
            }
        });

        Constants.getInputAction().set(aimName, "console", Constants.getConfig().getKeyConsole(), Constants.getConfig().getKeyConsoleMod(), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//				if(!console.isVisible()) {console.setVisible(true); console.changeInputAreaText(null);}
            }
        });

        Constants.getInputAction().set(aimName, "altF4", KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitConfirm();
            }
        });

        Constants.getInputAction().set(aimName, "f1", KeyEvent.VK_F1, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                Constants.setPaused(true);

                try {
                    aboutDialog.setVisible(true);
                } catch (Exception e1) {
                    log.error("Exception here: {}", e1.getMessage());
                    e1.printStackTrace();
                }

                log.debug("Out of pause...");
                Constants.setPaused(false);
            }
        });

        Constants.getInputAction().set(aimName, "victoryN", KeyEvent.VK_N, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextLevel();
            }
        });

        Constants.getInputAction().set(aimName, "failNewH", KeyEvent.VK_H, 0, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Constants.isGameOver()) {
                    Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                    restartGame();
                }
            }
        });
    }

    private void setFullscreen(boolean isFullscreen) {
        log.debug("Change Fullscreen mode to " + isFullscreen);
        if (current.isVisible()) {
            current.setVisible(false);
            current.dispose();
        }
        setUndecorated(isFullscreen);
        downPanel.setVisible(!isFullscreen);

        if (isFullscreen) {
            gameFrameWidth = (float) Constants.getScreenDimension().getWidth();
            gameFrameHeight = (float) Constants.getScreenDimension().getHeight();
            Constants.setBrickDim((int) ((gameFrameHeight - 15f) / Constants.getFieldLinesCount()));
        } else {
            gameFrameWidth = FRAME_HEIGHT_ETALON;
            float f0 = (gameFrameWidth - 87f) / Constants.getFieldLinesCount();
            float f1 = (gameFrameWidth / 5f * 3f) / Constants.getFieldColumnCount();
            Constants.setBrickDim((int) (f0 / 2f + f1 / 2f) - 10);
            gameFrameHeight = Constants.getBrickDim() * (Constants.getFieldLinesCount() + 2) + 6f;
        }

        // final sets fonts and next figure view:
        fontIncreaseMod = gameFrameWidth / gameFrameHeight - 0.24f;
        panelsLeftAndRightWidth = (gameFrameWidth - (Constants.getFieldColumnCount() * Constants.getBrickDim())) / 2f;

        Constants.setGamePanelsSpacingUp(((gameFrameHeight - (Constants.getFieldLinesCount() * Constants.getBrickDim())) / 2f) + 48f);
        Constants.setGamePanelsSpacingLR(((gameFrameWidth - panelsLeftAndRightWidth * 2f - (Constants.getFieldColumnCount() * Constants.getBrickDim())) / 2f) + 3);
        log.debug("gamePanelsSpacingUp: " + Constants.getGamePanelsSpacingUp() + "; gamePanelsSpacingLR: " + Constants.getGamePanelsSpacingLR());

        // correction of sizes:
        int minWidthNeed = Constants.getBrickDim() * 21;
        if (gameFrameWidth < minWidthNeed) {
            gameFrameWidth = minWidthNeed;
        }

        int minHeightNeed = Constants.getBrickDim() * 11 + 30;
        if (gameFrameHeight < minHeightNeed) {
            gameFrameHeight = minHeightNeed;
        }

        // set size of NextBrickViewer:
        int nextBreakViewSize = Constants.getBrickDim() + 15;
        while (nextBreakViewSize * 3 + 15 >= panelsLeftAndRightWidth) {
            nextBreakViewSize -= 2;
        }
        // установка размера окошка следующей фигуры:
        Constants.setNextBrickFieldDimension(nextBreakViewSize);

        // set frame back to visible:
        setExtendedState(isFullscreen ? Frame.MAXIMIZED_BOTH : Frame.NORMAL);
        if (!isFullscreen) {
            setMinimumSize(new Dimension((int) gameFrameWidth + Constants.getBrickDim() / 3, (int) gameFrameHeight));
            setSize(new Dimension((int) gameFrameWidth + Constants.getBrickDim() / 3, (int) gameFrameHeight));
        }
        setLocationRelativeTo(null);
        setVisible(true);

        // reload back image dims:
        reloadWallpaper();

        leftPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));
        rightPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));

        // final:
        Constants.getConfig().setFullscreen(isFullscreen);
    }

    private void reloadThemeResource(String themeName) throws IOException {
        if (themeName == null) {
            log.error("reloadThemeResource(): Income themeName is NULL");
        }
        log.debug("Loading Theme '" + themeName + "'...");

        try {
            String preImagePath = Constants.IMAGE_THEME[1] + themeName;

            Constants.getCache().remove(Constants.IMAGE_THEME[0]);
            Constants.getCache().remove("proto");
            Constants.getCache().remove("NoneOneBrick");
            Constants.getCache().remove("GreenOneBrick");
            Constants.getCache().remove("OrangeOneBrick");
            Constants.getCache().remove("PurpleOneBrick");
            Constants.getCache().remove("YellowOneBrick");
            Constants.getCache().remove("BlueOneBrick");
            Constants.getCache().remove("RedOneBrick");
            Constants.getCache().remove("BlackOneBrick");

            Constants.getCache().addIfAbsent(Constants.IMAGE_THEME[0], ImageIO.read(new File(preImagePath + "/theme.png")));
            Constants.getCache().addIfAbsent("proto", ImageIO.read(new File(preImagePath + "/proto.png")));
            Constants.getCache().addIfAbsent("NoneOneBrick", ImageIO.read(new File(preImagePath + "/noneOne.png")));
            Constants.getCache().addIfAbsent("GreenOneBrick", ImageIO.read(new File(preImagePath + "/greenOne.png")));
            Constants.getCache().addIfAbsent("OrangeOneBrick", ImageIO.read(new File(preImagePath + "/orangeOne.png")));
            Constants.getCache().addIfAbsent("PurpleOneBrick", ImageIO.read(new File(preImagePath + "/purpleOne.png")));
            Constants.getCache().addIfAbsent("YellowOneBrick", ImageIO.read(new File(preImagePath + "/yellowOne.png")));
            Constants.getCache().addIfAbsent("BlueOneBrick", ImageIO.read(new File(preImagePath + "/blueOne.png")));
            Constants.getCache().addIfAbsent("RedOneBrick", ImageIO.read(new File(preImagePath + "/redOne.png")));
            Constants.getCache().addIfAbsent("BlackOneBrick", ImageIO.read(new File(preImagePath + "/blackOne.png")));

            Constants.getImageService().setProto(getCachedImage("proto")); // Constants.IMAGE_BUTTON_PROTO_NAME[0]
            Constants.getImageService().setNoneOneBrick(getCachedImage("NoneOneBrick"));
            Constants.getImageService().setGreenOneBrick(getCachedImage("GreenOneBrick"));
            Constants.getImageService().setOrangeOneBrick(getCachedImage("OrangeOneBrick"));
            Constants.getImageService().setPurpleOneBrick(getCachedImage("PurpleOneBrick"));
            Constants.getImageService().setYellowOneBrick(getCachedImage("YellowOneBrick"));
            Constants.getImageService().setBlueOneBrick(getCachedImage("BlueOneBrick"));
            Constants.getImageService().setRedOneBrick(getCachedImage("RedOneBrick"));
            Constants.getImageService().setBlackOneBrick(getCachedImage("BlackOneBrick"));

            Constants.getSoundPlayer().add(Constants.SOUND_TIP_NAME[0],      new File(Constants.SOUND_TIP_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_WIN_NAME[0],      new File(Constants.SOUND_WIN_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_ACHIEVE_NAME[0],  new File(Constants.SOUND_ACHIEVE_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_FULLINE_NAME[0],  new File(Constants.SOUND_FULLINE_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_LOSE_NAME[0],     new File(Constants.SOUND_LOSE_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_WARN_NAME[0],     new File(Constants.SOUND_WARN_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_SPAWN_NAME[0],    new File(Constants.SOUND_SPAWN_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_STUCK_NAME[0],    new File(Constants.SOUND_STUCK_NAME[1].replace("THEME", themeName)));
            Constants.getSoundPlayer().add(Constants.SOUND_ROUND_NAME[0],    new File(Constants.SOUND_ROUND_NAME[1].replace("THEME", themeName)));
        } catch (Exception e) {
            log.error("Load resources exception: {}", e.getMessage());
            throw e;
        } finally {
            if (current.isVisible()) {
                reloadWallpaper();
            }
        }
    }

    private BufferedImage getCachedImage(String imageLabel) {
        return (BufferedImage) Constants.getCache().get(imageLabel);
    }

    public void reloadWallpaper() {
        Dimension frameSize = getGameFrameSize();
        if (frameSize.width <= 0 || frameSize.height <= 0) {
            log.error("Dims wallpaper can`t be less than '1 px'");
            return;
        }

        try {
            log.debug("GameFrame: reloadWallpaper: Create the wallpaper...");
            wallpaper = Constants.getMon().getConfiguration().createCompatibleVolatileImage(Constants.getScreenDimension().width, Constants.getScreenDimension().height);
            BufferedImage imIc = (BufferedImage) Constants.getCache().get(Constants.IMAGE_THEME[0]);
            float imageWidth = imIc.getWidth(), imageHeight = imIc.getHeight();
            float realFrameHeigthMinus = Constants.getConfig().isFullscreen() ? 10f : 100f;
            float sideShift = (float) ((imageWidth - frameSize.getWidth()) / 6f);
            float heightShift = (float) ((imageHeight - frameSize.getHeight()) / 2f);
            Graphics2D g2D = (Graphics2D) wallpaper.getGraphics();
            g2D.setColor(Color.BLACK);
            g2D.fillRect(0, 0, wallpaper.getWidth(), wallpaper.getHeight());
            g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            if (imageWidth > imageHeight) {
                // ширина фона больше чем высота. Выравниваем ВЫСОТУ по окну игры:
                if (getWidth() <= imageWidth) {
                    g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(),
                            (int) sideShift, 0,
                            (int) (imageWidth - sideShift), (int) (imageHeight + realFrameHeigthMinus),
                            null);
                } else {
                    sideShift = -sideShift / 3f;
                    g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(),
                            (int) (sideShift * 2f), (int) sideShift,
                            (int) (imageWidth - sideShift * 2f), (int) (imageHeight + realFrameHeigthMinus - sideShift),
                            null);
                }
            } else {
                // высота фона больше, чем ширина. Выравниваем ШИРИНУ по окну игры:
                g2D.drawImage(imIc, 0, 0, wallpaper.getWidth(), wallpaper.getHeight(),
                        0, (int) heightShift,
                        (int) imageWidth, (int) (imageHeight - heightShift),
                        null);
            }

            g2D.dispose();
        } catch (Exception e) {
            log.error("A new one exception here, argh: {}", e.getMessage());
        }
    }

    // checkers and tuners:
    public void lifeUp() {
        Constants.getSoundPlayer().play(Constants.SOUND_ACHIEVE_NAME[0], 50);
        Constants.setBonusCount(0);
        Constants.getSoundPlayer().play(Constants.SOUND_ACHIEVE_NAME[0], 75);
        Constants.setLives(Constants.getLives() + 1);
        Constants.getSoundPlayer().play(Constants.SOUND_ACHIEVE_NAME[0], 100);
    }

    public void lifeLost() {
        Constants.setLives(Constants.getLives() - 1);
        if (Constants.getBalls() <= 50) {
            Constants.setBalls(0);
        } else {
            Constants.setBalls(Constants.getBalls() - 50);
        }
        try {
            centerPanel.reCreateGameFieldMassive();
        } catch (BadAttributeValueExpException e) {
            log.error("BadAttributeValueExpException: {}", e.getMessage());
        }
    }

    // getters and setters:
    public RenderingHints getRender() {
        RenderingHints d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
        d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
        return d2DRender;
    }

    public float getSpeed() {
        if (Constants.getConfig().isHardcoreMode()) {
            return 1000f / (deltaTime / 2f);
        } else if (Constants.getConfig().isLitecoreMode()) {
            return 1000f / (deltaTime * 2f);
        } else {
            return 1000f / deltaTime;
        }
    }

    public Dimension getGameFrameSize() {
        return current.getSize();
    }

    public String getKeyLabel(KeyLabel keyLabel) {
        return switch (keyLabel) {
            case LEFT ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyLeft()) + " '" + (Constants.getConfig().getKeyLeftMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyLeftMod()) + "'");
            case RIGHT ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyRight()) + " '" + (Constants.getConfig().getKeyRightMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyRightMod()) + "'");
            case DOWN ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyDown()) + " '" + (Constants.getConfig().getKeyDownMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyDownMod()) + "'");
            case ROTATE ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyRotate()) + " '" + (Constants.getConfig().getKeyRotateMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyRotateMod()) + "'");
            case STUCK ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyStuck()) + " '" + (Constants.getConfig().getKeyStuckMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyStuckMod()) + "'");
            case CONSOLE ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyConsole()) + " '" + (Constants.getConfig().getKeyConsoleMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyConsoleMod()) + "'");
            case PAUSE ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyPause()) + " '" + (Constants.getConfig().getKeyPauseMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyPauseMod()) + "'");
            case FULLSCREEN ->
                    "' " + KeyEvent.getKeyText(Constants.getConfig().getKeyFullscreen()) + " '" + (Constants.getConfig().getKeyFullscreenMod() == 0 ? "" : " + '" + InputEvent.getModifiersExText(Constants.getConfig().getKeyFullscreenMod()) + "'");
        };
    }

    private void preInitialization() {
        log.debug("Start pre-Initialization...");
        Constants.getDateFormat().setTimeZone(TimeZone.getTimeZone("+3"));
        Constants.getConfig().setGameThemeName(THEME.valueOf(Constants.getConfig().getGameThemeName()).name());
        FRAME_HEIGHT_ETALON = (float) Constants.getScreenDimension().getHeight();

        // calculate lines, columns, gameFieldMassive`s size:
        Constants.setFieldColumnCount(12);
        Constants.setFieldLinesCount(14);

        // levels stages set, lifes set, etc:
        Constants.setLives(3);

        Constants.setFont0(Constants.getFfb().setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 14f * fontIncreaseMod, false));
        Constants.setFont1(Constants.getFfb().setFoxFont(FoxFontBuilder.FONT.CAMBRIA, 14f * fontIncreaseMod, true));

        // prepare to building GUI:
        log.debug("Building the GameFrame...");
        setTitle(Constants.getName() + " " + Constants.getVersion());
        try {
            setIconImage((Image) Constants.getCache().get(Constants.IMAGE_GAME_ICO_NAME[0]));
        } catch (Exception e1) {/* IGNORE */}
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // charge inAc to gameGUIframe:
        inputActionCharger();

        isInitialized = true;
    }

    private void startGame() {
        setFullscreen(Constants.getConfig().isFullscreen());
        Constants.setGameIsActive(true);
        was = System.currentTimeMillis();

        tickPool = Executors.newSingleThreadExecutor();
        tickPool.execute(() -> {
            log.debug("Launch the tick-Pool...");
            int descret = Constants.getMon().getRefreshRate();

            while (Constants.isGameIsActive()) {
                try {
                    tick();

                    if (!Constants.isAnimationOn()) {
                        try {
                            if (Constants.getConfig().isLitecoreMode()) {
                                TimeUnit.MILLISECONDS.sleep(deltaTime * 2);
                            } else if (Constants.getConfig().isHardcoreMode()) {
                                TimeUnit.MILLISECONDS.sleep(deltaTime / 2);
                            } else if (Constants.isSpeedUp()) {
                                TimeUnit.MILLISECONDS.sleep(deltaTime / 10);
                            } else {
                                TimeUnit.MILLISECONDS.sleep(deltaTime);
                            }
                        } catch (Exception e) {/* IGNORE */}
                    } else {
                        if (descret <= 60) {
                            TimeUnit.MILLISECONDS.sleep(33);
                        } else if (descret <= 72) {
                            TimeUnit.MILLISECONDS.sleep(30);
                        } else {
                            TimeUnit.MILLISECONDS.sleep(24);
                        }
                    }
                } catch (InterruptedException e) {
                    log.error("Tick exception: {}", e.getMessage());
                }
            }
        });
        tickPool.shutdown();

        leftPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));
        rightPanel.setPreferredSize(new Dimension((int) panelsLeftAndRightWidth, 0));

        Constants.getMusicPlayer().play(Constants.getMusicPlayer().getLastTrack());
    }

    public void reloadTheme() {
        log.debug("Theme tuner start...");

        try {
            String themeName = Constants.getConfig().getGameThemeName();
            if (themeName.equals(THEME.TECHNO.name())) {
                themeName = ("techno");
            } else if (themeName.equals(THEME.GLASS.name())) {
                themeName = ("glass");
            } else if (themeName.equals(THEME.HOLO.name())) {
                themeName = ("holo");
            } else if (themeName.equals(THEME.OTIME.name())) {
                themeName = ("otime");
            } else if (themeName.equals(THEME.SIMPLE.name())) {
                themeName = ("simple");
            } else if (themeName.equals(THEME.ASPHALT.name())) {
                themeName = ("asphalt");
            }

            reloadThemeResource(themeName);
        } catch (Exception e) {
            log.error("ResourceManager report about: " + e.getLocalizedMessage());
        }

        Constants.getConfig().setGameThemeName(Constants.getConfig().getGameThemeName());
        log.debug("Theme tune has complete. Now its '" + Constants.getConfig().getGameThemeName() + "'.");

        if (isInitialized) {
            try {
                reloadWallpaper();
            } catch (Exception e) {
                log.error("Wallpaper exception: {}", e.getMessage());
            }
            if (Constants.getMusicPlayer().getLastTrack() != null) {
                Constants.getMusicPlayer().play(Constants.getMusicPlayer().getLastTrack());
            } else {
                Constants.getMusicPlayer().play(Constants.MUSIC_START_GAME);
            }
        }

//        setFullscreen(!Constants.getConfig().isFullscreen());
//        setFullscreen(Constants.getConfig().isFullscreen());
        // Constants.setPaused(false);
    }

    public void tick() {
        if (Constants.isPaused() || Constants.isAnimationOn()) {
            basePane.repaint();
            return;
        }

        if (Constants.isReadyToNextFigure()) {
            if (Constants.isGameOver()) {
                return;
            }
            centerPanel.createNewFigure();
        }

        if (Constants.isSkipOneFrame()) {
            Constants.setSkipOneFrame(false);
        } else {
            centerPanel.shiftDown();
        }

        repaint();
    }

    public enum THEME {TECHNO, GLASS, HOLO, OTIME, SIMPLE, ASPHALT}

    public enum KeyLabel {LEFT, RIGHT, DOWN, STUCK, ROTATE, PAUSE, CONSOLE, FULLSCREEN}
}
