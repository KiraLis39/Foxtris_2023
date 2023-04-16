package ru.foxtris.ui.game;

import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.ui.game.modal.AboutDialog;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

@Slf4j
public class FrameMenuBar extends JMenuBar {

    public FrameMenuBar(GameFrame gameFrame) {
        JMenu subMenu0 = new JMenu("Общее") {
            {
                setMnemonic(KeyEvent.VK_1);

                JMenuItem item0 = new JMenuItem("Выход из игры");
                item0.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
                item0.addActionListener(e -> GameFrame.exitConfirm());

                add(item0);
            }
        };

        JMenu subMenu1 = new JMenu("Дополнительно") {
            {
                setMnemonic(KeyEvent.VK_2);

                JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem("Фоновое изображение") {
                    {
                        setSelected(Constants.getConfig().isUseBackImage());
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.ALT_DOWN_MASK));
                        addChangeListener(e -> {
                            if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
                                Constants.getConfig().setUseBackImage(true);
                            }
                        });
                    }
                };

                JRadioButtonMenuItem rbMenuItem2 = new JRadioButtonMenuItem("Стиль по умолчанию") {
                    {
                        setSelected(!Constants.getConfig().isUseBackImage());
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
                        addChangeListener(e -> {
                            if (((JRadioButtonMenuItem) e.getSource()).isSelected()) {
                                Constants.getConfig().setUseBackImage(false);
                            }
                        });
                    }
                };

                new ButtonGroup() {
                    {
//						setDefaultLookAndFeelDecorated(true);
                        add(rbMenuItem);
                        add(rbMenuItem2);
                    }
                };

                add(rbMenuItem);
                add(rbMenuItem2);
                addSeparator();

                JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem("Менять мелодию при смене уровня") {
                    {
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.ALT_DOWN_MASK));
                        setSelected(Constants.getConfig().isAutoChangeMelody());
                        addChangeListener(e ->
                                Constants.getConfig().setAutoChangeMelody(((JCheckBoxMenuItem) e.getSource()).isSelected())
                        );
                    }
                };

                add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem("Режим новичка (лёгкий)") {
                    {
                        setMnemonic(KeyEvent.VK_H);
                        addChangeListener(e -> Constants.getConfig().setLitecoreMode(((JCheckBoxMenuItem) e.getSource()).isSelected()));
                    }
                };

                add(cbMenuItem);
            }
        };

        JMenu subMenu2 = new JMenu("Справка") {
            {
                setMnemonic(KeyEvent.VK_3);

                JMenuItem item2 = new JMenuItem("Об игре", null) {
                    {
                        setAccelerator(KeyStroke.getKeyStroke("F1"));
                        getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
                        addActionListener(e -> {
                            AboutDialog aDia;
                            try {
                                aDia = new AboutDialog(gameFrame);
                                aDia.setVisible(true);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            Constants.setPaused(false);
                        });
                    }
                };

                JMenuItem item3 = new JMenuItem("Обновления", null) {
                    {
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.ALT_DOWN_MASK));
                        getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
                        addActionListener(e -> {

                        });
                    }
                };

                JMenuItem item4 = new JMenuItem("Обратная связь", null) {
                    {
                        setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK));
                        getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
                        addActionListener(e -> {

                        });
                    }
                };

                add(item2);
                add(item3);
                add(item4);
            }
        };

        add(subMenu0);
        add(subMenu1);
        add(subMenu2);
    }
}
