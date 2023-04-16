package ru.foxtris.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.KeyEvent;
import java.io.Serializable;

@Data
@Slf4j
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class UserConfigDTO implements Serializable {
    private String userName;
    private boolean isSoundEnabled;
    private boolean isShowStartLogo;
    private boolean isNextFigureShow;
    private boolean isSpecialBlocksEnabled;
    private boolean isAutoChangeMelody;
    private boolean isHardcoreMode;
    private boolean isLitecoreMode;
    private boolean isFullscreen;
    private int soundVolumePercent;
    private boolean isMusicEnabled;
    private int musicVolumePercent;
    private String gameThemeName;
    private int keyLeft;
    private int keyLeftMod;
    private int keyRight;
    private int keyRightMod;
    private int keyDown;
    private int keyDownMod;
    private int keyStuck;
    private int keyStuckMod;
    private int keyRotate;
    private int keyRotateMod;
    private int keyPause;
    private int keyPauseMod;
    private int keyConsole;
    private int keyConsoleMod;
    private int keyFullscreen;
    private int keyFullscreenMod;
    private boolean useBackImage;

    @JsonIgnore
    public void setKey(String paramName, int keyCode, int modifiersEx) {
        switch (paramName) {
            case "KEY_ROTATE" -> {
                setKeyRotate(keyCode);
                setKeyRotateMod(modifiersEx);
            }
            case "KEY_LEFT" -> {
                setKeyLeft(keyCode);
                setKeyLeftMod(modifiersEx);
            }
            case "KEY_RIGHT" -> {
                setKeyRight(keyCode);
                setKeyRotateMod(modifiersEx);
            }
            case "KEY_DOWN" -> {
                setKeyDown(keyCode);
                setKeyDownMod(modifiersEx);
            }
            case "KEY_STUCK" -> {
                setKeyStuck(keyCode);
                setKeyStuckMod(modifiersEx);
            }
            case "KEY_CONSOLE" -> {
                setKeyConsole(keyCode);
                setKeyConsoleMod(modifiersEx);
            }
            case "KEY_FULLSCREEN" -> {
                setKeyFullscreen(keyCode);
                setKeyFullscreenMod(modifiersEx);
            }
            default -> log.error("Unknown paramName {}", paramName);
        }
    }
}
