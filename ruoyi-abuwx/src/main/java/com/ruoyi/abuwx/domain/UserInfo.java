package com.ruoyi.abuwx.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户基本信息对象 user_info
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public class UserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 真实姓名 */
    @Excel(name = "真实姓名")
    private String realName;

    /** 身份证号(18位) */
    @Excel(name = "身份证号(18位)")
    private String idCard;

    /** 户籍所在地 */
    @Excel(name = "户籍所在地")
    private String domicile;

    /** 联系方式(手机号或固话) */
    @Excel(name = "联系方式(手机号或固话)")
    private String contact;

    /** 开户银行名称 */
    @Excel(name = "开户银行名称")
    private String bankName;

    /** 银行账号 */
    @Excel(name = "银行账号")
    private String bankAccount;

    /** 登录密码(加密存储) */
    @Excel(name = "登录密码(加密存储)")
    private String loginPassword;

    private String userrole;

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setRealName(String realName) 
    {
        this.realName = realName;
    }

    public String getRealName() 
    {
        return realName;
    }
    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard()
    {
        return idCard;
    }
    public void setDomicile(String domicile) 
    {
        this.domicile = domicile;
    }

    public String getDomicile() 
    {
        return domicile;
    }
    public void setContact(String contact) 
    {
        this.contact = contact;
    }

    public String getContact() 
    {
        return contact;
    }
    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", idCard='" + idCard + '\'' +
                ", domicile='" + domicile + '\'' +
                ", contact='" + contact + '\'' +
                ", bankName='" + bankName + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", loginPassword='" + loginPassword + '\'' +
                ", userrole='" + userrole + '\'' +
                '}';
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() 
    {
        return bankAccount;
    }
    public void setLoginPassword(String loginPassword) 
    {
        this.loginPassword = loginPassword;
    }

    public String getLoginPassword() 
    {
        return loginPassword;
    }

}
