package ru.foxtris.ui.game;

import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.service.GameConfigService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.concurrent.TimeUnit;

@Slf4j
public class StartMenuFrame extends JFrame implements MouseListener, MouseMotionListener {
    private final int FRAME_WIDTH = 800, FRAME_HEIGHT = 600;
    private final GameConfigService configService = new GameConfigService();
    private final BufferStrategy bs;
    private final Thread repTh;
    private Canvas canvas;
    private Rectangle2D startB, optionB, exitB, userChanger;
    private VolatileImage backgroundImage;
    private BufferedImage[] sp;
    private Point2D.Float startButtonPaint;
    private Point2D cursorPoint;
    private boolean repThRun, startPress, startOver, optionsPress, optionsOver, exitPress, exitOver;
    private float tmpInt0, tmpInt1;

    public StartMenuFrame() {
        log.debug("Building the StartMenu...");

        initialization();

        setTitle(Constants.getName());
        try {
            setIconImage((Image) Constants.getCache().get(Constants.IMAGE_GAME_ICO_NAME[0]));
        } catch (Exception e1) {
            log.warn("gameIcon image cant found");
        }
        setResizable(false);
        setIgnoreRepaint(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        getContentPane().setLayout(new BorderLayout());
        Color colorBackground = new Color(0.3f, 0.3f, 0.3f, 1.0f);
        getContentPane().setBackground(colorBackground);

        canvas = new Canvas(Constants.getMon().getConfiguration());
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);

        add(canvas);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        createBackBuffer();
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();

        int descret = Constants.getMon().getRefreshRate();
        log.debug("nmRefresh rate current monitor is " + descret);

        Constants.getMusicPlayer().play(Constants.MUSIC_START_MENU);

        repThRun = true;
        repTh = new Thread(() -> {
            log.debug("Launch draw-thread...");
            while (repThRun) {
                if (bs == null || canvas == null) {
                    repThRun = false;
                    return;
                }

                Graphics2D g2D = (Graphics2D) bs.getDrawGraphics();
                try {
                    do {
                        do {
                            g2D = (Graphics2D) bs.getDrawGraphics();
                            g2D.drawImage(backgroundImage, 0, 0, null);
                            g2D.drawImage(startPress ? sp[8] : startOver ? sp[7] : sp[6],
                                    (int) startButtonPaint.x,
                                    (int) startButtonPaint.y,
                                    (int) tmpInt0,
                                    (int) (tmpInt1 + 64),
                                    0, 0, 512, 64, null);
                            g2D.drawImage(optionsPress ? sp[5] : optionsOver ? sp[4] : sp[3],
                                    (int) startButtonPaint.x,
                                    (int) (startButtonPaint.y + 84),
                                    (int) tmpInt0,
                                    (int) (tmpInt1 + 148),
                                    0, 0, 512, 64, null);
                            g2D.drawImage(exitPress ? sp[2] : exitOver ? sp[1] : sp[0],
                                    (int) startButtonPaint.x,
                                    (int) (startButtonPaint.y + 168),
                                    (int) tmpInt0,
                                    (int) (tmpInt1 + 232),
                                    0, 0, 512, 64, null);
                        } while (bs.contentsRestored());
                    } while (bs.contentsLost());

                    bs.show();
                } catch (Exception e) {
                    log.error("Exception: {}", e.getMessage());
                } finally {
                    g2D.dispose();
                }

                Toolkit.getDefaultToolkit().sync();

                try {
                    TimeUnit.MILLISECONDS.sleep(getDelayByDescret(descret));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            log.debug("Draw-thread has stop correctly.");
        });
        repTh.start();

        log.debug("StartMenu was been builded.");
    }

    private long getDelayByDescret(int descret) {
        if (descret <= 60) {
            return 33;
        } else if (descret <= 72) {
            return 30;
        } else {
            return 24;
        }
    }

    private void initialization() {
        log.debug("Initialization StartMenu...");

        try {
            sp = Constants.getSpritesCombiner().getSprites(
                    Constants.IMAGE_MBSL_NAME[0],
                    (BufferedImage) Constants.getCache().get(Constants.IMAGE_MBSL_NAME[0]),
                    3, 3);
        } catch (Exception e) {
            log.error("Cut sprite exception: {}", e.getMessage());
        }

        startButtonPaint = new Point2D.Float(FRAME_WIDTH / 4f, FRAME_HEIGHT / 3f);
        tmpInt0 = FRAME_WIDTH / 4f * 3f;
        tmpInt1 = FRAME_HEIGHT / 3f;

        startB = new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY(), FRAME_WIDTH / 2f, 64);
        optionB = new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() + 84, FRAME_WIDTH / 2f, 64);
        exitB = new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() + 168, FRAME_WIDTH / 2f, 64);
        userChanger = new Rectangle2D.Double(startButtonPaint.getX(), startButtonPaint.getY() - 120, FRAME_WIDTH / 2f, 64);
    }

    private void deInitialization() {
        configService.saveAll();
        log.info("De-inititialization of the StartMenuFrame...");

        repTh.interrupt();
        sp = null;
        startButtonPaint = null;
        backgroundImage = null;
        canvas = null;

        Constants.getMusicPlayer().stop();
        dispose();
        log.info("De-inititialization accomlish. Lets GC...");
    }

    private void createBackBuffer() {
        log.debug("Creating the BackBuffer...");

        RenderingHints d2DRender = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        d2DRender.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE));
        d2DRender.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));

        BufferedImage buffBackground = (BufferedImage) Constants.getCache().get(Constants.IMAGE_MAIN_BKG_NAME[0]);
        backgroundImage = Constants.getMon().getConfiguration().createCompatibleVolatileImage(buffBackground.getWidth(), buffBackground.getHeight(), 2);
        if (backgroundImage.validate(Constants.getMon().getConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE) {
            log.warn("createBackBuffer: IMAGE_INCOMPATIBLE");
        }

        Graphics2D g2D = backgroundImage.createGraphics();
        g2D.addRenderingHints(d2DRender);

        g2D.drawImage(buffBackground, 0, 0, canvas);

        g2D.setStroke(new BasicStroke(3.0f));
        g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));

        g2D.setColor(Color.GRAY);
        g2D.fillRoundRect((int) (startButtonPaint.x - 7), (int) (startButtonPaint.y - 6), backgroundImage.getWidth() / 2 + 14, 245, 25, 25);

        g2D.setColor(Color.BLACK);
        g2D.drawRoundRect((int) (startButtonPaint.x - 2), (int) startButtonPaint.y, backgroundImage.getWidth() / 2 + 3, 235, 25, 25);

        g2D.setFont(Constants.getFont0());
        g2D.setColor(Color.WHITE);
        g2D.drawString("v" + Constants.getVersion(), 20, 40);
        g2D.drawString("Multiverse_39 @FoxGroup, 2023", backgroundImage.getWidth() - 360, backgroundImage.getHeight() - 16);


        g2D.setColor(Color.GRAY);
        g2D.fillRoundRect((int) (startButtonPaint.x - 7), (int) (startButtonPaint.y / 3), backgroundImage.getWidth() / 2 + 14, 90, 28, 28);

        g2D.setColor(Color.DARK_GRAY);
        g2D.drawRoundRect((int) (startButtonPaint.x - 2), (int) (startButtonPaint.y / 3 + 7), backgroundImage.getWidth() / 2 + 3, 78, 24, 24);

        g2D.setFont(Constants.getFont1());
        FontMetrics fm = g2D.getFontMetrics();
        g2D.setColor(Color.BLACK);
        String userName = "Игрок: " + Constants.getConfig().getUserName();
        g2D.drawString(userName,
                getWidth() / 2 - fm.stringWidth(userName) / 2 - 4,
                getHeight() / 6 + 2);
        g2D.drawString("(жми сюда, если это не ты)",
                getWidth() / 2 - fm.stringWidth("(жми сюда, если это не ты)") / 2 - 2,
                getHeight() / 6 + 32 + 4);

        g2D.setColor(Color.GREEN);
        g2D.drawString(userName,
                getWidth() / 2 - fm.stringWidth(userName) / 2 - 2, getHeight() / 6);
        g2D.setColor(Color.WHITE);
        g2D.drawString("(жми сюда, если это не ты)",
                getWidth() / 2 - fm.stringWidth("(жми сюда, если это не ты)") / 2,
                getHeight() / 6 + 32);

        g2D.dispose();

        log.info("BackBuffer was created successfully.");
    }

    private void changeUser() {
        String newUserName = JOptionPane.showInputDialog(
                this, "Как тебя зовут?", "Новый игрок:",
                JOptionPane.QUESTION_MESSAGE);
        if (newUserName != null && !newUserName.isBlank()) {
            Constants.getConfig().setUserName(newUserName);
            createBackBuffer();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        cursorPoint = e.getPoint();
        if (startB.contains(cursorPoint) && (!startPress)) {
            startPress = true;
        } else if (optionB.contains(cursorPoint) && (!optionsPress)) {
            optionsPress = true;
        } else if (exitB.contains(cursorPoint) && (!exitPress)) {
            exitPress = true;
        }
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        cursorPoint = e.getPoint();

        if (startB.contains(cursorPoint)) {
            deInitialization();
            new GameFrame();
        }

        if (optionB.contains(cursorPoint)) {
            Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0], false);
            new OptionsDialog(StartMenuFrame.this);
        }

        if (exitB.contains(cursorPoint)) {
            deInitialization();
            configService.saveAll();
            System.exit(0);
        }

        if (userChanger.contains(cursorPoint)) {
            changeUser();
        }

        startPress = false;
        optionsPress = false;
        exitPress = false;

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPoint = e.getPoint();

        if (startB.contains(cursorPoint)) {
            if (!startOver) {
                startOver = true;
                repaint();
            }
        } else {
            if (startOver) {
                startOver = false;
                repaint();
            }
        }
        if (optionB.contains(cursorPoint)) {
            if (!optionsOver) {
                optionsOver = true;
                repaint();
            }
        } else {
            if (optionsOver) {
                optionsOver = false;
                repaint();
            }
        }
        if (exitB.contains(cursorPoint)) {
            if (!exitOver) {
                exitOver = true;
                repaint();
            }
        } else {
            if (exitOver) {
                exitOver = false;
                repaint();
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
