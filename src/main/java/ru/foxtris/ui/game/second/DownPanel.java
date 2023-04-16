package ru.foxtris.ui.game.second;

import lombok.extern.slf4j.Slf4j;
import ru.foxtris.config.Constants;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class DownPanel extends JPanel {

    public DownPanel() {
        setBackground(Color.BLACK);
        setIgnoreRepaint(true);
        add(new JLabel() {
            {
                setText("-= " + Constants.getName() + " =-  @FoxGroup Multiverse-39");
                setForeground(Color.YELLOW.darker());
                setFont(Constants.getDownInfoPaneFont());
                setAlignmentX(CENTER_ALIGNMENT);
            }
        });
    }
}
