package com.alex;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import org.apache.commons.io.FileUtils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

public class Pack {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("missing arguments [sprites, tiles]");
        } else if (args[0].equals("sprites")) {
            System.out.println("Start packing sprites");

            Settings settings = new Settings();
            settings.pot = false;
            settings.ignoreBlankImages = false;
            settings.maxHeight = 2048;
            settings.maxWidth = 2048;
            settings.debug = false;
            settings.fast = false;
            settings.duplicatePadding = false;
            settings.paddingX = 0;
            settings.paddingY = 0;
            settings.grid = false;

            Pack.packSprites(
                    settings,
                    "../tikotep-adventure/tikotep-adventure-desktop/assets/data/images",
                    "tikotep"
            );
        } else if (args[0].equals("tiles")) {
            System.out.println("Start packing tiles");
            Pack.packTiles();
        }
    }

    private static void packTiles() throws IOException {
        int tileSize = 16;
        String path = "../pack-tiles-test";

        File list = new File(path);
        File[] fileList = list.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".png");
            }
        });

        for (File tileset : fileList) {
            System.out.println(tileset.getName());
            System.out.println("clean output dir");
            FileUtils.cleanDirectory(new File(path + "/tmp"));
//            String tilesetName = "cerf-world.png";
//            File tileset = new File(path + "/" + tilesetName);

            FileInputStream fis = new FileInputStream(tileset);
            BufferedImage image = ImageIO.read(fis);

            int cols = image.getWidth() / tileSize;
            int rows = image.getHeight() / tileSize;

            BufferedImage images[] = new BufferedImage[cols * rows];
            int count = 0;

            for (int x = 0; x < rows; x++) {
                for (int y = 0; y < cols; y++) {
                    images[count] = new BufferedImage(tileSize, tileSize, image.getType());

                    Graphics2D gr = images[count].createGraphics();
                    gr.drawImage(
                            image, //
                            0, 0, //
                            tileSize, tileSize,//
                            tileSize * y, tileSize * x, //
                            tileSize * y + tileSize, tileSize * x + tileSize, //
                            null
                    );
                    gr.dispose();
                    count++;
                }
            }
            System.out.println("Splitted");

            for (int i = 0; i < images.length; i++) {
                ImageIO.write(images[i], "png", new File(path + "/tmp/tiles_" + i + ".png"));
            }
            System.out.println("tiles images created");

            int padding = 2;

            Settings settings = new Settings();
            settings.pot = false;
            settings.ignoreBlankImages = false;
            settings.maxHeight = image.getHeight() + (padding * (rows + 1));
            settings.maxWidth = image.getWidth() + (padding * (cols + 1));
            settings.debug = false;
            settings.fast = false;
            settings.alias = false;
            settings.duplicatePadding = true;
            settings.paddingX = padding;
            settings.paddingY = padding;
            settings.grid = true;
            settings.fast = true;

            Pack.packSprites(
                    settings,
                    path + "/tmp",
                    "padded-" + tileset.getName().replaceAll("\\.png", "")
            );

            FileUtils.copyFileToDirectory(
                    new File(path + "/tmp/" + "padded-" + tileset.getName()),
                    new File(path + "/out")
            );
        }
    }

    private static void packSprites(Settings settings, String path, String atlas) {
        TexturePacker.process(
                settings,
                path,
                path,
                atlas
        );
    }
}
