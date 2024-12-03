package com.sparta.msa_exam.auth.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthUserResponse {

    private String userId;
    private String email;
    private String role;
    private boolean isValid;

}
