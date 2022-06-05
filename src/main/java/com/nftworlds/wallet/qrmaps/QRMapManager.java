package com.nftworlds.wallet.qrmaps;

import com.google.zxing.WriterException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class QRMapManager extends MapRenderer {
    public static HashMap<UUID, ItemStack> playerPreviousItem = new HashMap<>();

    private BufferedImage image;
    private boolean loaded;

    public QRMapManager() {
        this.loaded = false;
    }

    public QRMapManager(@NotNull String url) {
        this.loaded = false;
        this.load(url);
    }

    /**
     * It creates a QR code from the given URL, resizes it, and stores it in the image variable
     *
     * @param url The URL to be encoded
     * @return A boolean value.
     */
    public boolean load(@NotNull String url) {
        try {
            this.image = LinkUtils.createQRCode(url, 128);
            this.image = MapPalette.resizeImage(this.image);
        } catch (WriterException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * If the map is loaded, don't do anything.
     * Otherwise, draw the image to the map and disable tracking.
     *
     * @param view The MapView object that is being rendered.
     * @param canvas The canvas to draw on.
     * @param player The player who is viewing the map.
     */
    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (this.loaded) return;

        canvas.drawImage(0, 0, this.image);
        view.setTrackingPosition(false);

        this.loaded = true;
    }
}
