package com.invoice.invoice.service;

import java.util.Properties;
import java.util.Random;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailService {
    
    // ===== UPDATE THESE WITH YOUR GMAIL CREDENTIALS =====
    private static final String FROM_EMAIL = "jeeva8work@gmail.com";
    private static final String PASSWORD = "rzky pigk dhii pxmj";
    
    private final Session session;
    private final ConcurrentHashMap<String, OtpData> otpStore = new ConcurrentHashMap<>();
    
    private static class OtpData {
        String otp;
        long timestamp;
        int attempts;
        
        OtpData(String otp) {
            this.otp = otp;
            this.timestamp = System.currentTimeMillis();
            this.attempts = 0;
        }
    }
    
    public EmailService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        
        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }
    
    public void sendEmail(String toEmail, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            
            Transport.send(message);
            System.out.println("Email sent to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Email failed to " + toEmail + ": " + e.getMessage());
        }
    }
    
    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, new OtpData(otp));
        
        String subject = "M.R.GRAY BOARDS - Password Reset OTP";
        String body = "Dear User,\n\n"
            + "Your OTP for password reset is: " + otp + "\n\n"
            + "This OTP is valid for 5 minutes.\n\n"
            + "If you did not request this, please ignore this email.\n\n"
            + "Regards,\nM.R.GRAY BOARDS Team";
        
        sendEmail(email, subject, body);
        return otp;
    }
    
    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStore.get(email);
        if (data == null) {
            return false;
        }
        
        long elapsed = System.currentTimeMillis() - data.timestamp;
        if (elapsed > 5 * 60 * 1000) {
            otpStore.remove(email);
            return false;
        }
        
        if (data.attempts >= 3) {
            otpStore.remove(email);
            return false;
        }
        
        if (data.otp.equals(otp)) {
            otpStore.remove(email);
            return true;
        }
        
        data.attempts++;
        return false;
    }
    
    public void sendPasswordChangedConfirmation(String email) {
        String subject = "M.R.GRAY BOARDS - Password Changed Successfully";
        String body = "Dear User,\n\n"
            + "Your password has been changed.\n\n"
            + "If you did not make this change, please contact support immediately.\n\n"
            + "Regards,\nM.R.GRAY BOARDS Team";
        
        sendEmail(email, subject, body);
    }
}

/*
 * ===== HOW TO GET GMAIL APP PASSWORD =====
 * 
 * 1. Go to your Google Account -> myaccount.google.com
 * 2. Click "Security"
 * 3. Under "How you sign in to Google" -> enable 2-Step Verification
 * 4. Search "App Passwords" in the search bar
 * 5. Select App = "Mail", Device = "Other" -> type "InvoiceSystem"
 * 6. Click Generate -> copy the 16-character password
 * 7. Paste that password in PASSWORD field above
 */
