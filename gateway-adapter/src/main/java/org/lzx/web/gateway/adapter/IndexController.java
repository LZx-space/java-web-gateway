package org.lzx.web.gateway.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author LZx
 * @since 2021/11/28
 */
@Slf4j
@RestController
public class IndexController {

    @GetMapping("/test1/1")
    public String t1() {
        log.debug("logger level");
        return "hello1";
    }

    @GetMapping("/test2/2")
    public String t2() {
        return "hello2";
    }

}
