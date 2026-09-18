package com.project.mobility.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardForm {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    private String content;
}
