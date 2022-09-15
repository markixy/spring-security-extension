# Spring Security 登录扩展组件示例

## 自定义登录认证
- NameAuthProcessor 用户名+密码认证
- NoAuthProcessor 工号+密码认证
- EmailAuthProcessor 邮箱+验证码认证
- PhoneAuthProcessor 手机号+验证码认证

## 初始数据源
默认使用 H2 内嵌数据库，已配置启动时自动建表并初始化数据。
sql位于 src/main/resources/sql/data.sql

## 核心配置类
- SecurityConfiguration
核心配置片段：
```
http.apply(new MultipleAuthenticationFilterConfigurer<>())
        // 扩展 邮箱+验证码登录
        .addAuthProcessor(new EmailAuthProcessor(userDao, emailCodeService()))
        .addPermitUrl("/email/code")
        // 扩展 手机号+验证码登录
        .addAuthProcessor(new PhoneAuthProcessor(userDao, phoneCodeService()))
        .addPermitUrl("/phone/code")
        // 扩展 工号+密码登录
        .addAuthProcessor(new NoAuthProcessor(userDao, passwordEncoder()))
        // 扩展 用户名+密码登录
        .addAuthProcessor(new NameAuthProcessor(userDao, passwordEncoder()))
        // 放行认证相关路径
        .permitAll()
;
```

## 首页
- index.html

## 登录页
- login.html