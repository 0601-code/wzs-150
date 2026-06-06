package com.haircutcare.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haircutcare.common.Result;
import com.haircutcare.dto.LoginRequest;
import com.haircutcare.dto.LoginResponse;
import com.haircutcare.entity.SysUser;
import com.haircutcare.mapper.SysUserMapper;
import com.haircutcare.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
            new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername())
        );

        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole(), user.getId());

        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getRealName(),
            user.getRole(),
            user.getId()
        );

        return Result.success("登录成功", response);
    }
}
