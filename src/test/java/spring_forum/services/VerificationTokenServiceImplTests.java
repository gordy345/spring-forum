package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.VerificationToken;
import spring_forum.exceptions.TokenExpiredException;
import spring_forum.repositories.VerificationTokenRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static spring_forum.TestConstants.USER;
import static spring_forum.TestConstants.VERIFICATION_TOKEN;
import static spring_forum.utils.ExceptionMessages.TOKEN_EXPIRED;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceImplTests {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    private VerificationTokenService verificationTokenService;

    @BeforeEach
    void setUp() {
        verificationTokenService = new VerificationTokenServiceImpl(verificationTokenRepository);
    }

    @Test
    void createNewTokenForUser() {
        String createdToken = verificationTokenService.createNewTokenForUser(USER);
        assertNotNull(createdToken);
        verify(verificationTokenRepository).save(any(VerificationToken.class));
    }

    @Test
    void findTokenByValue() {
        when(verificationTokenRepository.findVerificationTokenByValue(anyString())).thenReturn(VERIFICATION_TOKEN);
        VerificationToken tokenByValue =
                verificationTokenService.findTokenByValue(VERIFICATION_TOKEN.getValue());
        assertEquals(tokenByValue.getValue(), VERIFICATION_TOKEN.getValue());
        assertEquals(tokenByValue.getUser(), VERIFICATION_TOKEN.getUser());
        verify(verificationTokenRepository).findVerificationTokenByValue(anyString());
    }

    @Test
    void deleteTokenByValue() {
        verificationTokenService.deleteTokenByValue(VERIFICATION_TOKEN.getValue());
        verify(verificationTokenRepository).deleteVerificationTokenByValue(anyString());
    }

    @Test
    void findTokenByValueWithError() {
        when(verificationTokenRepository.findVerificationTokenByValue(anyString())).thenReturn(null);
        TokenExpiredException exception = assertThrows(TokenExpiredException.class,
                () -> verificationTokenService.findTokenByValue(VERIFICATION_TOKEN.getValue()));
        assertEquals(TOKEN_EXPIRED, exception.getMessage());
        verify(verificationTokenRepository).findVerificationTokenByValue(anyString());
    }
}