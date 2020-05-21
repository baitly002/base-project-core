package org.basecode.common;

import org.basecode.common.criterion.model.PageParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface TestController {

    @RequestMapping("/kkk")
    public PageParameter all();
}
