package ru.foxtris.ui.game.second;

import fox.FoxRender;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.service.GameConfigService;
import ru.foxtris.ui.game.GameFrame;
import ru.foxtris.ui.game.OptionsDialog;
import ru.foxtris.ui.game.modal.AboutDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

@Slf4j
public class LeftPanel extends JPanel implements MouseListener {
    private final GameConfigService configService = new GameConfigService();
    private final Color panelColor = new Color(0.3f, 0.3f, 0.3f, 0.85f);
    private final GameFrame gameFrame;
    private final JPanel leftNextFigurePane;
    private final JPanel leftButtonsPane;
    private BufferedImage hardcoreBufferIco, hardcoreBufferIcoOff, specialBufferIco, specialBufferIcoOff, nextBufferIco;
    private BufferedImage nextBufferIcoOff, lightModeIco, lightModeIcoOff, autoMusicChangeIco, autoMusicChangeIcoOff;
    private BufferedImage buttonBufferIm, buttonOverBufferIm, buttonPressBufferIm, leftGrayBase, stage;
    private BufferedImage[] sp;
    private JButton exitButton, autorsButton, optionsButton, musicButton;

    public LeftPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());
        setIgnoreRepaint(true);
        setOpaque(false);

        initializeLeftPanel();

        leftNextFigurePane = new JPanel(new BorderLayout()) {
            {
                setIgnoreRepaint(true);

                add(new JPanel(new BorderLayout()) {
                    {
                        setIgnoreRepaint(true);
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2D = (Graphics2D) g;
                        drawGrayBack(this, g2D);

                        // draw left next figure:
                        drawLeftNextFigureArea(g2D);

                        g2D.dispose();
                    }

                    private void drawLeftNextFigureArea(Graphics2D g2D) {
                        int toRightShift, toDownShift;
                        int modX = (int) (getWidth() / 2D - Constants.getNextBrickFieldDimension() * 1.5f), modY = (int) (getHeight() / 2D - (Constants.getNextBrickFieldDimension() * 1.5f));

                        for (int nextCellsDrawX = 0; nextCellsDrawX < 3; nextCellsDrawX++) {
                            for (int nextCellsDrawY = 0; nextCellsDrawY < 3; nextCellsDrawY++) {
                                toRightShift = (Constants.getNextBrickFieldDimension() * nextCellsDrawX) + modX;
                                toDownShift = (Constants.getNextBrickFieldDimension() * nextCellsDrawY) + modY;

                                g2D.setColor(Color.GRAY);
                                g2D.drawRect(toRightShift, toDownShift, Constants.getNextBrickFieldDimension(), Constants.getNextBrickFieldDimension());

                                g2D.setColor(Color.BLACK);
                                g2D.fillRect(toRightShift + 1, toDownShift + 1, Constants.getNextBrickFieldDimension() - 2, Constants.getNextBrickFieldDimension() - 2);

                                if (Constants.getConfig().isNextFigureShow() && Constants.getNextFigureFuture() != null) {
                                    if (Constants.getNextFigureFuture().matrix()[nextCellsDrawY][nextCellsDrawX] != 0) {
                                        try {
                                            g2D.drawImage(
                                                    Constants.getBrickByIndex(Constants.getNextFigureFuture().matrix()[nextCellsDrawY][nextCellsDrawX]),
                                                    toRightShift, toDownShift,
                                                    Constants.getNextBrickFieldDimension(), Constants.getNextBrickFieldDimension(),
                                                    null
                                            );
                                        } catch (Exception e) {
                                            log.error("Exception here: {}", e.getMessage());
                                        }
                                    }
                                } else {
                                    try {
                                        g2D.drawImage(
                                                Constants.getBrickByIndex(0),
                                                toRightShift, toDownShift,
                                                Constants.getNextBrickFieldDimension(), Constants.getNextBrickFieldDimension(),
                                                null);
                                    } catch (Exception e) {
                                        log.error("Exception here: {}", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                });
            }
        };

        JPanel leftInformPane = new JPanel(new BorderLayout()) {
            {
                setIgnoreRepaint(true);
            }

            @Override
            public void paintComponent(Graphics g) {
                int numberSize = 78;
                int modX = (int) (getWidth() / 2f - (numberSize / 2f * gameFrame.getFontIncreaseMod()));
                int modY = (int) (numberSize * gameFrame.getFontIncreaseMod() / 1.5f);

                Graphics2D g2D = (Graphics2D) g;
                Constants.getRender().setRender(g2D, FoxRender.RENDER.LOW);
                drawGrayBack(this, g2D);

                g2D.drawImage(stage,
                        (int) (getWidth() / 2f - 32f), (int) (18f * gameFrame.getFontIncreaseMod()),
                        64, 27,
                        null);

                g2D.drawImage(sp[Constants.getStageCounter() + 1],
                        modX, modY,
                        (int) (numberSize * gameFrame.getFontIncreaseMod()), (int) (numberSize * gameFrame.getFontIncreaseMod()),
                        null);

                drawLeftIndicatorPictures(g2D);

                g2D.dispose();
            }

            private void drawLeftIndicatorPictures(Graphics2D g2D) {
                float picSize = getWidth() / 6f,
                        spacing = picSize / 7f,
                        middleLine = getWidth() / 2f - picSize / 2f;

                g2D.setColor(new Color(0.0f, 0.0f, 0.0f, 0.5f));
                g2D.fillRoundRect(
                        10, (int) (getHeight() * 0.78f),
                        getWidth() - 20, (int) (picSize + picSize / 3f),
                        27, 27);
                g2D.setColor(Color.DARK_GRAY);
                g2D.drawRoundRect(
                        10, (int) (getHeight() * 0.78f),
                        getWidth() - 20, (int) (picSize + picSize / 3f),
                        27, 27);


                g2D.drawImage(Constants.getConfig().isSpecialBlocksEnabled() ? specialBufferIco : specialBufferIcoOff,
                        (int) (picSize / 2f - spacing * 2f), (int) (getHeight() * 0.8f),
                        (int) (picSize), (int) (picSize), null);

                g2D.drawImage(Constants.getConfig().isNextFigureShow() ? nextBufferIco : nextBufferIcoOff,
                        (int) (middleLine - picSize - spacing), (int) (getHeight() * 0.8f),
                        (int) (picSize), (int) (picSize), null);

                g2D.drawImage(Constants.getConfig().isHardcoreMode() ? hardcoreBufferIco : hardcoreBufferIcoOff, (int) (middleLine), (int) (getHeight() * 0.8f), (int) (picSize), (int) (picSize), null);

                g2D.drawImage(Constants.getConfig().isLitecoreMode() ? lightModeIco : lightModeIcoOff,
                        (int) (middleLine + picSize + spacing), (int) (getHeight() * 0.8f),
                        (int) (picSize), (int) (picSize), null);

                g2D.drawImage(Constants.getConfig().isAutoChangeMelody() ? autoMusicChangeIco : autoMusicChangeIcoOff,
                        (int) ((getWidth() - picSize) - picSize / 2f + spacing * 2f), (int) (getHeight() * 0.8f),
                        (int) (picSize), (int) (picSize), null);
            }
        };

        leftButtonsPane = new JPanel(new GridLayout(4, 0, 2, 2)) {
            {
                setOpaque(false);
                setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                setIgnoreRepaint(true);

                musicButton = new JButton() {
                    {
                        setName("mus");
                        setOpaque(false);
                        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                        setToolTipText("Следующая мелодия");
                        setFocusPainted(false);
                        setFocusable(false);
                        addMouseListener(LeftPanel.this);
                        addActionListener(e -> {
                            Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                            Constants.getMusicPlayer().playNext();
                        });
                    }
                };

                optionsButton = new JButton() {
                    {
                        setName("opt");
                        setOpaque(false);
                        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                        setToolTipText("Опции и создатели игры");
                        setFocusPainted(false);
                        setFocusable(false);
                        addMouseListener(LeftPanel.this);
                        addActionListener(e -> {
                            Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                            Constants.setOptButPress(false);
                            Constants.setPaused(true);
                            new OptionsDialog(gameFrame);
                            gameFrame.inputActionCharger();
                            gameFrame.reloadTheme();
                            log.debug("Out of pause...");
                            Constants.setPaused(false);
                            configService.saveAll();
                        });
                    }
                };

                autorsButton = new JButton() {
                    {
                        setName("aut");
                        setOpaque(false);
                        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                        setToolTipText("Информация об игре");
                        setFocusPainted(false);
                        setFocusable(false);
                        addMouseListener(LeftPanel.this);
                        addActionListener(e -> {
                            Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                            Constants.setPaused(true);

                            try {
                                new AboutDialog(gameFrame).setVisible(true);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            log.debug("Out of pause...");
                            Constants.setPaused(false);
                        });
                    }
                };

                exitButton = new JButton() {
                    {
                        setName("exi");
                        setOpaque(false);
                        setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
                        setToolTipText("<html>Выход в меню. <br>Альтернатива клавише Esc</html>");
                        setFocusPainted(false);
                        setFocusable(false);
                        addMouseListener(LeftPanel.this);
                        addActionListener(e -> {
                            Constants.setExitButOver(false);
                            GameFrame.exitConfirm();
                        });
                    }
                };

                add(musicButton);
                add(optionsButton);
                add(autorsButton);
                add(exitButton);
            }

            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;
                drawGrayBack(this, g2D);

                final int x = 7;
                drawButton(g2D, musicButton, Constants.isMusButOver(), Constants.isMusButPress(), "Музыка >>", x, 9);
                drawButton(g2D, optionsButton, Constants.isOptButOver(), Constants.isOptButPress(), "Настройки", x, 7 + musicButton.getHeight());
                drawButton(g2D, autorsButton, Constants.isAboButOver(), Constants.isAboButPress(), "Об игре", x, 5 + optionsButton.getHeight() * 2);
                drawButton(g2D, exitButton, Constants.isExitButOver(), Constants.isExitButPress(), "Выход", x, 3 + autorsButton.getHeight() * 3);

                g2D.dispose();
            }
        };

        add(leftNextFigurePane, BorderLayout.NORTH);
        add(leftInformPane, BorderLayout.CENTER);
        add(leftButtonsPane, BorderLayout.SOUTH);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        revalidate();
        repaint();

        leftNextFigurePane.setPreferredSize(new Dimension(Constants.getNextBrickFieldDimension() * 4, Constants.getNextBrickFieldDimension() * 4));
        leftButtonsPane.setPreferredSize(new Dimension(0, (int) (gameFrame.getGameFrameSize().getHeight() / 6f)));

        prepareBaseImageBuffers();
    }

    private void initializeLeftPanel() {
        try {
            sp = Constants.getSpritesCombiner().getSprites(
                    "numbers",
                    (BufferedImage) Constants.getCache().get(Constants.IMAGE_NUMBERS_NAME[0]),
                    1, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }

        grayRectangleReDraw();
        prepareBaseImageBuffers();
    }

    private void prepareBaseImageBuffers() {
        try {
            stage = getFromCache(Constants.IMAGE_STAGE_NAME[0]);
            buttonBufferIm = getFromCache(Constants.IMAGE_BUTTON_PROTO_NAME[0]);
            buttonOverBufferIm = getFromCache(Constants.IMAGE_BUTTON_PROTO_OVER_NAME[0]);
            buttonPressBufferIm = getFromCache(Constants.IMAGE_BUTTON_PROTO_PRESS_NAME[0]);

            hardcoreBufferIco = getFromCache(Constants.IMAGE_HARDCORE_NAME[0]);
            hardcoreBufferIcoOff = getFromCache(Constants.IMAGE_HARDCORE_OFF_NAME[0]);

            specialBufferIco = getFromCache(Constants.IMAGE_SPEC_NAME[0]);
            specialBufferIcoOff = getFromCache(Constants.IMAGE_SPEC_OFF_NAME[0]);

            nextBufferIco = getFromCache(Constants.IMAGE_TIPS_NAME[0]);
            nextBufferIcoOff = getFromCache(Constants.IMAGE_TIPS_OFF_NAME[0]);

            lightModeIco = getFromCache(Constants.IMAGE_LITECORE_NAME[0]);
            lightModeIcoOff = getFromCache(Constants.IMAGE_LITECORE_OFF_NAME[0]);

            autoMusicChangeIco = getFromCache(Constants.IMAGE_AUTO_MUSIC_NAME[0]);
            autoMusicChangeIcoOff = getFromCache(Constants.IMAGE_AUTO_MUSIC_OFF_NAME[0]);
        } catch (Exception e) {
            log.error("Image cache got exception: {}", e.getMessage());
        }
    }

    private BufferedImage getFromCache(String imageLabel) {
        return (BufferedImage) Constants.getCache().get(imageLabel);
    }

    private void grayRectangleReDraw() {
        leftGrayBase = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2D = leftGrayBase.createGraphics();
        g2D.setRenderingHints(gameFrame.getRender());

        g2D.setColor(panelColor);
        g2D.fillRoundRect(0, 0, 600, 600, 20, 20);

        g2D.dispose();
    }

    private void drawGrayBack(JPanel panel, Graphics2D g2D) {
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(2));
        g2D.setRenderingHints(gameFrame.getRender());

        g2D.drawImage(leftGrayBase,
                3, 3,
                panel.getWidth() - 6, panel.getHeight() - 6,
                null);

        g2D.drawRoundRect(6, 6, panel.getWidth() - 12, panel.getHeight() - 12, 10, 10);
    }

    private void drawButton(Graphics2D g2D, JButton button, boolean over, boolean press, String buttonText, int x, int y) {
        g2D.setRenderingHints(gameFrame.getRender());

        try {
            Rectangle2D textBounds;

            g2D.setFont(Constants.getFont2());
            textBounds = g2D.getFontMetrics().getStringBounds(buttonText, g2D);

            if (press) {
                g2D.drawImage(buttonPressBufferIm, x + 2, y, button.getWidth() - x * 2 - 2, button.getHeight() - 3, this);
                g2D.setColor(Color.DARK_GRAY);
                g2D.drawString(buttonText,
                        (int) (button.getWidth() / 2D - textBounds.getWidth() / 2D - 3),
                        button.getHeight() / 2 + 1 + y);

                g2D.setColor(Color.WHITE);
                g2D.drawString(buttonText,
                        (int) (button.getWidth() / 2D - textBounds.getWidth() / 2D),
                        button.getHeight() / 2 + 4 + y);
            } else {
                BufferedImage bdi = buttonBufferIm;
                if (over) {
                    bdi = buttonOverBufferIm;
                }

                g2D.drawImage(bdi, x + 2, y, button.getWidth() - x * 2 - 2, button.getHeight() - 3, null);
                g2D.setColor(Color.DARK_GRAY);
                g2D.drawString(buttonText,
                        (int) (button.getWidth() / 2D - textBounds.getWidth() / 2D - 2),
                        button.getHeight() / 2 + 2 + y);

                g2D.setColor(Color.WHITE);
                g2D.drawString(
                        buttonText,
                        (int) (button.getWidth() / 2D - textBounds.getWidth() / 2D),
                        button.getHeight() / 2 + 4 + y
                );
            }
        } catch (Exception e) {
            log.error("Exception here: {}", e.getMessage());
        }
    }

    // Listeners:
    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getComponent().getName()) {
            case "mus" -> Constants.setMusButPress(true);
            case "opt" -> Constants.setOptButPress(true);
            case "aut" -> Constants.setAboButPress(true);
            case "exi" -> Constants.setExitButPress(true);
            default -> log.warn("e.getComponent().getName() has returned unknown value: " + e.getComponent().getName());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getComponent().getName()) {
            case "mus" -> Constants.setMusButPress(false);
            case "opt" -> Constants.setOptButPress(false);
            case "aut" -> Constants.setAboButPress(false);
            case "exi" -> Constants.setExitButPress(false);
            default -> log.error("e.getComponent().getName() has returned unknown value: {}", e.getComponent().getName());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        switch (e.getComponent().getName()) {
            case "mus" -> Constants.setMusButOver(true);
            case "opt" -> Constants.setOptButOver(true);
            case "aut" -> Constants.setAboButOver(true);
            case "exi" -> Constants.setExitButOver(true);
            default -> log.warn("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        switch (e.getComponent().getName()) {
            case "mus" -> Constants.setMusButOver(false);
            case "opt" -> Constants.setOptButOver(false);
            case "aut" -> Constants.setAboButOver(false);
            case "exi" -> Constants.setExitButOver(false);
            default -> log.warn("e.getComponent().getName() has returned uncknown value: " + e.getComponent().getName());
        }
    }

    public void mouseClicked(MouseEvent e) {}
}
