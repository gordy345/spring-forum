package spring_forum.services;

import spring_forum.domain.User;
import spring_forum.domain.VerificationToken;

public interface VerificationTokenService {

    String createNewTokenForUser(User user);

    VerificationToken findTokenByValue(String tokenVal);

    void deleteTokenByValue(String tokenVal);
}
