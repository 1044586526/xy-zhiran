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
import com.ruoyi.abuwx.domain.ShebaoEvaluation;
import com.ruoyi.abuwx.service.IShebaoEvaluationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 社保民主评议记录Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/abuwx/evaluation")
public class ShebaoEvaluationController extends BaseController
{
    @Autowired
    private IShebaoEvaluationService shebaoEvaluationService;

    /**
     * 查询社保民主评议记录列表
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShebaoEvaluation shebaoEvaluation)
    {
        startPage();
        List<ShebaoEvaluation> list = shebaoEvaluationService.selectShebaoEvaluationList(shebaoEvaluation);
        return getDataTable(list);
    }

    /**
     * 导出社保民主评议记录列表
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:export')")
    @Log(title = "社保民主评议记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ShebaoEvaluation shebaoEvaluation)
    {
        List<ShebaoEvaluation> list = shebaoEvaluationService.selectShebaoEvaluationList(shebaoEvaluation);
        ExcelUtil<ShebaoEvaluation> util = new ExcelUtil<ShebaoEvaluation>(ShebaoEvaluation.class);
        util.exportExcel(response, list, "社保民主评议记录数据");
    }

    /**
     * 获取社保民主评议记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(shebaoEvaluationService.selectShebaoEvaluationById(id));
    }

    /**
     * 新增社保民主评议记录
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:add')")
    @Log(title = "社保民主评议记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ShebaoEvaluation shebaoEvaluation)
    {
        return shebaoEvaluationService.insertShebaoEvaluation(shebaoEvaluation);
    }

    /**
     * 修改社保民主评议记录
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:edit')")
    @Log(title = "社保民主评议记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ShebaoEvaluation shebaoEvaluation)
    {
        return toAjax(shebaoEvaluationService.updateShebaoEvaluation(shebaoEvaluation));
    }

    /**
     * 删除社保民主评议记录
     */
    @PreAuthorize("@ss.hasPermi('abuwx:evaluation:remove')")
    @Log(title = "社保民主评议记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(shebaoEvaluationService.deleteShebaoEvaluationByIds(ids));
    }
}
