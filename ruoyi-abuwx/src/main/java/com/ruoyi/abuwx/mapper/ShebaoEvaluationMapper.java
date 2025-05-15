package com.ruoyi.abuwx.mapper;

import java.util.List;
import com.ruoyi.abuwx.domain.ShebaoEvaluation;

/**
 * 社保民主评议记录Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface ShebaoEvaluationMapper 
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
     * 删除社保民主评议记录
     * 
     * @param id 社保民主评议记录主键
     * @return 结果
     */
    public int deleteShebaoEvaluationById(Long id);

    /**
     * 批量删除社保民主评议记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShebaoEvaluationByIds(Long[] ids);
}
