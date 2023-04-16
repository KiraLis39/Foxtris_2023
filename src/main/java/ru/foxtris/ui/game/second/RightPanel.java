package ru.foxtris.ui.game.second;

import fox.FoxRender;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.ui.game.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Slf4j
public class RightPanel extends JPanel {
    private final Color panelColor = new Color(0.3f, 0.3f, 0.3f, 0.85f);
    private final GameFrame gameFrame;
    private final JPanel rightInfosPane;
    private final BufferedImage rightGrayBase = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
    private final BufferedImage lifeHeartImage = (BufferedImage) Constants.getCache().get("life");
    private final BufferedImage bonusKristalImage = (BufferedImage) Constants.getCache().get("bonus");
    private String tmpSpeed;

    public RightPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;

        setLayout(new BorderLayout());
        setIgnoreRepaint(true);
        setOpaque(false);

        grayRectangleReDraw();

        float leftShift = 21f;
        rightInfosPane = new JPanel(new BorderLayout()) {
            {
                setIgnoreRepaint(true);
            }

            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2D = (Graphics2D) g;

                Constants.getRender().setRender(g2D, FoxRender.RENDER.MED);
                drawGrayBack(this, g2D);
                drawControlInfo(g2D);

                g2D.dispose();
            }

            private void drawControlInfo(Graphics2D g2D) {
                Font fontA = Constants.getFont0();
                Font fontB = Constants.getFont1();

                tmpSpeed = String.format("%(.2fx", gameFrame.getSpeed());
                g2D.setColor(Color.BLACK);
                g2D.setFont(fontA);

                float spacing = gameFrame.getFontIncreaseMod() * 2f;
                float headerAlign = (float) (getWidth() / 2D - Constants.getFfb().getStringBounds(g2D, "Управление:").getWidth() / 2D);

                g2D.drawString("Управление:", headerAlign, spacing * 13f);
                g2D.drawString("_________________", headerAlign, spacing * 14f);
                g2D.setColor(Color.RED);
                g2D.drawString("Управление:", headerAlign + 2, spacing * 13f);
                g2D.drawString("_________________", headerAlign + 1, spacing * 14f);

                g2D.setColor(Color.GREEN);
                g2D.setFont(fontA);
                g2D.drawString("Влево:", leftShift, spacing * 25f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.LEFT),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 25f);

