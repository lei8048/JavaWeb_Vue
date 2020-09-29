package com.javaweb.system.dto;

import lombok.Data;

/**
 * 更新密码Dto
 */
@Data
public class UpdatePwdDto {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String rePassword;

}
