package com.ruoyi.abuwx.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.abuwx.domain.DisasterReliefApplication;
import com.ruoyi.abuwx.service.IDisasterReliefApplicationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 灾害救助申报Controller
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
@RestController
@RequestMapping("/abuwx/application")
public class DisasterReliefApplicationController extends BaseController
{
    @Autowired
    private IDisasterReliefApplicationService disasterReliefApplicationService;

    /**
     * 查询灾害救助申报列表
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:list')")
    @GetMapping("/list")
    public TableDataInfo list(DisasterReliefApplication disasterReliefApplication)
    {
        startPage();
        List<DisasterReliefApplication> list = disasterReliefApplicationService.selectDisasterReliefApplicationList(disasterReliefApplication);
        return getDataTable(list);
    }

    /**
     * 导出灾害救助申报列表
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:export')")
    @Log(title = "灾害救助申报", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DisasterReliefApplication disasterReliefApplication)
    {
        List<DisasterReliefApplication> list = disasterReliefApplicationService.selectDisasterReliefApplicationList(disasterReliefApplication);
        ExcelUtil<DisasterReliefApplication> util = new ExcelUtil<DisasterReliefApplication>(DisasterReliefApplication.class);
        util.exportExcel(response, list, "灾害救助申报数据");
    }

    /**
     * 获取灾害救助申报详细信息
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(disasterReliefApplicationService.selectDisasterReliefApplicationById(id));
    }

    /**
     * 新增灾害救助申报
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:add')")
    @Log(title = "灾害救助申报", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DisasterReliefApplication disasterReliefApplication)
    {
        return toAjax(disasterReliefApplicationService.insertDisasterReliefApplication(disasterReliefApplication));
    }

    /**
     * 修改灾害救助申报
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:edit')")
    @Log(title = "灾害救助申报", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DisasterReliefApplication disasterReliefApplication)
    {
        return toAjax(disasterReliefApplicationService.updateDisasterReliefApplication(disasterReliefApplication));
    }

    /**
     * 删除灾害救助申报
     */
    @PreAuthorize("@ss.hasPermi('abuwx:application:remove')")
    @Log(title = "灾害救助申报", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(disasterReliefApplicationService.deleteDisasterReliefApplicationByIds(ids));
    }
}
