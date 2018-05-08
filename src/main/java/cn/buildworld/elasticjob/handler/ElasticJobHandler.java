package cn.buildworld.elasticjob.handler;

import cn.buildworld.elasticjob.config.DataSourceConfig;
import cn.buildworld.elasticjob.job.MyJob2;
import cn.buildworld.elasticjob.listener.ElasticJobListener;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 添加任务的配置
 *
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-08 14:58
 * @Version: V1.0
 */
@Component
public class ElasticJobHandler {

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private ElasticJobListener elasticJobListener;

    /**
     * @param jobName
     * @param jobClass
     * @param shardingTotalCount
     * @param cron
     * @param id                 数据ID
     * @return
     */
    private static LiteJobConfiguration.Builder simpleJobConfigBuilder(String jobName,
                                                                       Class<? extends SimpleJob> jobClass,
                                                                       int shardingTotalCount,
                                                                       String cron,
                                                                       String id) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount).jobParameter(id).build(), jobClass.getCanonicalName()));
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName            任务名
     * @param cron               表达式
     * @param shardingTotalCount 分片数
     */
    public void addJob(String jobName, String cron, Integer shardingTotalCount, String id) {
        LiteJobConfiguration jobConfig = simpleJobConfigBuilder(jobName, MyJob2.class, shardingTotalCount, cron, id)
                .overwrite(true).build();

        new SpringJobScheduler(new MyJob2(), zookeeperRegistryCenter, jobConfig, elasticJobListener).init();
    }
}
