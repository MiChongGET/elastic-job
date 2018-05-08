package cn.buildworld.elasticjob.web;

import cn.buildworld.elasticjob.config.DataSourceConfig;
import cn.buildworld.elasticjob.handler.ElasticJobHandler;
import cn.buildworld.elasticjob.job.MyJob2;
import cn.buildworld.elasticjob.listener.ElasticJobListener;
import cn.buildworld.elasticjob.utils.DateUtil;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-07 18:21
 * @Version: V1.0
 */

@RestController
public class TestController {

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Autowired
    private ElasticJobListener elasticJobListener;


    @Autowired
    private ElasticJobHandler elasticJobHandler;

    /**
     * 动态添加任务逻辑
     */
    @RequestMapping("/test")
    public void test() {
        int shardingTotalCount = 2;
        String jobName = UUID.randomUUID().toString() + "-test123";

        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration
                .newBuilder(jobName, "* * * * * ?", shardingTotalCount)
                .shardingItemParameters("0=A,1=B")
                .build();

        SimpleJobConfiguration simpleJobConfiguration =
                new SimpleJobConfiguration(jobCoreConfiguration, MyJob2.class.getCanonicalName());

        JobScheduler jobScheduler = new JobScheduler(zookeeperRegistryCenter, LiteJobConfiguration.newBuilder(simpleJobConfiguration).build());



        try {
            jobScheduler.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("定时任务创建失败");
        }


    }

    @ResponseBody
    @RequestMapping("/add")
    public Object add(){

        Date startTime = new Date();
        startTime.setTime(startTime.getTime()+3000);

        String cron = DateUtil.getCron(startTime);

        try {
            elasticJobHandler.addJob("myjob:"+cron,cron,2,"66666");
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }

        return "success";
    }


}
