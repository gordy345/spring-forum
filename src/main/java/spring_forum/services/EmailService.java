package spring_forum.services;


public interface EmailService {
    void sendConfirmationEmail(String emailAddress, String token);
}
