# Spring Security 登录扩展组件（用于支持多种登录方式）

## 实现思路
- 自定义AuthenticationFilter 提供认证接口，用于构建自定义AuthenticationToken，通过自定义AuthenticationProvider对自定义AuthenticationToken进行认证
- 将 Filter 和 Provider 组装到过滤器链中，使之生效。
- 放行必要的路径

## 自定义认证抽象层
- AuthProcessor：认证处理器接口，用于抽象不同认证方式！ 
- MultipleAuthenticationFilter：认证过滤器，维护多个认证处理器 List<AuthProcessor>，支持多种认证
- MultipleAuthenticationProvider：认证提供者，实际上将认证代理给 AuthProcessor
- MultipleAuthenticationFilterConfigurer：自定义认证配置器，需要在 SecurityConfiguration 应用！

## 示例
> 每种认证方式只需要实现 AuthProcessor 接口

见 spring-security-login-extension-samples 项目

