package cn.buildworld.elasticjob.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据库源配置
 *
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-08 9:29
 * @Version: V1.0
 */
@Configuration
public class DataSourceConfig {

    @Bean("datasource")
    @ConfigurationProperties("spring.datasource.druid.log")
    public DataSource dataSourceTow(){
        return DruidDataSourceBuilder.create().build();
    }
}
