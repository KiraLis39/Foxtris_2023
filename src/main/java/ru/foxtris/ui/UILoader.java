package ru.foxtris.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.foxtris.config.Constants;
import ru.foxtris.dto.UserConfigDTO;
import ru.foxtris.service.GameConfigService;
import ru.foxtris.ui.game.StartMenuFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class UILoader {
    private final ObjectMapper mapper = new ObjectMapper();
    private final GameConfigService configService = new GameConfigService();
    private float frameOpacity;
    private Thread logoThread;

    public UILoader() {
        frameOpacity = 0.1f;

        try {
            log.info("Запуск программы.");
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            preparePrimaryResources();
            if (Constants.getConfig() == null) {
                throw new NullPointerException();
            }
            loadUserData();

            if (Constants.getConfig().isShowStartLogo()) {
                logoThread = new Thread(() -> {
                    try {
                        showLogo();
                    } catch (Exception e) {
                        log.error("Show logo exception: {}", e.getMessage());
                    }
                    log.debug("Logo has ended");
                });
                logoThread.setDaemon(true);
                logoThread.start();
            }

            loadResources();

            if (logoThread != null) {
                while (logoThread.isAlive()) {
                    try {
                        log.info("Start logo ends await...");
                        logoThread.join();
                    } catch (InterruptedException e) {
                        log.error("Join logo exception: {}", e);
                    }
                }
            }

            log.info("Launch the StartMenu...");
            new StartMenuFrame();
        } catch (Exception e) {
            log.error("Tetris start failed: " + e.getMessage());
        }
    }

    private void loadUserData() {
        log.debug("Load user save data...");
        Constants.getSoundPlayer().setCurrentPlayerVolumePercent(100);

        Constants.getSoundPlayer().setMuted(!Constants.getConfig().isSoundEnabled());
        Constants.getSoundPlayer().setLooped(false);
        Constants.getSoundPlayer().setCurrentPlayerVolumePercent(Constants.getConfig().getSoundVolumePercent());

        Constants.getMusicPlayer().setMuted(!Constants.getConfig().isMusicEnabled());
        Constants.getMusicPlayer().setLooped(true);
        Constants.getMusicPlayer().setCurrentPlayerVolumePercent(Constants.getConfig().getMusicVolumePercent());

        Constants.getMusicPlayer().setUseExperimentalQualityFormat(false);
        Constants.getMusicPlayer().setUseUnsignedFormat(false);
    }

    private void preparePrimaryResources() throws IOException {
        if (Files.notExists(Constants.getSavesDirectory())) {
            Files.createDirectories(Constants.getSavesDirectory());
        }

        if (Files.notExists(Path.of(Constants.getSavesDirectory() + Constants.DEFAULT_SAVE_FILE_NAME))) {
            log.error("Save file not found. Was created default one. Please restart for init them.");
            makeDefaultSaveFile();
        } else {
            Constants.setConfig(mapper.readValue(Paths.get(Constants.getSavesDirectory() + Constants.DEFAULT_SAVE_FILE_NAME).toFile(), UserConfigDTO.class));
        }

        Constants.getSoundPlayer().add(Constants.SOUND_LAUNCH_NAME[0], new File(Constants.SOUND_LAUNCH_NAME[1]));
        Constants.getSoundPlayer().add(Constants.SOUND_CLICK_NAME[0], new File(Constants.SOUND_CLICK_NAME[1]));
    }

    private void makeDefaultSaveFile() {
        UserConfigDTO configDTO = UserConfigDTO.builder()
                .isShowStartLogo(true)
                .isSoundEnabled(true)
                .soundVolumePercent(75)
                .isMusicEnabled(true)
                .musicVolumePercent(85)
                .gameThemeName("HOLO")
                .isNextFigureShow(true)
                .isSpecialBlocksEnabled(true)
                .isAutoChangeMelody(true)
                .isHardcoreMode(false)
                .isLitecoreMode(false)
                .userName("NoNameUser")
                .useBackImage(true)
                .build();
        Constants.setConfig(configDTO);
        configService.saveAll();

        configService.resetControlKeys();
    }

    private void loadResources() {
        log.debug("Loading main.tetris.media resources....");
        try {
            Constants.getCache().addIfAbsent(Constants.IMAGE_HARDCORE_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_HARDCORE_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_HARDCORE_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_HARDCORE_OFF_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_SPEC_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_SPEC_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_SPEC_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_SPEC_OFF_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_TIPS_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_TIPS_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_TIPS_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_TIPS_OFF_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_LIFE_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_LIFE_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_BONUS_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BONUS_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_AUTO_MUSIC_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_AUTO_MUSIC_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_AUTO_MUSIC_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_AUTO_MUSIC_OFF_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_LITECORE_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_LITECORE_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_LITECORE_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_LITECORE_OFF_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_GAME_ICO_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_GAME_ICO_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_BACKABOUT_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BACKABOUT_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_STARSABOUT_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_STARSABOUT_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_BABOUT_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BABOUT_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_BUTTON_PROTO_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BUTTON_PROTO_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_BUTTON_PROTO_OVER_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BUTTON_PROTO_OVER_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_BUTTON_PROTO_PRESS_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BUTTON_PROTO_PRESS_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_VICTORY_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_VICTORY_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_GAMEOVER_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_GAMEOVER_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_PAUSE_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_PAUSE_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_WIN_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_WIN_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_SWITCH_ON_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_SWITCH_ON_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_SWITCH_OFF_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_SWITCH_OFF_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_STAGE_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_STAGE_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_LOGOFOX_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_LOGOFOX_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_MBSL_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_MBSL_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_BUTTON_UNIVERSAL_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_BUTTON_UNIVERSAL_NAME[1])));
            Constants.getCache().addIfAbsent(Constants.IMAGE_NUMBERS_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_NUMBERS_NAME[1])));

            Constants.getCache().addIfAbsent(Constants.IMAGE_MAIN_BKG_NAME[0], Objects.requireNonNull(linkToBimage(Constants.IMAGE_MAIN_BKG_NAME[1])));
        } catch (Exception e) {
            log.error("Loading image resources exception: {}", e.getMessage());
        }

        Constants.getSoundPlayer().add(Constants.SOUND_LAUNCH_NAME[0], new File(Constants.SOUND_LAUNCH_NAME[1]));
        Constants.getSoundPlayer().add(Constants.SOUND_CLICK_NAME[0], new File(Constants.SOUND_CLICK_NAME[1]));

        try (Stream<Path> musics = Files.list(Paths.get("./resources/music"))) {
            for (Path file : musics.toList()) {
                Constants.getMusicPlayer().add(file.getFileName().toString(), file.toFile());
            }
        } catch (IOException e) {
            log.error("Loading audio resources exception: {}", e.getMessage());
        }

        log.debug("Resources loading accomplished!");
    }

    private BufferedImage linkToBimage(String url) throws IOException {
        URL res = this.getClass().getResource(url);
        if (res == null) {
            log.error("Resource URL '{}' is NULL", url);
            return null;
        }
        return ImageIO.read(res);
    }

    private void showLogo() {
        log.debug("Showing Logo...");
        Constants.getSoundPlayer().play(Constants.SOUND_LAUNCH_NAME[0], false);

        JFrame logoFrame = new JFrame(Constants.getMon().getConfiguration());
        logoFrame.setUndecorated(true);
        logoFrame.setBackground(new Color(0, 0, 0, 0));
        logoFrame.setOpacity(frameOpacity);

        try {
            try {
                BufferedImage image = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream(Constants.IMAGE_LOGO_NAME[1])));
                logoFrame.setPreferredSize(new Dimension(image.getWidth(logoFrame), image.getHeight(logoFrame)));

                logoFrame.add(new JPanel() {
                    @Override
                    public void paintComponent(Graphics g) {
                        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), logoFrame);
                    }
                });
            } catch (IOException ioe) {
                log.error("Error by reading start logo image: {}", ioe.getMessage());
            }

            logoFrame.pack();
            logoFrame.setLocationRelativeTo(null);
            logoFrame.setVisible(true);

            while (frameOpacity < 1.0f) {
                Thread.yield();
                frameOpacity += 0.0015f;
                if (frameOpacity > 1.0f) {
                    frameOpacity = 1.0f;
                }
                logoFrame.setOpacity(frameOpacity);
            }

            Thread.sleep(2000);

            while (frameOpacity > 0.0f) {
                Thread.yield();
                frameOpacity -= 0.002f;
                if (frameOpacity < 0.0f) {
                    frameOpacity = 0.0f;
                }
                logoFrame.setOpacity(frameOpacity);
            }
        } catch (Exception e) {
            log.error("Logo image not ready: {}", e.getMessage());
        }

        logoFrame.dispose();
    }
}
