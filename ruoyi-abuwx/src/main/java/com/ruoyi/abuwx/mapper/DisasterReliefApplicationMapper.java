package com.ruoyi.abuwx.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.abuwx.domain.DisasterReliefApplication;

/**
 * 灾害救助申报Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public interface DisasterReliefApplicationMapper  extends BaseMapper<DisasterReliefApplication>
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
     * 删除灾害救助申报
     * 
     * @param id 灾害救助申报主键
     * @return 结果
     */
    public int deleteDisasterReliefApplicationById(Long id);


    /**
     * 批量删除灾害救助申报
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDisasterReliefApplicationByIds(Long[] ids);

    Long selectMaxReportId();

    List<DisasterReliefApplication> selectDisasterReliefApplicationByIdCard(String idCard);

    List<DisasterReliefApplication> selectDisasterReliefApplicationByPhone(String phone);

    List<DisasterReliefApplication> listDis();

    DisasterReliefApplication selectDisasterReliefApplicationByReportId(String reportId);
}
