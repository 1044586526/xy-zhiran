package com.ruoyi.abuwx.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 社保民主评议记录对象 shebao_evaluation
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public class ShebaoEvaluation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评议记录ID */
    private Long id;

    /** 申请ID */
    @Excel(name = "申请ID")
    private Long applicationId;

    /** 评议人ID */
    @Excel(name = "评议人ID")
    private String evaluatorId;

    /** 评议人姓名 */
    @Excel(name = "评议人姓名")
    private String evaluatorName;

    /** 真实性评分(1-5分) */
    @Excel(name = "真实性评分(1-5分)")
    private Long truthScore;

    /** 严重程度评分(1-5分) */
    @Excel(name = "严重程度评分(1-5分)")
    private Long severityScore;

    /** 救助必要性评分(1-5分) */
    @Excel(name = "救助必要性评分(1-5分)")
    private Long necessityScore;

    /** 总评分 */
    @Excel(name = "总评分")
    private BigDecimal totalScore;

    /** 评议意见 */
    @Excel(name = "评议意见")
    private String opinion;

    /** 评议时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评议时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date evaluationTime;

    /** 状态：0-无效，1-有效 */
    @Excel(name = "状态：0-无效，1-有效")
    private Integer status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setApplicationId(Long applicationId) 
    {
        this.applicationId = applicationId;
    }

    public Long getApplicationId() 
    {
        return applicationId;
    }
    public void setEvaluatorId(String evaluatorId) 
    {
        this.evaluatorId = evaluatorId;
    }

    public String getEvaluatorId() 
    {
        return evaluatorId;
    }
    public void setEvaluatorName(String evaluatorName) 
    {
        this.evaluatorName = evaluatorName;
    }

    public String getEvaluatorName() 
    {
        return evaluatorName;
    }
    public void setTruthScore(Long truthScore) 
    {
        this.truthScore = truthScore;
    }

    public Long getTruthScore() 
    {
        return truthScore;
    }
    public void setSeverityScore(Long severityScore) 
    {
        this.severityScore = severityScore;
    }

    public Long getSeverityScore() 
    {
        return severityScore;
    }
    public void setNecessityScore(Long necessityScore) 
    {
        this.necessityScore = necessityScore;
    }

    public Long getNecessityScore() 
    {
        return necessityScore;
    }
    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }
    public void setOpinion(String opinion) 
    {
        this.opinion = opinion;
    }

    public String getOpinion() 
    {
        return opinion;
    }
    public void setEvaluationTime(Date evaluationTime) 
    {
        this.evaluationTime = evaluationTime;
    }

    public Date getEvaluationTime() 
    {
        return evaluationTime;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("applicationId", getApplicationId())
            .append("evaluatorId", getEvaluatorId())
            .append("evaluatorName", getEvaluatorName())
            .append("truthScore", getTruthScore())
            .append("severityScore", getSeverityScore())
            .append("necessityScore", getNecessityScore())
            .append("totalScore", getTotalScore())
            .append("opinion", getOpinion())
            .append("evaluationTime", getEvaluationTime())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
