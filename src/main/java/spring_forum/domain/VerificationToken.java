package spring_forum.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "verification_token")
public class VerificationToken extends BaseEntity {

    private static final int EXPIRATION = 60 * 24;

    @Column(name = "value")
    private String value;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    private Date expiryDate;

    public VerificationToken(final String token, final User user) {
        this.value = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }


}
