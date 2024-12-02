package com.sparta.msa_exam.gateway.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {

    private String email;
    private String role;
    private boolean isValid;

}
