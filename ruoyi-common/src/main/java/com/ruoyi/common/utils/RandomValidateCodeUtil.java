package com.ruoyi.common.utils;


/**
 * @author 阿水
 * @create 2023-03-17 10:41
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomValidateCodeUtil {


    public static final String RANDOMCODEKEY = "RANDOMVALIDATECODEKEY";//放到session中的key
    private static final Logger logger = LoggerFactory.getLogger(RandomValidateCodeUtil.class);

    // private String randString = "0123456789";//随机产生只有数字的字符串 private String
    //private String randString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生只有字母的字符串
    private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生数字与字母组合的字符串

    private int width = 95;// 图片宽
    private int height = 25;// 图片高
    private int lineSize = 40;// 干扰线数量
    private int stringNum = 4;// 随机产生字符数量
    private Random random = new Random();

    /**
     * 获得字体
     */
    private Font getFont() {
        return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
    }

    /**
     * 获得颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc - 16);
        int g = fc + random.nextInt(bc - fc - 14);
        int b = fc + random.nextInt(bc - fc - 18);
        return new Color(r, g, b);
    }

    /**
     * 生成随机图片
     */
    public void getRandomCode(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始随机生成图片。。。。。。。。");

        HttpSession session = request.getSession();

        // BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        // 产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        Graphics g = image.getGraphics();

        g.fillRect(0, 0, width, height);//图片大小

        g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));//字体大小

        g.setColor(getRandColor(110, 133));//字体颜色

        // 绘制干扰线
        for (int i = 0; i <= lineSize; i++) {
            drawLine(g);
        }

        // 绘制随机字符
        String randomString = "";

        for (int i = 1; i <= stringNum; i++) {
            randomString = drawString(g, randomString, i);
        }

        // logger.info(randomString);
        // TODO: 2025/5/15  
        Jedis jedis = new Jedis("127.0.0.1", 6379); //ip和端口号
        jedis.set("RANDOMCODEKEY", randomString); //存入数据key=name；value=HelloWorld
        String value = jedis.get("RANDOMCODEKEY"); //获取key=name的值
        System.out.println("生成的数据value: " + value);


        //将生成的随机字符串保存到session中
        //session.removeAttribute(RANDOMCODEKEY);
        //session.setAttribute(RANDOMCODEKEY, randomString);
        g.dispose();

        try {
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", response.getOutputStream());
        } catch (Exception e) {
            logger.error("将内存中的图片通过流动形式输出到客户端失败>>>>   ", e);
        }
        System.out.println("随机生成图片完成！！");

    }

    /**
     * 绘制字符串
     */
    private String drawString(Graphics g, String randomString, int i) {
        g.setFont(getFont());
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(randString
                .length())));
        randomString += rand;
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return randomString;
    }

    /**
     * 绘制干扰线
     */
    private void drawLine(Graphics g) {

        int x = random.nextInt(width);
        int y = random.nextInt(height);

        int xl = random.nextInt(13);
        int yl = random.nextInt(15);

        g.drawLine(x, y, x + xl, y + yl);

    }

    /**
     * 获取随机的字符
     */
    public String getRandomString(int num) {
        return String.valueOf(randString.charAt(num));
    }
}



