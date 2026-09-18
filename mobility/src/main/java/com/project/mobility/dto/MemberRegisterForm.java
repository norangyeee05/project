package com.project.mobility.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterForm {

    @NotBlank(message = "아이디를 확인해주세요")
    @Size(min=4, max=50)
    private String userid;

    @NotBlank(message = "비밀번호르 확인해주세요")
    @Size(min=8, max=100)
    private String password;

    @NotBlank(message = "성함을 확인해주세요")
    @Size(max=50)
    private String name;

    @Size(max=30)
    private String tel;

    @NotBlank(message = "이메일을 확인해주세요")
    @Email
    @Size(max=120)
    private String email;
}
