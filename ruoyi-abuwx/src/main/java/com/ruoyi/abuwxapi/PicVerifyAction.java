package com.ruoyi.abuwxapi;


import com.ruoyi.abuwx.domain.UserInfo;
import com.ruoyi.abuwx.service.IUserInfoService;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.RandomValidateCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/wxapi")
@CrossOrigin(origins = "*")
public class PicVerifyAction {
    private final static Logger logger = LoggerFactory.getLogger(PicVerifyAction.class);
    @Autowired
    private IUserInfoService userInfoService;
    /**
     * 生成验证码
     */
    @RequestMapping(value = "/getVerify")
    public void getVerify(HttpServletRequest request, HttpServletResponse response) {

        try {

            //设置相应类型,告诉浏览器输出的内容为图片
            response.setContentType("image/jpeg");

            //设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);

            RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();

            randomValidateCode.getRandomCode(request, response);//输出验证码图片方法

        } catch (Exception e) {

            logger.error("获取验证码失败>>>>   ", e);

        }

    }

    /**
     * 校验验证码
     */
    @RequestMapping(value = "/checkVerify", method = RequestMethod.GET, headers = "Accept=application/json")
    public boolean checkVerify(String vaibleStr, HttpSession session) {
        try {
            //@RequestParam

            //从session中获取随机数
            //String inputStr = verifyInput;
            String inputStr = vaibleStr;


            //String randomNum = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
            Jedis jedis = new Jedis("127.0.0.1", 6379); //ip和端口号

            String randomNum = jedis.get("RANDOMCODEKEY"); //获取key=name的值
            jedis.close(); //关闭Jedis

            if (randomNum == null || "".equals(randomNum) || !randomNum.equalsIgnoreCase(inputStr)) {
                System.out.println("接受的随机数"+inputStr);
                System.out.println("生成的随机数"+randomNum);
                System.out.println("得得得得");
                return false;
            } else {
                System.out.println("验证码正确！！！！！！！！！！！！！！！！！！！！！！！");
                return true;
            }

        } catch (Exception e) {
            logger.error("验证码校验失败", e);
            System.out.println("哈哈哈哈哈哈哈哈");
            return false;
        }
    }
    @PostMapping("/login")
    public AjaxResult login(@RequestBody UserInfo userInfo) {
        List<UserInfo> list = userInfoService.selectUserInfoList(userInfo);
        for (int i = 0; i <list.size(); i++) {
            System.out.println(list.get(i).getIdCard());
            System.out.println(userInfo.getIdCard());
            if(list.get(i).getIdCard().equals(userInfo.getIdCard())){

                if(list.get(i).getLoginPassword().equals(userInfo.getLoginPassword())){

                    return AjaxResult.success(list);
                }else {
                    return AjaxResult.error(HttpStatus.UNAUTHORIZED, "密码错误");
                }
            }else {
              return AjaxResult.error(HttpStatus.UNAUTHORIZED, "账号不存在");
            }
        }
        return AjaxResult.success(list);
    }
    @PostMapping("/register")
    public AjaxResult register(@RequestBody UserInfo userInfo) {
    int userInfo1 = userInfoService.insertUserInfo(userInfo);
    if (userInfo1<0){
        return AjaxResult.error(HttpStatus.UNAUTHORIZED, "注册失败");
    }
    return AjaxResult.success("恭喜您，注册成功");
    }
}
