package com.fastcode.oidc.domain.model;

import java.util.Date;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokenverification")
@IdClass(TokenverificationId.class)
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class TokenverificationEntity extends AbstractEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "token_type", nullable = false, length = 256)
    private String tokenType;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "expiration_time", nullable = true)
    private Date expirationTime;

    @Basic
    @Column(name = "token", nullable = true, length = 512)
    private String token;

    @ManyToOne
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private UserEntity user;
}
