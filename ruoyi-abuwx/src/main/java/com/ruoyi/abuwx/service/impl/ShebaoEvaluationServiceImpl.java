package com.ruoyi.abuwx.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.abuwx.mapper.ShebaoEvaluationMapper;
import com.ruoyi.abuwx.domain.ShebaoEvaluation;
import com.ruoyi.abuwx.service.IShebaoEvaluationService;

/**
 * 社保民主评议记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class ShebaoEvaluationServiceImpl implements IShebaoEvaluationService 
{
    @Autowired
    private ShebaoEvaluationMapper shebaoEvaluationMapper;

    /**
     * 查询社保民主评议记录
     * 
     * @param id 社保民主评议记录主键
     * @return 社保民主评议记录
     */
    @Override
    public ShebaoEvaluation selectShebaoEvaluationById(Long id)
    {
        return shebaoEvaluationMapper.selectShebaoEvaluationById(id);
    }

    /**
     * 查询社保民主评议记录列表
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 社保民主评议记录
     */
    @Override
    public List<ShebaoEvaluation> selectShebaoEvaluationList(ShebaoEvaluation shebaoEvaluation)
    {
        return shebaoEvaluationMapper.selectShebaoEvaluationList(shebaoEvaluation);
    }

    /**
     * 新增社保民主评议记录
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 结果
     */
    @Override
    public int insertShebaoEvaluation(ShebaoEvaluation shebaoEvaluation)
    {
        shebaoEvaluation.setCreateTime(DateUtils.getNowDate());
        return shebaoEvaluationMapper.insertShebaoEvaluation(shebaoEvaluation);
    }

    /**
     * 修改社保民主评议记录
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 结果
     */
    @Override
    public int updateShebaoEvaluation(ShebaoEvaluation shebaoEvaluation)
    {
        shebaoEvaluation.setUpdateTime(DateUtils.getNowDate());
        return shebaoEvaluationMapper.updateShebaoEvaluation(shebaoEvaluation);
    }

    /**
     * 批量删除社保民主评议记录
     * 
     * @param ids 需要删除的社保民主评议记录主键
     * @return 结果
     */
    @Override
    public int deleteShebaoEvaluationByIds(Long[] ids)
    {
        return shebaoEvaluationMapper.deleteShebaoEvaluationByIds(ids);
    }

    /**
     * 删除社保民主评议记录信息
     * 
     * @param id 社保民主评议记录主键
     * @return 结果
     */
    @Override
    public int deleteShebaoEvaluationById(Long id)
    {
        return shebaoEvaluationMapper.deleteShebaoEvaluationById(id);
    }
    @Override
    public boolean checkEvaluatorRole(Long userId) {
        // 根据您的业务逻辑实现，例如从用户表中查询角色
        // 这里仅作示例，您需要根据实际情况修改
        if (userId == null) {
            return false;
        }

        // 查询用户信息判断角色
        // UserInfo user = userInfoMapper.selectUserInfoById(userId);
        // return user != null && "reviewer".equals(user.getUserRole());

        // 简单示例：假设有评议员ID列表
        List<Long> evaluatorIds = Arrays.asList(1L, 2L, 3L); // 替换为实际的评议员ID
        return evaluatorIds.contains(userId);
    }

    @Override
    public Map<String, Object> getEvaluationStatistics(Long applicationId) {
        Map<String, Object> statistics = new HashMap<>();

        // 查询该申请的所有评议
        ShebaoEvaluation query = new ShebaoEvaluation();
        query.setApplicationId(applicationId);
        List<ShebaoEvaluation> evaluations = selectShebaoEvaluationList(query);

        // 计算统计数据
        int count = evaluations.size();
        BigDecimal totalScore = BigDecimal.ZERO; // 使用 BigDecimal 替代 double

        for (ShebaoEvaluation evaluation : evaluations) {
            if (evaluation != null && evaluation.getTotalScore() != null) {
                totalScore = totalScore.add(evaluation.getTotalScore()); // BigDecimal 相加
            }
        }

        // 计算平均分（仍使用 BigDecimal 保持精度）
        BigDecimal averageScore = count > 0
                ? totalScore.divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP)  // 保留1位小数，四舍五入
                : BigDecimal.ZERO;

        statistics.put("evaluationCount", count);
        statistics.put("averageScore", averageScore.toString()); // 直接返回 BigDecimal 的字符串表示

        return statistics;
    }

    @Override
    public boolean checkUserEvaluated(Long applicationId, Long evaluatorId) {
        if (applicationId == null || evaluatorId == null) {
            return false;
        }

        ShebaoEvaluation query = new ShebaoEvaluation();
        query.setApplicationId(applicationId);
        query.setEvaluatorId(String.valueOf(evaluatorId));

        List<ShebaoEvaluation> evaluations = selectShebaoEvaluationList(query);
        return evaluations != null && !evaluations.isEmpty();
    }
}
