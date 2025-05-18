package com.ruoyi.abuwx.service;

import java.util.List;
import com.ruoyi.abuwx.domain.DisasterReliefApplication;
import com.ruoyi.abuwx.dto.ResultDTO;

/**
 * 灾害救助申报Service接口
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public interface IDisasterReliefApplicationService 
{
    /**
     * 查询灾害救助申报
     * 
     * @param id 灾害救助申报主键
     * @return 灾害救助申报
     */
    public DisasterReliefApplication selectDisasterReliefApplicationById(Long id);


    /**
     * 查询灾害救助申报列表
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 灾害救助申报集合
     */
    public List<DisasterReliefApplication> selectDisasterReliefApplicationList(DisasterReliefApplication disasterReliefApplication);

    /**
     * 新增灾害救助申报
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 结果
     */
    public int insertDisasterReliefApplication(DisasterReliefApplication disasterReliefApplication);

    /**
     * 修改灾害救助申报
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 结果
     */
    public int updateDisasterReliefApplication(DisasterReliefApplication disasterReliefApplication);

    /**
     * 批量删除灾害救助申报
     * 
     * @param ids 需要删除的灾害救助申报主键集合
     * @return 结果
     */
    public int deleteDisasterReliefApplicationByIds(Long[] ids);

    /**
     * 删除灾害救助申报信息
     * 
     * @param id 灾害救助申报主键
     * @return 结果
     */
    public int deleteDisasterReliefApplicationById(Long id);

    List<DisasterReliefApplication> selectDisasterReliefApplicationByIdCard(String idCard);

    List<DisasterReliefApplication> selectDisasterReliefApplicationByPhone(String phone);

    DisasterReliefApplication selectDisasterReliefApplicationByReportId(String reportId);

    ResultDTO count();

}
