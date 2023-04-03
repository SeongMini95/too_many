package com.toomany.domain.usertoken;

import com.toomany.domain.BaseTimeEntity;
import com.toomany.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "user_token")
public class UserToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token", nullable = false, length = 128)
    private String refreshToken;

    @Column(name = "expire_datetime", nullable = false)
    private LocalDateTime expireDatetime;

    @Builder
    public UserToken(User user, String refreshToken, LocalDateTime expireDatetime) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expireDatetime = expireDatetime;
    }

    public void reissue(String newRefreshToken, int maxAge) {
        this.refreshToken = newRefreshToken;
        this.expireDatetime = LocalDateTime.now().plusSeconds(maxAge);
    }
}