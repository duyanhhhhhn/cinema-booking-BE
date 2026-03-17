package CinemaBooking.Group2.ultis;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


public class QrCodeUtil {

    /**
     * Tạo QR code từ text và trả về Base64 encoded PNG image.
     * Có thể nhúng trực tiếp vào HTML: <img src="data:image/png;base64,..." />
     *
     * @param text   Nội dung cần encode vào QR (ví dụ: bookingCode)
     * @param width  Chiều rộng ảnh QR (px)
     * @param height Chiều cao ảnh QR (px)
     * @return Base64 string của ảnh PNG
     */
    public static String generateQrCodeBase64(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            byte[] qrBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(qrBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR code for: " + text, e);
        }
    }

    /**
     * Tạo QR code với kích thước mặc định 250x250
     */
    public static String generateQrCodeBase64(String text) {
        return generateQrCodeBase64(text, 250, 250);
    }
}
