package ru.foxtris.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.foxtris.config.Constants;

import java.awt.event.KeyEvent;
import java.io.File;

@Slf4j
@Service
public final class GameConfigService {
    private final ObjectMapper mapper = new ObjectMapper();

    public void saveAll() {
        try {
            mapper.writeValue(new File(Constants.getSavesDirectory() + Constants.DEFAULT_SAVE_FILE_NAME), Constants.getConfig());
        } catch (Exception e) {
            log.error("Save all methode exception: {}", e.getMessage());
        }
    }

    public void resetControlKeys() {
        Constants.getConfig().setKeyLeft(KeyEvent.VK_LEFT);
        Constants.getConfig().setKeyLeftMod(0);
        Constants.getConfig().setKeyRight(KeyEvent.VK_RIGHT);
        Constants.getConfig().setKeyRightMod(0);
        Constants.getConfig().setKeyDown(KeyEvent.VK_DOWN);
        Constants.getConfig().setKeyDownMod(0);
        Constants.getConfig().setKeyStuck(KeyEvent.VK_UP);
        Constants.getConfig().setKeyStuckMod(0);
        Constants.getConfig().setKeyRotate(KeyEvent.VK_Z);
        Constants.getConfig().setKeyRotateMod(0);
        Constants.getConfig().setKeyConsole(KeyEvent.VK_BACK_QUOTE);
        Constants.getConfig().setKeyConsoleMod(0);
        Constants.getConfig().setKeyFullscreen(KeyEvent.VK_F);
        Constants.getConfig().setKeyFullscreenMod(0);
        Constants.getConfig().setKeyPause(KeyEvent.VK_ESCAPE);
        Constants.getConfig().setKeyPauseMod(0);

        saveAll();
    }
}
