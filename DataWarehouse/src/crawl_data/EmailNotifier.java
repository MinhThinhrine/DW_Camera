package crawl_data;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailNotifier {

    // Cấu hình email cố định
    private static final String SMTP_HOST = "smtp.gmail.com"; // Thay bằng SMTP server bạn dùng
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "21130549@st.hcmuaf.edu.vn"; // Email gửi
    private static final String EMAIL_PASSWORD = "nuum kgif bawc pncd";   // Mật khẩu email gửi
    private static final String EMAIL_TO = "thinh913011@gmail.com"; // Email nhận

    // Hàm gửi email thông báo thất bại
    public static void sendFailureNotification(String errorDetails) {
        String subject = "⚠️ Thông báo: Code chạy không thành công";
        String body = """
                <h2>Thông báo từ hệ thống</h2>
                <p><strong>Kết quả:</strong> <span style="color: red;">Thất bại</span></p>
                <p><strong>Chi tiết lỗi:</strong></p>
                <pre>%s</pre>
                <p>Vui lòng kiểm tra và xử lý ngay.</p>
                """.formatted(errorDetails);

        sendEmail(subject, body);
    }

    // Hàm gửi email thông báo thành công
    public static void sendSuccessNotification() {
        String subject = "✅ Thông báo: Code chạy thành công";
        String body = """
                <h2>Thông báo từ hệ thống</h2>
                <p><strong>Kết quả:</strong> <span style="color: green;">Thành công</span></p>
                <p>Hệ thống đã thực thi code thành công. Không phát sinh lỗi.</p>
                """;

        sendEmail(subject, body);
    }

    // Hàm gửi email chung
    private static void sendEmail(String subject, String body) {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(EMAIL_TO));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Email sent successfully to: " + EMAIL_TO);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email to: " + EMAIL_TO);
        }
    }

    // Ví dụ sử dụng
    public static void main(String[] args) {
        // Gửi email thất bại
        sendFailureNotification("Đã xảy ra lỗi kết nối với database.");

        // Gửi email thành công
        sendSuccessNotification();
    }
}
