package cn.com.markix.security.email;

/**
 * 验证码服务：生成验证码、校验验证码
 *
 * @author markix
 */
public interface EmailCodeService {

    /**
     * 生成验证码
     */
    void generateCode(String email);

    /**
     * 校验验证码
     */
    boolean verifyCode(String email, String code);

}
