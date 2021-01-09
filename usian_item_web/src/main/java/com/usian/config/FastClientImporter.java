package com.usian.config;

import com.github.tobato.fastdfs.FdfsClientConfig;
import com.usian.pojo.TbItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;

@Configuration //<beans>
//FdfsClientConfig：读取application.yml中的fastdfs配置，并创建连接放到连接池
@Import(FdfsClientConfig.class)//<bean>
// 解决重复注册bean的问题
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class FastClientImporter {

}
