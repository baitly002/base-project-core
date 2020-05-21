Controller类的代码生成是在项目启动，也就是发生在spring容器初始化的时候。

引用了common包之后，在spring web 容器初始化时，会自动扫描路径下，被@RestController注解的类，如下该类是一个接口类，则会根据application.yml配置文件中所配置的值，以相应的调用方式生成controller类

**application.yml**
```yaml
spring:
	autowried-type: dubbo
```


没有spring.autowried-type=dubbo时，默认采用spring原来的注入方式来生成controller类

参与如下：
```java
package com.zyun.core.service;

import com.zyun.core.model.Address;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.basecode.common.generic.service.GenericBaseService;

@RestController
@RequestMapping(value="/address")
public interface AddressService extends GenericBaseService<Address>{

	@GetMapping("/{id}")
	public Address selectByPrimaryKey(@PathVariable Long id);
}
```
