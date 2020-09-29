package com.javaweb.system.dto;

import lombok.Data;

/**
 * 个人信息Dto
 */
@Data
public class UpdateInfoDto {

    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realname;

    /**
     * 手机号
     */
    private String mobile;

}
