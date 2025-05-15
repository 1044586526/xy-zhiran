package com.ruoyi.abuwx.mapper;

import java.util.List;
import com.ruoyi.abuwx.domain.UserInfo;

/**
 * 用户基本信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-07
 */
public interface UserInfoMapper 
{
    /**
     * 查询用户基本信息
     * 
     * @param id 用户基本信息主键
     * @return 用户基本信息
     */
    public UserInfo selectUserInfoById(Long id);

    /**
     * 查询用户基本信息列表
     * 
     * @param userInfo 用户基本信息
     * @return 用户基本信息集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    /**
     * 新增用户基本信息
     * 
     * @param userInfo 用户基本信息
     * @return 结果
     */
    public int insertUserInfo(UserInfo userInfo);

    /**
     * 修改用户基本信息
     * 
     * @param userInfo 用户基本信息
     * @return 结果
     */
    public int updateUserInfo(UserInfo userInfo);

    /**
     * 删除用户基本信息
     * 
     * @param id 用户基本信息主键
     * @return 结果
     */
    public int deleteUserInfoById(Long id);

    /**
     * 批量删除用户基本信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserInfoByIds(Long[] ids);
}
