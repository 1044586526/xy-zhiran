package com.ruoyi.abuwx.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.abuwx.dto.ResultDTO;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.abuwx.mapper.DisasterReliefApplicationMapper;
import com.ruoyi.abuwx.domain.DisasterReliefApplication;
import com.ruoyi.abuwx.service.IDisasterReliefApplicationService;

/**
 * 灾害救助申报Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
@Service
public class DisasterReliefApplicationServiceImpl implements IDisasterReliefApplicationService 
{
    @Autowired
    private DisasterReliefApplicationMapper disasterReliefApplicationMapper;

    /**
     * 查询灾害救助申报
     * 
     * @param id 灾害救助申报主键
     * @return 灾害救助申报
     */
    @Override
    public DisasterReliefApplication selectDisasterReliefApplicationById(Long id)
    {
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationById(id);
    }



    /**
     * 查询灾害救助申报列表
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 灾害救助申报
     */
    @Override
    public List<DisasterReliefApplication> selectDisasterReliefApplicationList(DisasterReliefApplication disasterReliefApplication)
    {
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationList(disasterReliefApplication);
    }

    /**
     * 新增灾害救助申报
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 结果
     */
    @Override
    public int insertDisasterReliefApplication(DisasterReliefApplication disasterReliefApplication)
    {
        Long maxId = disasterReliefApplicationMapper.selectMaxReportId();
        if (maxId == null) {
            maxId = 1L;
        } else {
            maxId += 1L;
        }
        disasterReliefApplication.setReportId(String.valueOf(maxId));
        disasterReliefApplication.setCreateTime(DateUtils.getNowDate());
        return disasterReliefApplicationMapper.insertDisasterReliefApplication(disasterReliefApplication);
    }

    /**
     * 修改灾害救助申报
     * 
     * @param disasterReliefApplication 灾害救助申报
     * @return 结果
     */
    @Override
    public int updateDisasterReliefApplication(DisasterReliefApplication disasterReliefApplication)
    {
        disasterReliefApplication.setUpdateTime(DateUtils.getNowDate());
        return disasterReliefApplicationMapper.updateDisasterReliefApplication(disasterReliefApplication);
    }

    /**
     * 批量删除灾害救助申报
     * 
     * @param ids 需要删除的灾害救助申报主键
     * @return 结果
     */
    @Override
    public int deleteDisasterReliefApplicationByIds(Long[] ids)
    {
        return disasterReliefApplicationMapper.deleteDisasterReliefApplicationByIds(ids);
    }

    /**
     * 删除灾害救助申报信息
     * 
     * @param id 灾害救助申报主键
     * @return 结果
     */
    @Override
    public int deleteDisasterReliefApplicationById(Long id)
    {
        return disasterReliefApplicationMapper.deleteDisasterReliefApplicationById(id);
    }
    /**
     * 根据身份证号码查询申报记录
     *
     * @param idCard 身份证号码
     * @return 申报记录列表
     */
    @Override
    public List<DisasterReliefApplication> selectDisasterReliefApplicationByIdCard(String idCard) {
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationByIdCard(idCard);
    }

    /**
     * 根据手机号码查询申报记录
     *
     * @param phone 手机号码
     * @return 申报记录列表
     */
    @Override
    public List<DisasterReliefApplication> selectDisasterReliefApplicationByPhone(String phone) {
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationByPhone(phone);
    }

    /**
     * 根据申报编号查询申报记录
     *
     * @param reportId 申报编号
     * @return 申报记录
     */
    @Override
    public DisasterReliefApplication selectDisasterReliefApplicationByReportId(String reportId) {
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationByReportId(reportId);
    }


    @Override
    public ResultDTO count() {
        ResultDTO resultDTO = new ResultDTO();
        List<DisasterReliefApplication> disasterReliefApplications = disasterReliefApplicationMapper.listDis();
        if (CollectionUtils.isNotEmpty(disasterReliefApplications)){
            resultDTO.setList(disasterReliefApplications);
            BigDecimal totalRequestAmount = disasterReliefApplications.stream()
                    .map(DisasterReliefApplication::getRequestAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            resultDTO.setCount(totalRequestAmount);

        }
        return resultDTO;
    }





}
