package com.ruoyi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    private String token;        // 验证码token
    private String imageBase64;  // base64格式的图片数据
}