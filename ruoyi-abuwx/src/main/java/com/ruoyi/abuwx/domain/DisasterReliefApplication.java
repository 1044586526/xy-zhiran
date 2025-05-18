package com.ruoyi.abuwx.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 灾害救助申报对象 disaster_relief_application
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public class DisasterReliefApplication extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申报ID */
    private Long id;

    /** 申报编号 */
    @Excel(name = "申报编号")
    private String reportId;

    /** 申报人姓名 */
    @Excel(name = "申报人姓名")
    private String name;

    /** 身份证号码 */
    @Excel(name = "身份证号码")
    private String idCard;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String address;

    /** 家庭人口数 */
    @Excel(name = "家庭人口数")
    private Long familySize;

    /** 灾害类型(flood,earthquake,drought,typhoon,landslide,fire,other) */
    @Excel(name = "灾害类型(flood,earthquake,drought,typhoon,landslide,fire,other)")
    private String disasterType;

    /** 灾害发生时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "灾害发生时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date disasterDate;

    /** 灾害详情描述 */
    @Excel(name = "灾害详情描述")
    private String disasterDesc;

    /** 损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other) */
    @Excel(name = "损失类型列表(JSON格式,包含house,crop,livestock,belongings,business,other)")
    private String lossTypes;

    /** 房屋损失情况(collapse,severe,moderate,slight) */
    @Excel(name = "房屋损失情况(collapse,severe,moderate,slight)")
    private String houseDamage;

    /** 农作物受灾面积(亩) */
    @Excel(name = "农作物受灾面积(亩)")
    private BigDecimal cropArea;

    /** 预估经济损失(元) */
    @Excel(name = "预估经济损失(元)")
    private BigDecimal estimatedLoss;

    /** 损失详细说明 */
    @Excel(name = "损失详细说明")
    private String lossDesc;

    /** 救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other) */
    @Excel(name = "救助类型列表(JSON格式,包含fund,material,housing,medical,employment,other)")
    private String reliefTypes;

    /** 申请救助金额(元) */
    @Excel(name = "申请救助金额(元)")
    private BigDecimal requestAmount;

    /** 物资需求详情 */
    @Excel(name = "物资需求详情")
    private String materialNeeds;

    /** 其他救助需求 */
    @Excel(name = "其他救助需求")
    private String otherNeeds;

    /** 佐证材料文件信息(JSON格式,包含name,url,size) */
    @Excel(name = "佐证材料文件信息(JSON格式,包含name,url,size)")
    private String evidenceFiles;

    /** 佐证材料说明 */
    @Excel(name = "佐证材料说明")
    private String evidenceDesc;

    /** 申报状态:0待审核,1审核通过,2审核拒绝,3已发放 */
    @Excel(name = "申报状态:0待审核,1审核通过,2审核拒绝,3已发放")
    private Integer status;

    /** 审核人ID */
    @Excel(name = "审核人ID")
    private Long auditUserId;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date auditTime;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String auditRemark;

    /** 申报人声明确认 */
    @Excel(name = "申报人声明确认")
    private Integer declaration;


    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号")
    private String cardNo;


    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setReportId(String reportId) 
    {
        this.reportId = reportId;
    }

    public String getReportId() 
    {
        return reportId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard() 
    {
        return idCard;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }
    public void setFamilySize(Long familySize) 
    {
        this.familySize = familySize;
    }

    public Long getFamilySize() 
    {
        return familySize;
    }
    public void setDisasterType(String disasterType) 
    {
        this.disasterType = disasterType;
    }

    public String getDisasterType() 
    {
        return disasterType;
    }
    public void setDisasterDate(Date disasterDate) 
    {
        this.disasterDate = disasterDate;
    }

    public Date getDisasterDate() 
    {
        return disasterDate;
    }
    public void setDisasterDesc(String disasterDesc) 
    {
        this.disasterDesc = disasterDesc;
    }

    public String getDisasterDesc() 
    {
        return disasterDesc;
    }
    public void setLossTypes(String lossTypes) 
    {
        this.lossTypes = lossTypes;
    }

    public String getLossTypes() 
    {
        return lossTypes;
    }
    public void setHouseDamage(String houseDamage) 
    {
        this.houseDamage = houseDamage;
    }

    public String getHouseDamage() 
    {
        return houseDamage;
    }
    public void setCropArea(BigDecimal cropArea) 
    {
        this.cropArea = cropArea;
    }

    public BigDecimal getCropArea() 
    {
        return cropArea;
    }
    public void setEstimatedLoss(BigDecimal estimatedLoss) 
    {
        this.estimatedLoss = estimatedLoss;
    }

    public BigDecimal getEstimatedLoss() 
    {
        return estimatedLoss;
    }
    public void setLossDesc(String lossDesc) 
    {
        this.lossDesc = lossDesc;
    }

    public String getLossDesc() 
    {
        return lossDesc;
    }
    public void setReliefTypes(String reliefTypes) 
    {
        this.reliefTypes = reliefTypes;
    }

    public String getReliefTypes() 
    {
        return reliefTypes;
    }
    public void setRequestAmount(BigDecimal requestAmount) 
    {
        this.requestAmount = requestAmount;
    }

    public BigDecimal getRequestAmount() 
    {
        return requestAmount;
    }
    public void setMaterialNeeds(String materialNeeds) 
    {
        this.materialNeeds = materialNeeds;
    }

    public String getMaterialNeeds() 
    {
        return materialNeeds;
    }
    public void setOtherNeeds(String otherNeeds) 
    {
        this.otherNeeds = otherNeeds;
    }

    public String getOtherNeeds() 
    {
        return otherNeeds;
    }
    public void setEvidenceFiles(String evidenceFiles) 
    {
        this.evidenceFiles = evidenceFiles;
    }

    public String getEvidenceFiles() 
    {
        return evidenceFiles;
    }
    public void setEvidenceDesc(String evidenceDesc) 
    {
        this.evidenceDesc = evidenceDesc;
    }

    public String getEvidenceDesc() 
    {
        return evidenceDesc;
    }
    public void setStatus(Integer status) 
    {
        this.status = status;
    }

    public Integer getStatus() 
    {
        return status;
    }
    public void setAuditUserId(Long auditUserId) 
    {
        this.auditUserId = auditUserId;
    }

    public Long getAuditUserId() 
    {
        return auditUserId;
    }
    public void setAuditTime(Date auditTime) 
    {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() 
    {
        return auditTime;
    }
    public void setAuditRemark(String auditRemark) 
    {
        this.auditRemark = auditRemark;
    }

    public String getAuditRemark() 
    {
        return auditRemark;
    }
    public void setDeclaration(Integer declaration) 
    {
        this.declaration = declaration;
    }

    public Integer getDeclaration() 
    {
        return declaration;
    }

    @Override
    public String toString() {
        return "DisasterReliefApplication{" +
                "id=" + id +
                ", reportId='" + reportId + '\'' +
                ", name='" + name + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", familySize=" + familySize +
                ", disasterType='" + disasterType + '\'' +
                ", disasterDate=" + disasterDate +
                ", disasterDesc='" + disasterDesc + '\'' +
                ", lossTypes='" + lossTypes + '\'' +
                ", houseDamage='" + houseDamage + '\'' +
                ", cropArea=" + cropArea +
                ", estimatedLoss=" + estimatedLoss +
                ", lossDesc='" + lossDesc + '\'' +
                ", reliefTypes='" + reliefTypes + '\'' +
                ", requestAmount=" + requestAmount +
                ", materialNeeds='" + materialNeeds + '\'' +
                ", otherNeeds='" + otherNeeds + '\'' +
                ", evidenceFiles='" + evidenceFiles + '\'' +
                ", evidenceDesc='" + evidenceDesc + '\'' +
                ", status=" + status +
                ", auditUserId=" + auditUserId +
                ", auditTime=" + auditTime +
                ", auditRemark='" + auditRemark + '\'' +
                ", declaration=" + declaration +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
