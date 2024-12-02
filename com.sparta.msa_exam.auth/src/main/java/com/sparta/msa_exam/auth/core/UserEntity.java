package com.sparta.msa_exam.auth.core;

import com.sparta.msa_exam.auth.auth.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email; // user_name 으로 컬럼이 들어간다.
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;


    public void setPassword(String password) {
        this.password = password;
    }
}
