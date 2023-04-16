package ru.foxtris.ui.game.modal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;
import ru.foxtris.service.GameConfigService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

@Slf4j
@RequiredArgsConstructor
public class ControlsDialog extends JDialog implements ActionListener {
    private static Dialog pak;
    private final GameConfigService configService = new GameConfigService();
    private final int DIALOG_WIDTH = 300;
    private final int DIALOG_HEIGHT = 400;

    public ControlsDialog(Dialog parent) {
        super(parent, true);

        log.debug("Building the ControlsDialog...");

        setTitle("Окно управления:");
        try {
            setIconImage((BufferedImage) Constants.getCache().get(Constants.IMAGE_GAME_ICO_NAME[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setModal(true);
        setModalExclusionType(ModalExclusionType.NO_EXCLUDE);
        setSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel baseControlPane = new JPanel(new BorderLayout()) {
            {
                setBackground(Color.DARK_GRAY);
                setForeground(Color.WHITE);

                JPanel controlsGridPane = new JPanel(new GridLayout(8, 1)) {
                    {
                        setBorder(new EmptyBorder(0, 6, 0, 0));
                        setBackground(Color.DARK_GRAY);
                        setForeground(Color.WHITE);
                        setFont(Constants.getFont2());

                        add(controlLine("KEY_LEFT", "Влево:", Constants.getConfig().getKeyLeft(), Constants.getConfig().getKeyLeftMod()));
                        add(controlLine("KEY_RIGHT", "Вправо:", Constants.getConfig().getKeyRight(), Constants.getConfig().getKeyRightMod()));
                        add(controlLine("KEY_DOWN", "Вниз:", Constants.getConfig().getKeyDown(), Constants.getConfig().getKeyDownMod()));
                        add(controlLine("KEY_STUCK", "Сброс:", Constants.getConfig().getKeyStuck(), Constants.getConfig().getKeyStuckMod()));

                        add(controlLine("KEY_ROTATE", "Поворот:", Constants.getConfig().getKeyRotate(), Constants.getConfig().getKeyRotateMod()));
                        add(controlLine("KEY_CONSOLE", "Консоль:", Constants.getConfig().getKeyConsole(), Constants.getConfig().getKeyConsoleMod()));
                        add(controlLine("KEY_FULLSCREEN", "Экран:", Constants.getConfig().getKeyFullscreen(), Constants.getConfig().getKeyFullscreenMod()));

                        add(controlLine("KEY_PAUSE", "Пауза:", Constants.getConfig().getKeyPause(), Constants.getConfig().getKeyPauseMod(), false));
                    }

                    private Component controlLine(String name, String description, int key, int mod) {
                        return controlLine(name, description, key, mod, true);
                    }

                    private Component controlLine(String name, String description, int key, int mod, boolean isModificable) {
                        return new JPanel(new BorderLayout()) {
                            {
                                setBackground(Color.DARK_GRAY);
                                setForeground(Color.WHITE);

                                JTextField nameField = new JTextField() {
                                    {
                                        setBackground(Color.DARK_GRAY);
                                        setForeground(Color.WHITE);
                                        setBorder(null);
                                        setName(name);
                                        setFont(Constants.getFont2());
                                        setText(description);
                                        setEditable(false);
                                        setFocusable(false);
                                    }
                                };

                                JButton keyBut = new JButton() {
                                    {
                                        setBackground(Color.BLACK);
                                        setForeground(isModificable ? Color.WHITE : Color.BLACK);
                                        setFont(Constants.getFont2());
                                        setName(key + "_" + mod);
                                        setText("' " + KeyEvent.getKeyText(key) + " '" + (mod == 0 ? "" : " + '" + InputEvent.getModifiersExText(mod) + "'"));
                                        setPreferredSize(new Dimension(DIALOG_WIDTH / 2, 0));

                                        addActionListener(ControlsDialog.this);

                                        setEnabled(isModificable);
                                    }
                                };

                                add(nameField, BorderLayout.CENTER);
                                add(keyBut, BorderLayout.EAST);
                            }
                        };
                    }
                };

                JPanel buttonsDownPane = new JPanel(new BorderLayout()) {
                    {
                        setBackground(Color.DARK_GRAY);
                        setForeground(Color.WHITE);
                        setBorder(new EmptyBorder(3, 3, 6, 3));

                        add(new JButton("Принять") {
                            {
                                setBackground(new Color(0.75f, 1.0f, 0.75f));
                                setPreferredSize(new Dimension(150, 30));
                                addActionListener(e -> dispose());
                            }
                        }, BorderLayout.WEST);

                        add(new JButton("Сброс") {
                            {
                                setBackground(new Color(1.0f, 0.75f, 0.75f));
                                setPreferredSize(new Dimension(100, 30));
                                addActionListener(e -> {
                                    try {
                                        configService.resetControlKeys();
                                    } catch (Exception ex) {
                                        log.error("Reset control keys exception: {}", ex.getMessage());
                                    } finally {
                                        dispose();
                                    }
                                });
                            }
                        }, BorderLayout.EAST);
                    }
                };

                add(controlsGridPane, BorderLayout.CENTER);
                add(buttonsDownPane, BorderLayout.SOUTH);
            }
        };

        add(baseControlPane);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        pak = new Dialog(this, null, true) {
            final int DIALOG_W = 300, DIALOG_H = 100;

            {
                setSize(DIALOG_W, DIALOG_H);
                setUndecorated(true);
                setLocationRelativeTo(null);
                setBackground(Color.DARK_GRAY);

                add(new JLabel("Нажми нужную клавишу...") {
                    {
                        setFont(Constants.getFont2());
                        setForeground(Color.WHITE);
                        setHorizontalAlignment(CENTER);
                    }
                });
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2D = (Graphics2D) g;

                g2D.setStroke(new BasicStroke(3));
                g2D.setColor(Color.GRAY);
                g2D.drawRoundRect(5, 5, DIALOG_W - 10, DIALOG_H - 10, 5, 5);

                g2D.dispose();
            }
        };
        pak.addKeyListener(new KeyAdapterHandle((JButton) e.getSource()));
        pak.setVisible(true);
    }

    public class KeyAdapterHandle extends KeyAdapter {
        private final JButton tmpBut;

        public KeyAdapterHandle(JButton _tmpBut) {
            tmpBut = _tmpBut;
        }

        @Override
        public void keyReleased(KeyEvent e) {
            JPanel parentPane = (JPanel) tmpBut.getParent();
            String paramName = null;

            for (Component comp : parentPane.getComponents()) {
                if (comp instanceof JTextField field) {
                    paramName = field.getName();
                    break;
                }
            }

            Constants.getConfig().setKey(paramName, e.getKeyCode(), e.getModifiersEx());

            tmpBut.setText("' "
                    .concat(KeyEvent.getKeyText(e.getKeyCode()))
                    .concat((InputEvent.getModifiersExText(e.getModifiersEx()).equals("") ?
                            " '" :
                            " ' + '".concat(InputEvent.getModifiersExText(e.getModifiersEx()).concat("'")))
                    ));

            configService.saveAll();
            pak.dispose();

            Constants.getSoundPlayer().play(Constants.SOUND_CLICK_NAME[0]);
        }
    }
}
