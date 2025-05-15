package com.ruoyi.common.utils;

import com.wf.captcha.SpecCaptcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class CustomSpecCaptcha extends SpecCaptcha {
    private Color backgroundColor = Color.WHITE; // 默认白色

    public CustomSpecCaptcha(int width, int height, int len) {
        super(width, height, len);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    private boolean graphicsImage(char[] strs, OutputStream out) {
        try {
            BufferedImage bi = new BufferedImage(this.width, this.height, 1);
            Graphics2D g2d = (Graphics2D)bi.getGraphics();
            // 修改这里使用自定义背景色
            g2d.setColor(backgroundColor != null ? backgroundColor : Color.WHITE);
            g2d.fillRect(0, 0, this.width, this.height);

            // 其余保持原样
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.drawOval(2, g2d);
            g2d.setStroke(new BasicStroke(2.0F, 0, 2));
            this.drawBesselLine(1, g2d);
            g2d.setFont(this.getFont());
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int fW = this.width / strs.length;
            int fSp = (fW - (int)fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;

            for(int i = 0; i < strs.length; ++i) {
                g2d.setColor(this.color());
                int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
                g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
            }

            g2d.dispose();
            ImageIO.write(bi, "png", out);
            out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}