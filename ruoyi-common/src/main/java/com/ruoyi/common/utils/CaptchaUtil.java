package com.ruoyi.common.utils;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class CaptchaUtil {

    @Value("${captcha.width}")
    private int width;

    @Value("${captcha.height}")
    private int height;

    @Value("${captcha.length}")
    private int length;

    /**
     * 生成验证码
     */
    public Captcha generateCaptcha() {
        // 创建验证码对象
        CustomSpecCaptcha captcha = new CustomSpecCaptcha(width, height, length);

        // 设置验证码字体
        captcha.setFont(new Font("Verdana", Font.BOLD, 28));

        // 设置类型，纯数字、纯字母、数字+字母
        captcha.setCharType(Captcha.TYPE_DEFAULT);

        // 正确设置背景色方法
        captcha.setBackgroundColor(new Color(248, 229, 238));

        return captcha;
    }
}