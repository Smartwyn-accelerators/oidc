package com.fastcode.oidc.domain.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenverificationId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tokenType;
    private Long id;

    public TokenverificationId(String tokenType, Long id) {
        this.tokenType = tokenType;
        this.id = id;
    }
}
