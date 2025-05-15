package com.ruoyi.abuwxapi;

import com.ruoyi.abuwx.domain.AbucoderBanners;
import com.ruoyi.abuwx.domain.UserInfo;
import com.ruoyi.abuwx.service.IAbucoderBannersService;
import com.ruoyi.abuwx.service.IUserInfoService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * author：AbuCoder QQ:932696181
 * 微信小程序获取轮播图接口
 * Gitee:https://gitee.com/rahman
 */
@RestController
@RequestMapping("/wxapi")
public class WxuserController {

    @Autowired
    IUserInfoService iUserInfoService;

    /**
     * 查询用户列表
     */
    @GetMapping("/user/query")
    public AjaxResult query( UserInfo userInfo) {
        userInfo = new UserInfo(); // 重置查询条件
        List<UserInfo> userList = iUserInfoService.selectUserInfoList(userInfo);
        return AjaxResult.success(userList);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/user/get/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId) {
        return AjaxResult.success(iUserInfoService.selectUserInfoById(userId));
    }

    /**
     * 新增用户
     */
    @PostMapping("/user/add")
    public AjaxResult add(@RequestBody UserInfo userInfo) {
        // 设置默认密码加密等操作
        String defaultPassword = "123456";
        // 实际应用中应该对密码进行加密处理
        userInfo.setLoginPassword(defaultPassword);
        userInfo.setCreateTime(new Date());

        return AjaxResult.success(iUserInfoService.insertUserInfo(userInfo));
    }

    /**
     * 修改用户
     */
    @PutMapping("/user/edit")
    public AjaxResult edit(@RequestBody UserInfo userInfo) {
        // 避免修改密码，从数据库中获取原密码
        UserInfo dbUserInfo = iUserInfoService.selectUserInfoById(userInfo.getId());
        // 保持密码不变
        userInfo.setLoginPassword(dbUserInfo.getLoginPassword());
        userInfo.setUpdateTime(new Date());

        return AjaxResult.success(iUserInfoService.updateUserInfo(userInfo));
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/user/delete/{userId}")
    public AjaxResult remove(@PathVariable Long userId) {
        return AjaxResult.success(iUserInfoService.deleteUserInfoById(userId));
    }
}
