package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.VerificationToken;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    VerificationToken findVerificationTokenByValue(String token);

    void deleteVerificationTokenByValue(String tokenVal);
}
