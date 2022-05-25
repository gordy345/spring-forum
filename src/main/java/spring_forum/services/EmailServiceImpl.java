package spring_forum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import spring_forum.utils.Secret;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendConfirmationEmail(String emailAddress, String token) {
        log.info("Sending confirmation email to address: " + emailAddress);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailAddress);
        message.setSubject("Complete Registration on our Forum!");
        message.setFrom(Secret.getEmailUsernameSMTP());
        message.setText("To confirm your account, please click here: "
                + "http://localhost:8080/users/confirm-account?token=" + token);
        javaMailSender.send(message);
    }
}
