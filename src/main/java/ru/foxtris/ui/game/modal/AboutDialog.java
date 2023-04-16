package ru.foxtris.ui.game.modal;

import fox.FoxRender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class AboutDialog extends JDialog implements KeyListener {
    private final Random rand = new Random();
    private final Color color0 = new Color(0.2f, 0.2f, 0.2f, 0.75f);
    private final Color color1 = new Color(0.35f, 0.35f, 0.35f, 1);
    private final Color color2 = new Color(1, 0.75f, 1, 0.95f);
    private final Color color3 = new Color(0.5f, 1, 1, 0.95f);
    private final long repaintTime = 17;
    private final String verticalSeparator = "_____________________";
    private final String tmp0 = "Информация об игре:";
    private final String tmp1 = "Руководитель: KiraLis39";
    private final String tmp2 = "Программист: KiraLis39";
    private final String tmp3 = "Графика: KiraLis39 и интернет";
    private final String tmp4 = "Музыка: KiraLis39 и KeyGen";
    private final String tmp5 = "Тестирование: @FoxGroup";
    private final String tmp6 = "2021 год";
    private final String tmp7 = "Multiverse_39.";
    private final String tmp9 = "Alt + F4 - меню выхода;";
    private final String tmp13 = "F1 - справка Об игре.";
    private final String tmp139 = "Предложения и жалобы: AngelicaLis39@mail.ru";
    private BufferedImage[] sp;
    private BufferedImage backSkyBuffer, middleStarsBuffer, bAbout, grayBaseImage;
    private Thread backAniThread;
    private Canvas canvas;
    private FontMetrics fm;
    private BufferStrategy bs;
    private Double rotation = 0.0D;
    private final Double rotationSpeed = 0.0075D;
    private float landRound = 0.0f, figuresRound = 0.0f, shiftFloat0 = 0f, shiftFloat1 = 0f;
    private int randFig0 = 0, randFig1 = 0, integer0, integer1;
    private Graphics2D g2D;

    public AboutDialog(JFrame parent) {
        super(parent, true);

        init();

        setTitle("Об игре:");
        setIconImage((BufferedImage) Constants.getCache().get(Constants.IMAGE_GAME_ICO_NAME[0]));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModal(true);

        setMinimumSize(new Dimension(600, 600));
        setResizable(false);
        setLocationRelativeTo(null);

        canvas = new Canvas(Constants.getMon().getConfiguration());
        canvas.addKeyListener(AboutDialog.this);

        add(canvas);

        addKeyListener(this);

        backAniThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!AboutDialog.this.isVisible()) {
                    Thread.yield();
                }
                if (bs == null) {
                    try {
                        canvas.createBufferStrategy(3);
                    } catch (Exception e) {
                        canvas.createBufferStrategy(2);
                    }
                }

                while (AboutDialog.this.isVisible() && canvas != null) {
                    redrawCanvas();

                    try {
                        TimeUnit.MILLISECONDS.sleep(repaintTime);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }

                deInit();
            }

            private void redrawCanvas() {
                bs = canvas.getBufferStrategy();
                g2D = (Graphics2D) bs.getDrawGraphics();
                Constants.getRender().setRender(g2D, FoxRender.RENDER.LOW);

                drawSky();
                drawFallingFigures();
                drawContentInfo();

                g2D.dispose();
                bs.show();
            }

            private void drawSky() {
                g2D.drawImage(backSkyBuffer, 0, 0, backSkyBuffer.getWidth(), backSkyBuffer.getHeight(), null);

//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.65f));
                g2D.drawImage(middleStarsBuffer,
                        0, 0,
                        600, 600,

                        0,
                        (int) (0f + landRound),
                        middleStarsBuffer.getWidth(),
                        (int) (600f + landRound), null);
//				g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
            }

            private void drawFallingFigures() {
                if (landRound < 595f) {
                    landRound += 1.2f;
                } else {
                    landRound = 0f;
                }

                if (figuresRound + 1.75f < 690f) {
                    figuresRound += 1.75f;
                } else {
                    figuresRound = 20f;
                    randFig0 = getRandomFigure();
                    randFig1 = getRandomFigure();
                    shiftFloat0 = rand.nextInt(16) + 4f;
                    shiftFloat1 = rand.nextInt(16) + 4f;
                }

                integer0 = (int) (-180f + figuresRound + shiftFloat0 * 7);
                integer1 = (int) (-180f + figuresRound + shiftFloat1 * 8);
                rotation += rotationSpeed;

                int x = (int) ((5f + shiftFloat0) + sp[randFig0].getWidth() / 2f);
                int y = (int) (integer0 + sp[randFig0].getHeight() / 2f);

                g2D.translate(x, y);
                g2D.rotate(-rotation);
                g2D.drawImage(sp[randFig0], -(sp[randFig0].getWidth() / 2), -(sp[randFig0].getHeight() / 2), AboutDialog.this);
                g2D.rotate(rotation);
                g2D.translate(-x, -y);


                x = (int) ((475f + shiftFloat1) + sp[randFig1].getWidth() / 2f);
                y = (int) (integer1 + sp[randFig1].getHeight() / 2f);

                g2D.translate(x, y);
                g2D.rotate(rotation);
                g2D.drawImage(sp[randFig1], -(sp[randFig1].getWidth() / 2), -(sp[randFig1].getHeight() / 2), AboutDialog.this);
                g2D.rotate(-rotation);
                g2D.translate(-x, -y);
            }

            private void drawContentInfo() {
                g2D.setFont(Constants.getFont2());
                fm = g2D.getFontMetrics();

                g2D.drawImage(grayBaseImage, 100, 0, getWidth() - 200, 560, null);

                g2D.setColor(color2);
                g2D.drawString(tmp0, 300 - fm.stringWidth(tmp0) / 2, 45);
                g2D.drawString(verticalSeparator, 300 - fm.stringWidth(verticalSeparator) / 2 - 5, 65);

                g2D.setColor(color3);
                g2D.setFont(Constants.getFont2());
                fm = g2D.getFontMetrics();

                g2D.drawString(tmp1, 300 - fm.stringWidth(tmp1) / 2, 100);
                g2D.drawString(tmp2, 300 - fm.stringWidth(tmp2) / 2, 130);
                g2D.drawString(tmp3, 300 - fm.stringWidth(tmp3) / 2, 160);
                g2D.drawString(tmp4, 300 - fm.stringWidth(tmp4) / 2, 190);
                g2D.drawString(tmp5, 300 - fm.stringWidth(tmp5) / 2, 220);

                g2D.drawString(tmp6, 300, 260);
                g2D.drawString(tmp7, 300, 280);

                g2D.setColor(color2);
                g2D.setFont(Constants.getFont2());
                fm = g2D.getFontMetrics();

                g2D.drawString(verticalSeparator, 300 - fm.stringWidth(verticalSeparator) / 2 - 5, 460);

                g2D.setColor(color3);
                g2D.setFont(Constants.getFont2());
                fm = g2D.getFontMetrics();

                g2D.drawString(tmp9, 300 - fm.stringWidth(tmp9) / 2, 480);
                g2D.drawString(tmp13, 300 - fm.stringWidth(tmp13) / 2, 500);

                g2D.setColor(color1);
                g2D.drawString(tmp139, 300 - fm.stringWidth(tmp139) / 2 - 4, 540);
            }
        });
        backAniThread.start();
    }

    private int getRandomFigure() {
        return rand.nextInt(4);
    }

    private void init() {
        log.debug("About frame init...");

        randFig0 = getRandomFigure();
        randFig1 = getRandomFigure();
        shiftFloat0 = rand.nextInt(8) + 4f;
        shiftFloat1 = rand.nextInt(8) + 4f;

        bAbout = (BufferedImage) Constants.getCache().get(Constants.IMAGE_BABOUT_NAME[0]);
        backSkyBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_BACKABOUT_NAME[0]);
        middleStarsBuffer = (BufferedImage) Constants.getCache().get(Constants.IMAGE_STARSABOUT_NAME[0]);

        if (grayBaseImage == null) {
            grayBaseImage = new BufferedImage(450, 590, BufferedImage.TYPE_INT_ARGB);
            g2D = grayBaseImage.createGraphics();

            g2D.setColor(color0);
            g2D.fillRect(0, 0, grayBaseImage.getTileWidth(), grayBaseImage.getHeight());

            g2D.setColor(color1);
            g2D.drawRoundRect(9, 9, grayBaseImage.getTileWidth() - 18, grayBaseImage.getHeight() - 18, 9, 9);
        }

        sp = Constants.getSpritesCombiner().getSprites(
                Constants.IMAGE_BABOUT_NAME[0],
                bAbout, 2, 2);
    }

    private void deInit() {
        log.debug("About frame de-init...");
        backAniThread.interrupt();
        fm = null;
        canvas = null;
        g2D.dispose();
        Constants.getMusicPlayer().playNext();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
            deInit();
            dispose();
        }
    }

    public void keyTyped(KeyEvent e) {
        // ignored
    }

    public void keyReleased(KeyEvent e) {
        // ignored
    }
}
