package com.ruoyi.abuwx.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.abuwx.domain.ShebaoEvaluation;

/**
 * 社保民主评议记录Service接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface IShebaoEvaluationService 
{
    /**
     * 查询社保民主评议记录
     * 
     * @param id 社保民主评议记录主键
     * @return 社保民主评议记录
     */
    public ShebaoEvaluation selectShebaoEvaluationById(Long id);

    /**
     * 查询社保民主评议记录列表
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 社保民主评议记录集合
     */
    public List<ShebaoEvaluation> selectShebaoEvaluationList(ShebaoEvaluation shebaoEvaluation);

    /**
     * 新增社保民主评议记录
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 结果
     */
    public int insertShebaoEvaluation(ShebaoEvaluation shebaoEvaluation);

    /**
     * 修改社保民主评议记录
     * 
     * @param shebaoEvaluation 社保民主评议记录
     * @return 结果
     */
    public int updateShebaoEvaluation(ShebaoEvaluation shebaoEvaluation);

    /**
     * 批量删除社保民主评议记录
     * 
     * @param ids 需要删除的社保民主评议记录主键集合
     * @return 结果
     */
    public int deleteShebaoEvaluationByIds(Long[] ids);

    /**
     * 删除社保民主评议记录信息
     * 
     * @param id 社保民主评议记录主键
     * @return 结果
     */
    public int deleteShebaoEvaluationById(Long id);

    public boolean checkEvaluatorRole(Long userId);

    public Map<String, Object> getEvaluationStatistics(Long applicationId);

    public boolean checkUserEvaluated(Long applicationId, Long evaluatorId);
}
