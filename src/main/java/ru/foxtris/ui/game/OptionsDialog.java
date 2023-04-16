package ru.foxtris.ui.game;

import fox.FoxRender;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.ui.game.modal.ControlsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

@Slf4j
public class OptionsDialog extends JDialog implements MouseListener, KeyListener, MouseMotionListener {
    private final BufferedImage[] detailsBuffer = new BufferedImage[2];
    private final int horizontalStartX = 300, horizontalEndX = 350;
    private final MouseMotionListener mmList;
    private MouseListener mList;
    private KeyListener kList;
    private FontMetrics fm;
    private BufferedImage[] sp;
    private boolean isThemeButPressed, isThemeButOver, isControlsButPressed, isControlsButOver;

    public OptionsDialog(JFrame parent) {
        super(parent, true);

        log.debug("Building the OptionsDialog...");

        try {
            sp = Constants.getSpritesCombiner().getSprites(
                    Constants.IMAGE_BUTTON_UNIVERSAL_NAME[0],
                    (BufferedImage) Constants.getCache().get(Constants.IMAGE_BUTTON_UNIVERSAL_NAME[0]),
                    1, 3);
        } catch (Exception e) {
            log.error("Buttons sprite exception: {}", e.getMessage());
        }
        mmList = this;
        mList = this;
        kList = this;

        try {
            detailsBuffer[0] = (BufferedImage) Constants.getCache().get(Constants.IMAGE_SWITCH_ON_NAME[0]);
            detailsBuffer[1] = (BufferedImage) Constants.getCache().get(Constants.IMAGE_SWITCH_OFF_NAME[0]);
        } catch (Exception e1) {
            log.error("Uni button not found or error: {}", e1.getMessage());
        }

        setTitle("Окно настроек:");
        try {
            setIconImage((BufferedImage) Constants.getCache().get(Constants.IMAGE_GAME_ICO_NAME[0]));
        } catch (Exception e) {
            log.error("Game icon image not found or error: {}", e.getMessage());
        }
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);
        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        setSize(new Dimension(390, 620));
        setLocationRelativeTo(null);
        setResizable(false);

