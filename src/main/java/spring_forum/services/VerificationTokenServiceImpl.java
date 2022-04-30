package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.User;
import spring_forum.domain.VerificationToken;
import spring_forum.exceptions.NotFoundException;
import spring_forum.repositories.VerificationTokenRepository;

import javax.transaction.Transactional;
import java.util.UUID;

import static spring_forum.utils.ExceptionMessages.TOKEN_EXPIRED;

@Slf4j
@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    @Transactional
    public String createNewTokenForUser(User user) {
        log.info("Creating new token for user with ID = " + user.getId());
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    @Transactional
    public VerificationToken findTokenByValue(String tokenVal) {
        log.info("Finding token entity with token value = " + tokenVal);
        VerificationToken token =
                verificationTokenRepository.findVerificationTokenByValue(tokenVal);
        if (token == null) {
            throw new NotFoundException(TOKEN_EXPIRED);
        }
        return token;
    }

    @Override
    @Transactional
    public void deleteTokenByValue(String tokenVal) {
        log.info("Deleting token with value: " + tokenVal);
        verificationTokenRepository.deleteVerificationTokenByValue(tokenVal);
    }
}
