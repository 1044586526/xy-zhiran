package com.ruoyi.abuwxapi;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * author：AbuCoder QQ:932696181
 * 微信小程序获取通知公告接口
 * Gitee:https://gitee.com/rahman
 */
@RestController
@RequestMapping("/wxapi")
public class WxNoticeController {

    @Autowired
    ISysNoticeService iNoticeService;
    @GetMapping("/loadNotice")
    public AjaxResult loadNotice(){
        List<SysNotice> notices = iNoticeService.selectAllNotice();
        return AjaxResult.success(notices);
    }
}