        add(new JPanel(new BorderLayout()) {
            {
                setBackground(Color.DARK_GRAY);
                setIgnoreRepaint(true);
                setFocusable(true);
                addKeyListener(kList);
                addMouseListener(mList);
                addMouseMotionListener(mmList);
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                Constants.getRender().setRender(g2d, FoxRender.RENDER.MED);

                fm = g2d.getFontMetrics(Constants.getFont0());

                g2d.setFont(Constants.getFont0());
                g2d.setColor(Color.BLACK);
                g2d.drawString("Настройки игры:", getSize().width / 2 - fm.stringWidth("Настройки игры:") / 2 - 2, 30 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Настройки игры:", getSize().width / 2 - fm.stringWidth("Настройки игры:") / 2, 30);

                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(10, 45, getSize().width - 20 - 1, getSize().height - 55 + 1, 10, 10);
                g2d.setColor(Color.GREEN);
                g2d.drawRoundRect(10, 45, getSize().width - 20, getSize().height - 55, 10, 10);

                g2d.setFont(Constants.getFont1());
                fm = g2d.getFontMetrics(Constants.getFont1());
                g2d.setColor(Color.BLACK);
                g2d.drawString("Включить звук:", 20 - 2, 80 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Включить звук:", 20, 80);

                try {
                    g2d.drawImage(!Constants.getSoundPlayer().isMuted() ? detailsBuffer[0] : detailsBuffer[1],
                            (int) (getSize().width * 0.75f), 60 - 2, 70, 40, null);
                } catch (Exception e) {
                    log.error("Sound player image not found or error: {}", e.getMessage());
                }

                g2d.setColor(Color.BLACK);
                g2d.drawString("Включить музыку:", 20 - 2, 155 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Включить музыку:", 20, 155);

                try {
                    g2d.drawImage(!Constants.getMusicPlayer().isMuted() ? detailsBuffer[0] : detailsBuffer[1],
                            (int) (getSize().width * 0.75f), 135 - 2,
                            70, 40, null);
                } catch (Exception e) {
                    log.error("Music player image not found or error: {}", e.getMessage());
                }


                g2d.setColor(Color.BLACK);
                g2d.drawString("Следующая фигура:", 20 - 2, 230 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Следующая фигура:", 20, 230);

                try {
                    g2d.drawImage(Constants.getConfig().isNextFigureShow() ? detailsBuffer[0] : detailsBuffer[1],
                            (int) (getSize().width * 0.75f), 210 - 2,
                            70, 40, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                g2d.setColor(Color.BLACK);
                g2d.drawString("Спец. блоки:", 20 - 2, 305 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Спец. блоки:", 20, 305);

                try {
                    g2d.drawImage(
                            Constants.getConfig().isSpecialBlocksEnabled() ? detailsBuffer[0] : detailsBuffer[1],
                            (int) (getSize().width * 0.75f), 285 - 2,
                            70, 40, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                g2d.setColor(Color.BLACK);
                g2d.drawString("Хардкор:", 20 - 2, 380 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString("Хардкор:", 20, 380);

                try {
                    g2d.drawImage(Constants.getConfig().isHardcoreMode() ? detailsBuffer[0] : detailsBuffer[1],
                            (int) (getSize().width * 0.75f), 360 - 2,
                            70, 40, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                g2d.setColor(Color.BLACK);
                g2d.drawString(
                        "Тема: '" + Constants.getConfig().getGameThemeName() + "' (" + (GameFrame.THEME.valueOf(Constants.getConfig().getGameThemeName()).ordinal() + 1) +
                                "/" + GameFrame.THEME.values().length + ")", 20 - 2, 455 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString(
                        "Тема: '" + Constants.getConfig().getGameThemeName() + "' (" + (GameFrame.THEME.valueOf(Constants.getConfig().getGameThemeName()).ordinal() + 1) +
                                "/" + GameFrame.THEME.values().length + ")", 20, 455);

                g2d.drawImage(isThemeButPressed ? sp[2] : isThemeButOver ? sp[0] : sp[1],
                        (int) (getSize().getWidth() * 0.8f), 425,
                        50, 50, null);

                g2d.setColor(Color.BLACK);
                g2d.drawString(
                        "Управление:",
                        20 - 2, 530 + 2);
                g2d.setColor(Color.GREEN);
                g2d.drawString(
                        "Управление:",
                        20, 530);

                g2d.drawImage(isControlsButPressed ? sp[2] : (isControlsButOver ? sp[0] : sp[1]),
                        (int) (getSize().getWidth() * 0.8f), 500,
                        50, 50,
                        null);

                g2d.dispose();
            }
        });

        setVisible(true);
        Constants.setPaused(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            mList = null;
            kList = null;
            dispose();
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int tmpX = e.getPoint().x, tmpY = e.getPoint().y;

        if (tmpX > horizontalStartX && tmpX < horizontalEndX) {
            if (tmpY > 55 && tmpY < 95) {
                if (Constants.getConfig().isSoundEnabled()) {
                    log.info("Disable sounds...");
                    Constants.getSoundPlayer().setMuted(true);
                    Constants.getConfig().setSoundEnabled(false);
                } else {
                    log.info("Enable sounds...");
                    Constants.getSoundPlayer().setMuted(false);
                    Constants.getConfig().setSoundEnabled(true);
                }
                repaint();
            } else if (tmpY > 135 && tmpY < 170) {
                if (Constants.getConfig().isMusicEnabled()) {
                    log.info("Disable music...");
                    Constants.getConfig().setMusicEnabled(false);
                    Constants.getMusicPlayer().setMuted(true);
                    Constants.getMusicPlayer().stop();
                } else {
                    log.info("Enable music...");
                    Constants.getConfig().setMusicEnabled(true);
                    Constants.getMusicPlayer().setMuted(false);
                    if (Constants.getMusicPlayer().getLastTrack() != null) {
                        Constants.getMusicPlayer().play(Constants.getMusicPlayer().getLastTrack());
                    }
                }
            } else if (tmpY > 210 && tmpY < 240) {
                if (Constants.getConfig().isNextFigureShow()) {
                    log.info("Disable next figures...");
                    Constants.getConfig().setNextFigureShow(false);
                } else {
                    log.info("Enable next figures...");
                    Constants.getConfig().setNextFigureShow(true);
                }
            } else if (tmpY > 285 && tmpY < 320) {
                if (Constants.getConfig().isSpecialBlocksEnabled()) {
                    log.info("Enable special blocks...");
                    Constants.getConfig().setSpecialBlocksEnabled(false);
                } else {
                    log.info("Disable special blocks...");
                    Constants.getConfig().setSpecialBlocksEnabled(true);
                }
            } else if (tmpY > 360 && tmpY < 390) {
                if (Constants.getConfig().isHardcoreMode()) {
                    log.info("Enable hardcore...");
                    Constants.getConfig().setHardcoreMode(false);
                } else {
                    log.info("Disable hardcore...");
                    Constants.getConfig().setHardcoreMode(true);
                }
            } else if (tmpY > 430 && tmpY < 470) {
                isControlsButPressed = false;
                if (!isThemeButPressed) {
                    isThemeButPressed = true;
                }

                log.info("Changing theme...");
                int nextThemeOrdinal = GameFrame.THEME.valueOf(Constants.getConfig().getGameThemeName()).ordinal() + 1;
                if (GameFrame.THEME.values().length > nextThemeOrdinal) {
                    Constants.getConfig().setGameThemeName(GameFrame.THEME.values()[nextThemeOrdinal].name());
                } else {
                    Constants.getConfig().setGameThemeName(GameFrame.THEME.values()[0].name());
                }
            } else if (tmpY > 500 && tmpY < 540) {
                isThemeButPressed = false;
                if (!isControlsButPressed) {
                    isControlsButPressed = true;
                }

                log.info("Changing controls...");
                Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
                new ControlsDialog(OptionsDialog.this);
                isControlsButPressed = false;
            }
            repaint();
        } else {
            if (isThemeButPressed) {
                isThemeButPressed = false;
                repaint();
            }
            if (isControlsButPressed) {
                isControlsButPressed = false;
                repaint();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        if (isThemeButPressed) {
            isThemeButPressed = false;
        }
        if (isControlsButPressed) {
            isControlsButPressed = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int tmpX = e.getPoint().x, tmpY = e.getPoint().y;

        if (tmpX > horizontalStartX && tmpX < horizontalEndX) {
            if (tmpY > 420 && tmpY < 480) {
                if (!isThemeButOver) {
                    isThemeButOver = true;
                }
            } else {
                isThemeButOver = false;
            }

            if (tmpY > 500 && tmpY < 540) {
                if (!isControlsButOver) {
                    isControlsButOver = true;
                }
            } else {
                isControlsButOver = false;
            }
            repaint();
        } else {
            if (isThemeButOver) {
                isThemeButOver = false;
                repaint();
            }
            if (isControlsButOver) {
                isControlsButOver = false;
                repaint();
            }
        }
    }

    public void mouseClicked(MouseEvent m) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void mouseDragged(MouseEvent e) {
    }
}
