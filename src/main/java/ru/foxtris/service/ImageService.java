package ru.foxtris.service;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

@Data
@Service
public class ImageService {
    @Getter
    @Setter
    private BufferedImage proto, noneOneBrick, greenOneBrick, orangeOneBrick, purpleOneBrick, yellowOneBrick, blueOneBrick, redOneBrick, blackOneBrick;
}
