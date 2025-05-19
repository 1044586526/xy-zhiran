package com.ruoyi.abuwx.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.abuwx.domain.ShebaoEvaluation;
import com.ruoyi.abuwx.domain.UserInfo;
import com.ruoyi.abuwx.dto.ResultDTO;
import com.ruoyi.abuwx.mapper.ShebaoEvaluationMapper;
import com.ruoyi.abuwx.mapper.UserInfoMapper;
import com.ruoyi.common.core.domain.AjaxResult;
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

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private ShebaoEvaluationMapper shebaoEvaluationMapper;

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




    @Override
    public List<DisasterReliefApplication> selectDisasterReliefComment(DisasterReliefApplication disasterReliefApplication) {
        disasterReliefApplication.setStatus(null);
        return disasterReliefApplicationMapper.selectDisasterReliefApplicationList(disasterReliefApplication);
    }

    @Override
    public List<DisasterReliefApplication> commentSet(DisasterReliefApplication disasterReliefApplication) {
        List<DisasterReliefApplication> disasterReliefApplications = new ArrayList<>();
        disasterReliefApplication.setStatus(null);
        disasterReliefApplications = disasterReliefApplicationMapper.selectDisasterReliefApplicationList(disasterReliefApplication);
        if (CollectionUtils.isNotEmpty(disasterReliefApplications)){
            List<ShebaoEvaluation> shebaoEvaluations = shebaoEvaluationMapper.selectShebaoEvaluationList(new ShebaoEvaluation());
            return disasterReliefApplications.stream()
                    .filter(d ->d.getStatus()!= 2)
                    .peek(dis -> {
                List<ShebaoEvaluation> collect = shebaoEvaluations.stream()
                        .filter(s -> s.getApplicationId().equals(dis.getId()) && s.getEvaluatorId().equals(String.valueOf(disasterReliefApplication.getUserId())))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(collect)) {
                    // 已评议
                    dis.setIsComment(0);
                }else {
                    dis.setIsComment(1);
                }
            }).collect(Collectors.toList());
        }
        return disasterReliefApplications;
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
    public AjaxResult updateDisasterReliefApplicationRt(DisasterReliefApplication disasterReliefApplication)
    {
        // 评议操作
        if (Objects.isNull(disasterReliefApplication.getFlagComment())){
            ShebaoEvaluation shebaoEvaluation = new ShebaoEvaluation();
            shebaoEvaluation.setApplicationId(disasterReliefApplication.getId());
            shebaoEvaluation.setEvaluatorId(String.valueOf(disasterReliefApplication.getUserId()));
            List<ShebaoEvaluation> shebaoEvaluations = shebaoEvaluationMapper.selectShebaoEvaluationList(shebaoEvaluation);
            if (CollectionUtils.isEmpty(shebaoEvaluations)){
                return AjaxResult.error("请先进行评议操作");
            }
        }

        disasterReliefApplication.setUpdateTime(DateUtils.getNowDate());
        return AjaxResult.success(disasterReliefApplicationMapper.updateDisasterReliefApplication(disasterReliefApplication));
    }

    @Override
    public AjaxResult cancel(DisasterReliefApplication disasterReliefApplication)
    {

        // 删除评议记录

        disasterReliefApplication.setUpdateTime(DateUtils.getNowDate());
        return AjaxResult.success(disasterReliefApplicationMapper.updateDisasterReliefApplication(disasterReliefApplication));
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
        // 检查该用户是否已经评议
        ShebaoEvaluation shebaoEvaluation = new ShebaoEvaluation();

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
        List<UserInfo> userInfos = userInfoMapper.selectUserInfoList(null);
        List<UserInfo> infos = userInfos.stream().filter(u -> !u.getUserrole().equals("admin")).collect(Collectors.toList());
        Map<String, String> stringMap = infos.stream().collect(Collectors.toMap(UserInfo::getIdCard, UserInfo::getBankAccount));
        List<DisasterReliefApplication> disasterReliefApplications = disasterReliefApplicationMapper.listDis();
        if (CollectionUtils.isNotEmpty(disasterReliefApplications)){
            List<DisasterReliefApplication> collect = disasterReliefApplications.stream().peek(d -> {
                String s = stringMap.get(d.getIdCard());
                d.setCardNo(s);
            }).collect(Collectors.toList());
            resultDTO.setList(collect);
            BigDecimal totalRequestAmount = disasterReliefApplications.stream()
                    .map(DisasterReliefApplication::getRequestAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            resultDTO.setCount(totalRequestAmount);

        }
        return resultDTO;
    }

    @Override
    public Map<String, Integer> survey() {
        DisasterReliefApplication reliefApplication = new DisasterReliefApplication();
        Map<String,Integer> result = new HashMap<>();
        List<DisasterReliefApplication> disasterReliefApplications = disasterReliefApplicationMapper.selectDisasterReliefApplicationList(reliefApplication);
        // 评议
        List<ShebaoEvaluation> shebaoEvaluations = shebaoEvaluationMapper.selectShebaoEvaluationList(null);
        if (CollectionUtils.isNotEmpty(disasterReliefApplications)){
            result.put("all",disasterReliefApplications.size());
            result.put("success", (int) disasterReliefApplications.stream().filter(d -> d.getStatus() == 1).count());
            result.put("fault", (int) disasterReliefApplications.stream().filter(d -> d.getStatus() == 2).count());
            result.put("pending", (int) disasterReliefApplications.stream().filter(d -> d.getStatus() == 0).count());
            result.put("issued", (int) disasterReliefApplications.stream().filter(d -> d.getStatus() == 3).count());
            if (CollectionUtils.isNotEmpty(shebaoEvaluations)){
                List<Long> collect = shebaoEvaluations.stream().map(ShebaoEvaluation::getApplicationId).collect(Collectors.toList());
                result.put("comment", (int) disasterReliefApplications.stream()
                        .filter(d -> d.getStatus() == 0 && !collect.contains(d.getId())).count());
            }
        }
        return result;
    }


}
