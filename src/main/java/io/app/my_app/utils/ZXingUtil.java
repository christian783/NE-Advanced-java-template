package io.app.my_app.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ZXingUtil {
    public static String encodeQRCode(String contents) throws IOException, WriterException {
        BitMatrix bitMatrix =  new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "png", outputStream);
        return "data:image/png;base64,%s".formatted(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
    }
}