package com.nftworlds.wallet.qrmaps;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Hashtable;

public final class LinkUtils {

    /**
     * It takes a URL, sends it to bit.ly, and returns the shortened URL
     *
     * @param url The URL to shorten
     * @return A shortened URL
     */
    public static @NotNull String shortenURL(@NotNull String url) throws IOException, InterruptedException {
        // TODO: Move bit.ly API key to config
        final String requestBody = new JSONObject().put("long_url", url).toString();

        final HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer 25eb1a884375f6f92d9773ad9e7a3f30d26a6551")
                .uri(URI.create("https://api-ssl.bitly.com/v4/shorten"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        final HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        final JSONObject jsonObject = new JSONObject(response.body());
        return jsonObject.getString("link");
    }

    /**
     * It takes a string and an integer, and returns a QR code image
     *
     * @param url The URL to be encoded in the QR code
     * @param size The size of the QR code image.
     * @return A BufferedImage object.
     */
    public static @NotNull BufferedImage createQRCode(@NotNull String url, int size) throws WriterException {
        final Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        final QRCodeWriter writer = new QRCodeWriter();
        final BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size, hintMap);

        final int matrixSize = bitMatrix.getWidth();
        final BufferedImage image = new BufferedImage(matrixSize, matrixSize, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixSize, matrixSize);

        graphics.setColor(Color.BLACK);
        for (int x = 0; x < matrixSize; x++) {
            for (int y = 0; y < matrixSize; y++) {
                if (bitMatrix.get(x, y)) {
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        }

        bitMatrix.clear();

        return image;
    }
}
