package cn.com.markix.security.email.controller;

import cn.com.markix.security.email.EmailCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author markix
 */
@RestController
public class EmailCodeController {

    @Autowired
    private EmailCodeService codeService;

    /**
     * 生成验证码
     */
    @PostMapping("/email/code")
    public String getCode(@RequestParam String email) {
        codeService.generateCode(email);
        return "ok";
    }

}
