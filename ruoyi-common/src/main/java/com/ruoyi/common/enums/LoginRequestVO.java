package com.ruoyi.common.enums;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequestVO {
    @NotBlank(message = "身份证号不能为空")
    private String idNumber;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "验证码token不能为空")
    private String captchaToken;
}
