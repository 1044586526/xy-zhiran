package com.ruoyi.abuwxapi;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.abuwx.domain.DisasterReliefApplication;
import com.ruoyi.abuwx.dto.ResultDTO;
import com.ruoyi.abuwx.service.IDisasterReliefApplicationService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * author：AbuCoder QQ:932696181
 * 微信小程序申报信息接口
 * Gitee:https://gitee.com/rahman
 */
@RestController
@RequestMapping("/wxapi")
public class DisasterReliefController extends BaseController {

    @Autowired
    IDisasterReliefApplicationService iDisasterReliefApplicationService;

    @PostMapping("/shebao")
    public AjaxResult shebaoadd(@RequestBody DisasterReliefApplication disasterReliefApplication){
       int  bannerslist = iDisasterReliefApplicationService.insertDisasterReliefApplication(disasterReliefApplication);
        return AjaxResult.success("申报成功，请等待管理员审核");
    }
    @GetMapping("/shebao/public")
    public AjaxResult getPublicInfo(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "disasterType", required = false) String disasterType,
            @RequestParam(value = "regionCode", required = false) String regionCode,
            @RequestParam(value = "month", required = false) String month,
            @RequestParam(value = "status", defaultValue = "3") Integer status) {

        // 创建查询条件
        DisasterReliefApplication queryParam = new DisasterReliefApplication();
        queryParam.setStatus(status); // 设置状态筛选

        if (StringUtils.isNotEmpty(keyword)) {
            // 关键词搜索逻辑
        }

        if (StringUtils.isNotEmpty(disasterType)) {
            queryParam.setDisasterType(disasterType);
        }

        if (StringUtils.isNotEmpty(month)) {
            // 处理月份筛选
        }

        // 分页查询
        PageHelper.startPage(page, pageSize);
        List<DisasterReliefApplication> list = iDisasterReliefApplicationService.selectDisasterReliefApplicationList(queryParam);

        // 返回结果
        PageInfo<DisasterReliefApplication> pageInfo = new PageInfo<>(list);
        return AjaxResult.success(pageInfo);
    }
    @PostMapping("/query")
    public AjaxResult query(@RequestBody DisasterReliefApplication disasterReliefApplication) {
        // 获取查询条件
        String idCard = disasterReliefApplication.getIdCard();
        String phone = disasterReliefApplication.getPhone();
        String reportId = disasterReliefApplication.getReportId();

        // 根据提供的参数进行查询
        List<DisasterReliefApplication> applicationList = null;

        if (StringUtils.isNotEmpty(idCard)) {
            // 根据身份证号码查询
            applicationList = iDisasterReliefApplicationService.selectDisasterReliefApplicationByIdCard(idCard);
        } else if (StringUtils.isNotEmpty(phone)) {
            // 根据手机号码查询
            applicationList = iDisasterReliefApplicationService.selectDisasterReliefApplicationByPhone(phone);
        } else if (StringUtils.isNotEmpty(reportId)) {
            // 根据申报编号查询
            DisasterReliefApplication application = iDisasterReliefApplicationService.selectDisasterReliefApplicationByReportId(reportId);
            if (application != null) {
                applicationList = new ArrayList<>();
                applicationList.add(application);
            }
        } else {
            // 如果没有提供任何查询参数，返回错误信息
            return AjaxResult.error("请提供查询参数：身份证号码、手机号码或申报编号");
        }

        // 检查查询结果
        if (applicationList == null || applicationList.isEmpty()) {
            return AjaxResult.error("未查询到相关申报记录");
        }

        // 返回查询结果
        return AjaxResult.success("查询成功", applicationList);
    }
    @PostMapping("/shebao/list")
    public AjaxResult list(@RequestBody DisasterReliefApplication disasterReliefApplication){
        List<DisasterReliefApplication>  bannerslist = iDisasterReliefApplicationService.selectDisasterReliefApplicationList(disasterReliefApplication);
        return AjaxResult.success(bannerslist);
    }


    @PostMapping("/shebao/comment")
    public AjaxResult comment(@RequestBody DisasterReliefApplication disasterReliefApplication){
        List<DisasterReliefApplication>  bannerslist = iDisasterReliefApplicationService.selectDisasterReliefComment(disasterReliefApplication);
        return AjaxResult.success(bannerslist);
    }
    @GetMapping("/shebao/list/{id}")
    public AjaxResult listid(@PathVariable("id") Long id){
        DisasterReliefApplication  bannerslist = iDisasterReliefApplicationService.selectDisasterReliefApplicationById(id);
        return AjaxResult.success(bannerslist);
    }


    @PostMapping("/list/count")
    public ResultDTO count(){
        return iDisasterReliefApplicationService.count();
    }


    /**
     * 修改灾害救助申报
     */
    @PostMapping("/list/update")
    public AjaxResult edit(@RequestBody DisasterReliefApplication disasterReliefApplication)
    {
        return iDisasterReliefApplicationService.updateDisasterReliefApplicationRt(disasterReliefApplication);
    }


    /**
     * 概述
     */
    @PostMapping("/list/survey")
    public Map<String,Integer> survey()
    {
        return iDisasterReliefApplicationService.survey();
    }
}