                g2D.setFont(fontA);
                g2D.drawString("Вправо:", leftShift, spacing * 35f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.RIGHT),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 35f);

                g2D.setFont(fontA);
                g2D.drawString("Вниз:", leftShift, spacing * 45f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.DOWN),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 45f);

                g2D.setFont(fontA);
                g2D.drawString("Поворот:", leftShift, spacing * 55f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.ROTATE),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 55f);

                g2D.setFont(fontA);
                g2D.drawString("Экран:", leftShift, spacing * 65f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.FULLSCREEN),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 65f);

                g2D.setFont(fontA);
                g2D.drawString("Консоль:", leftShift, spacing * 75f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.CONSOLE),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 75f);

                g2D.setFont(fontA);
                g2D.drawString("Сброс:", leftShift, spacing * 85f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.STUCK),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 85f);

                g2D.setFont(fontA);
                g2D.drawString("Пауза:", leftShift, spacing * 95f);
                g2D.setFont(fontB);
                g2D.drawString(gameFrame.getKeyLabel(GameFrame.KeyLabel.PAUSE),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 95f);
                g2D.setFont(fontA);
                g2D.setColor(Color.BLACK);

                headerAlign = (float) (getWidth() / 2D - Constants.getFfb().getStringBounds(g2D, "Информация:").getWidth() / 2D);

                g2D.drawString("Информация:", headerAlign, spacing * 114f);
                g2D.drawString("__________________", headerAlign, spacing * 115f);
                g2D.setColor(Color.RED);
                g2D.drawString("Информация: ", headerAlign + 1, spacing * 114f + 1);
                g2D.drawString("__________________", headerAlign + 1, spacing * 115f + 1);


                g2D.setColor(Color.GREEN);
                g2D.drawString("Линии:", leftShift, spacing * 125f);
                g2D.setFont(fontB);
                g2D.drawString(String.valueOf(Constants.getCollectedLinesCounter()),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 125f);

                g2D.setFont(fontA);
                g2D.drawString("Скорость:", leftShift, spacing * 135f);
                g2D.setFont(fontB);
                g2D.drawString(tmpSpeed,
                        84 * gameFrame.getFontIncreaseMod(), spacing * 135f);

                g2D.setFont(fontA);
                g2D.drawString("Очки:", leftShift, spacing * 145f);
                g2D.setFont(fontB);
                g2D.drawString(String.valueOf(Constants.getBalls()),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 145f);

                g2D.setFont(fontA);
                g2D.drawString("Цель:", leftShift, spacing * 155f);
                g2D.setFont(fontB);
                g2D.drawString(String.valueOf(Constants.getStages()[Constants.getStageCounter()]),
                        84 * gameFrame.getFontIncreaseMod(), spacing * 155f);


                g2D.setFont(fontA);
                g2D.setColor(Color.YELLOW);
                g2D.drawString("Бонусы:", leftShift, spacing * 178f);
                for (int i = 0; i < Constants.getBonusCount(); i++) {
                    g2D.drawImage(
                            bonusKristalImage,
                            (int) (80 * gameFrame.getFontIncreaseMod() + (19 * i)), (int) (spacing * 171f),
                            (int) (16 * gameFrame.getFontIncreaseMod()), (int) (16 * gameFrame.getFontIncreaseMod()),
                            null);
                }

                g2D.setColor(Color.RED);
                if (Constants.getLives() <= 0) {
                    g2D.setColor(Color.RED);
                }
                g2D.setFont(fontA);
                g2D.drawString("Жизни:", leftShift, spacing * 190f);
                for (int i = 0; i < Constants.getLives(); i++) {
                    g2D.drawImage(
                            lifeHeartImage,
                            (int) (80 * gameFrame.getFontIncreaseMod() + (19 * i)), (int) (spacing * 183f),
                            (int) (24 * gameFrame.getFontIncreaseMod()), (int) (16 * gameFrame.getFontIncreaseMod()),
                            null);
                }


                g2D.setColor(Color.WHITE);
                g2D.setFont(fontA);
                g2D.drawString("Игрок:", leftShift, getHeight() - 70f);
                g2D.setFont(fontB);
                g2D.drawString(Constants.getConfig().getUserName(),
                        84 * gameFrame.getFontIncreaseMod(), getHeight() - 70f);


                g2D.setFont(fontA);
                g2D.drawString("Время в игре:", leftShift, getHeight() - 50f);
                g2D.setFont(fontB);
                if (!Constants.isPaused()) {
                    g2D.drawString(
                            Constants.getDateFormat().format(System.currentTimeMillis() - gameFrame.getWas()),
                            leftShift, getHeight() - 30f);
                }
            }
        };

        add(rightInfosPane, BorderLayout.CENTER);
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        revalidate();
        repaint();
    }

    private void drawGrayBack(JPanel panel, Graphics2D g2D) {
        g2D.setColor(Color.BLACK);
        g2D.setStroke(new BasicStroke(2));
        g2D.setRenderingHints(gameFrame.getRender());

        g2D.drawImage(rightGrayBase,
                3, 3,
                panel.getWidth() - 6, panel.getHeight() - 6,
                null);

        g2D.drawRoundRect(6, 6, panel.getWidth() - 12, panel.getHeight() - 12, 10, 10);
    }

    public void grayRectangleReDraw() {
        Graphics2D g2D = rightGrayBase.createGraphics();
        g2D.setRenderingHints(gameFrame.getRender());

        g2D.setColor(panelColor);
        g2D.fillRoundRect(0, 0, 600, 600, 20, 20);

        g2D.dispose();
    }
}
