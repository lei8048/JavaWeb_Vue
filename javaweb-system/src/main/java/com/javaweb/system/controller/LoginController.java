package com.javaweb.system.controller;

import com.javaweb.common.common.BaseController;
import com.javaweb.common.config.CommonConfig;
import com.javaweb.common.utils.CommonUtils;
import com.javaweb.common.utils.JsonResult;
import com.javaweb.common.utils.StringUtils;
import com.javaweb.system.dto.LoginDto;
import com.javaweb.system.dto.UpdateInfoDto;
import com.javaweb.system.dto.UpdatePwdDto;
import com.javaweb.system.entity.User;
import com.javaweb.system.mapper.UserMapper;
import com.javaweb.system.service.ILoginService;
import com.javaweb.system.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;

/**
 * <p>
 * 后台用户管理表 前端控制器
 * </p>
 *
 * @author 鲲鹏
 * @since 2020-02-26
 */
@RestController
@RequestMapping("/login")
public class LoginController extends BaseController {

    @Autowired
    private ILoginService userService;
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @GetMapping("/getLoginInfo")
    public JsonResult getLoginInfo() {
        return userService.getLoginInfo(ShiroUtils.getAdminId());
    }

    /**
     * 获取验证码
     *
     * @param response
     * @return
     */
    @GetMapping("/captcha")
    public JsonResult captcha(HttpServletResponse response) {
        return userService.captcha(response);
    }

    /**
     * 用户登录
     *
     * @param loginDto 登录Dto
     * @return
     */
    @PostMapping("/login")
    public JsonResult login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        return userService.login(loginDto, request);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @GetMapping("/logout")
    public JsonResult logout() {
        // 退出
        return userService.logout();
    }

    /**
     * 用户未登录
     *
     * @return
     */
    @GetMapping("/un_auth")
    public JsonResult unAuth() {
        return JsonResult.error(HttpStatus.UNAUTHORIZED, "", null);
    }

    /**
     * 用户无权限
     *
     * @return
     */
    @GetMapping("/unauthorized")
    public JsonResult unauthorized() {
        return JsonResult.error(HttpStatus.FORBIDDEN, "用户无权限", null);
    }

    /**
     * 获取菜单列表
     *
     * @return
     */
    @GetMapping("/getMenuList")
    public JsonResult getMenuList() {
        return userService.getMenuList();
    }

    /**
     * 更新个人信息
     *
     * @param updateInfoDto 个人信息Dto
     * @return
     * @throws IOException
     */
    @PostMapping("/profile")
    public JsonResult profile(@RequestBody UpdateInfoDto updateInfoDto) throws IOException {
//        return JsonResult.error("演示系统禁止操作");
        // 头像验证
        if (StringUtils.isEmpty(updateInfoDto.getAvatar())) {
            return JsonResult.error("请上传头像");
        }
        // 真实姓名验证
        if (StringUtils.isEmpty(updateInfoDto.getRealname())) {
            return JsonResult.error("真实姓名不能为空");
        }
        // 手机号验证
        if (StringUtils.isEmpty(updateInfoDto.getMobile())) {
            return JsonResult.error("手机号码不能为空");
        }

        // 更新数据信息
        User user = new User();
        user.setId(ShiroUtils.getAdminId());
        if (!StringUtils.isEmpty(updateInfoDto.getAvatar()) && updateInfoDto.getAvatar().contains(CommonConfig.imageURL)) {
            user.setAvatar(updateInfoDto.getAvatar().replaceAll(CommonConfig.imageURL, ""));
        } else {
            user.setAvatar(updateInfoDto.getAvatar());
        }
        user.setRealname(updateInfoDto.getRealname());
        user.setMobile(updateInfoDto.getMobile());
        Integer result = userMapper.updateById(user);
        if (result == 0) {
            return JsonResult.error("更新个人信息失败");
        }
        return JsonResult.success("更新个人信息成功");
    }

    /**
     * 重置密码
     *
     * @param updatePwdDto 重置密码Dto
     * @return
     */
    @PostMapping("/editPassword")
    public JsonResult editPassword(@RequestBody UpdatePwdDto updatePwdDto) {
//        return JsonResult.error("演示系统禁止操作");
        // 原密码校验
        if (StringUtils.isEmpty(updatePwdDto.getOldPassword())) {
            return JsonResult.error("原密码不能为空");
        }
        // 新密码校验
        if (StringUtils.isEmpty(updatePwdDto.getPassword())) {
            return JsonResult.error("新密码不能为空");
        }
        // 确认密码
        if (StringUtils.isEmpty(updatePwdDto.getRePassword())) {
            return JsonResult.error("确认密码不能为空");
        }
        if (!updatePwdDto.getPassword().equals(updatePwdDto.getRePassword())) {
            return JsonResult.error("两次输入的密码不一致");
        }
        // 获取当前用户的密码
        User user = userService.getById(ShiroUtils.getAdminId());
        if (user == null) {
            return JsonResult.error("用户信息不存在");
        }
        if (!user.getStatus().equals(1)) {
            return JsonResult.error("您的信息已被禁用");
        }
        user.setPassword(CommonUtils.password(updatePwdDto.getPassword()));
        Integer result = userMapper.updateById(user);
        if (result == 0) {
            return JsonResult.error("密码修改失败");
        }
        return JsonResult.success("密码修改成功");
    }
}
