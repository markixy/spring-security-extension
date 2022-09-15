package cn.com.markix.security.phone.controller;

import cn.com.markix.security.phone.PhoneCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author markix
 */
@RestController
public class PhoneCodeController {

    @Autowired
    private PhoneCodeService codeService;

    /**
     * 生成验证码
     */
    @PostMapping("/phone/code")
    public String getCode(@RequestParam String phone) {
        codeService.generateCode(phone);
        return "ok";
    }

}
