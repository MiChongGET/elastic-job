package cn.buildworld.elasticjob.config;

import cn.buildworld.elasticjob.listener.ElasticJobListener;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-07 18:16
 * @Version: V1.0
 */
@Configuration
@ConditionalOnExpression("'${elastic.zookeeper.server-lists}'.length() >0")
public class ElasticConfig {

    /**
     * 初始化配置
     * @param serverList
     * @param namespace
     * @return
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter regCenter(@Value("${elaticjob.zookeeper.server-lists}") String serverList
            , @Value("${elaticjob.zookeeper.namespace}") String namespace) {

        return new ZookeeperRegistryCenter(new ZookeeperConfiguration(serverList, namespace));

    }


}
