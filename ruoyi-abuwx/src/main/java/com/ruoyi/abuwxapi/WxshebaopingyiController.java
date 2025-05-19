package com.ruoyi.abuwxapi;

import com.ruoyi.abuwx.domain.ShebaoEvaluation;
import com.ruoyi.abuwx.domain.UserInfo;
import com.ruoyi.abuwx.service.IShebaoEvaluationService;
import com.ruoyi.abuwx.service.IUserInfoService;
import com.ruoyi.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * author：AbuCoder QQ:932696181
 * 灾情救助民主评议接口
 * Gitee:https://gitee.com/rahman
 */
@RestController
@RequestMapping("/wxapi/shebao/evaluation")
public class WxshebaopingyiController {

    @Autowired
    private IShebaoEvaluationService shebaoEvaluationService;

    /**
     * 查询评议列表
     */
    @GetMapping("/list")
    public AjaxResult list(ShebaoEvaluation shebaoEvaluation) {
        List<ShebaoEvaluation> evaluationList = shebaoEvaluationService.selectShebaoEvaluationList(shebaoEvaluation);
        return AjaxResult.success(evaluationList);
    }

    /**
     * 获取评议历史记录
     */
    @GetMapping("/history/{applicationId}")
    public AjaxResult getEvaluationHistory(@PathVariable Long applicationId) {
        ShebaoEvaluation query = new ShebaoEvaluation();
        query.setApplicationId(applicationId);
        List<ShebaoEvaluation> evaluations = shebaoEvaluationService.selectShebaoEvaluationList(query);
        return AjaxResult.success(evaluations);
    }

    /**
     * 添加评议
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ShebaoEvaluation shebaoEvaluation) {
        shebaoEvaluation.setEvaluationTime(new Date());
        return shebaoEvaluationService.insertShebaoEvaluation(shebaoEvaluation);
    }

    /**
     * 检查用户评议员角色
     */
    @GetMapping("/check-role")
    public AjaxResult checkRole(@RequestParam(required = false) Long userId) {
        // 这里应该根据您的业务逻辑检查用户是否是评议员
        // 假设您有一个评议员角色表，需要在Service层实现此逻辑
        Map<String, Object> result = new HashMap<>();
        boolean isEvaluator = shebaoEvaluationService.checkEvaluatorRole(userId);
        result.put("role", isEvaluator ? "reviewer" : "user");
        return AjaxResult.success(result);
    }

    /**
     * 获取用户已评议的申请列表
     */
    @GetMapping("/user-evaluated/{evaluatorId}")
    public AjaxResult getUserEvaluatedApplications(@PathVariable Long evaluatorId) {
        ShebaoEvaluation query = new ShebaoEvaluation();
        query.setEvaluatorId(String.valueOf(evaluatorId));
        List<ShebaoEvaluation> evaluations = shebaoEvaluationService.selectShebaoEvaluationList(query);
        return AjaxResult.success(evaluations);
    }

    /**
     * 获取某个申请的评议统计
     */
    @GetMapping("/statistics/{applicationId}")
    public AjaxResult getEvaluationStatistics(@PathVariable Long applicationId) {
        Map<String, Object> statistics = shebaoEvaluationService.getEvaluationStatistics(applicationId);
        return AjaxResult.success(statistics);
    }

    /**
     * 检查用户是否已评议某申请
     */
    @GetMapping("/check")
    public AjaxResult checkEvaluated(@RequestParam Long applicationId, @RequestParam Long evaluatorId) {
        boolean hasEvaluated = shebaoEvaluationService.checkUserEvaluated(applicationId, evaluatorId);
        Map<String, Object> result = new HashMap<>();
        result.put("hasEvaluated", hasEvaluated);
        return AjaxResult.success(result);
    }
}