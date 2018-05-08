# elastic-job
分布式任务调度框架
#### 一、前言
[CSDN地址](https://blog.csdn.net/qq_31673689/article/details/80245593)

[官网地址](http://elasticjob.io/index_zh.html)
> 当当网张亮主导开发的分布式任务调度框架，结合zookeeper技术解决quartz框架在分布式系统中重复的定时任务导致的不可预见的错误！


```
Elastic-Job是一个分布式调度解决方案，由两个相互独立的子项目Elastic-Job-Lite和Elastic-Job-Cloud组成。

Elastic-Job-Lite定位为轻量级无中心化解决方案，使用jar包的形式提供分布式任务的协调服务；Elastic-Job-Cloud采用自研Mesos Framework的解决方案，额外提供资源治理、应用分发以及进程隔离等功能。
```
![架构图](http://ovfotjrsi.bkt.clouddn.com/docs/img/architecture/elastic_job_lite.png)

#### 二、SpringBoot整合

> 官网给的例子是基于spring xml来的，有兴趣的可以去看看，我们的项目采用springboot框架，所以要修改一些东西，比如修改为使用@Bean的方式来启动配置

##### 1、pom配置

```
        <!--框架核心jar包-->
        <dependency>
            <groupId>com.github.kuhn-he</groupId>
            <artifactId>elastic-job-lite-spring-boot-starter</artifactId>
            <version>2.1.5</version>
        </dependency>
        
        <!--添加数据相关的驱动主要是为了记录任务相关的一些数据，日志-->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.2</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
```
##### 2、application.properties配置

```
# zookeeper集群
elaticjob.zookeeper.server-lists=127.0.0.1:2181
elaticjob.zookeeper.namespace=my-project

# 主要是为了存储任务执行的日志
spring.datasource.druid.log.url=jdbc:mysql://localhost:3306/event_log
spring.datasource.druid.log.username=root
spring.datasource.druid.log.password=root
spring.datasource.druid.log.driver-class-name=com.mysql.jdbc.Driver

#  自动创建更新验证数据库结构
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database=mysql
spring.jpa.show-sql=true

```

##### 3、使用bean方式配置项目

```
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
    
    /**
     * 设置活动监听，前提是已经设置好了监听，见下一个目录
     * @return
     */
    @Bean
    public ElasticJobListener elasticJobListener() {
        return new ElasticJobListener(100, 100);
    }


}

```

##### 4、任务监听器

```
@Component
public class ElasticJobListener extends AbstractDistributeOnceElasticJobListener {


    /**
     * 设置间隔时间
     * @param startedTimeoutMilliseconds
     * @param completedTimeoutMilliseconds
     */
    public ElasticJobListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    /**
     * 任务开始
     * @param shardingContexts
     */
    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        System.out.println("任务开始");
    }

    /**
     * 任务结束
     * @param shardingContexts
     */
    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        System.err.println("任务结束");
    }
}

```

##### 5、数据库配置（任务第一种方式使用到）

```
@Configuration
public class DataSourceConfig {

    @Bean("datasource")
    @ConfigurationProperties("spring.datasource.druid.log")
    public DataSource dataSourceTow(){
        return DruidDataSourceBuilder.create().build();
    }
}

```



##### 6、设置任务（三种方式）

> Part1 通过在注解上面设置任务的cron,name等

```
@ElasticSimpleJob(cron = "0/2 * * * * ?",
        jobName = "firstJob",
        shardingTotalCount = 2,
        jobParameter = "测试参数",
        shardingItemParameters = "0=A,1=B",
        dataSource = "datasource")
@Component
public class MyJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(String.format("------Thread ID: %s, 任务总片数: %s, " +
                        "当前分片项: %s,当前参数: %s," +
                        "当前任务名称: %s,当前任务参数: %s,"+
                        "当前任务的id: %s"
                ,
                //获取当前线程的id
                Thread.currentThread().getId(),
                //获取任务总片数
                shardingContext.getShardingTotalCount(),
                //获取当前分片项
                shardingContext.getShardingItem(),
                //获取当前的参数
                shardingContext.getShardingParameter(),
                //获取当前的任务名称
                shardingContext.getJobName(),
                //获取当前任务参数
                shardingContext.getJobParameter(),
                //获取任务的id
                shardingContext.getTaskId()
        ));
    }
}
```
> Part2 通过控制器动态添加任务

```
@RestController
public class TestController {

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;


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

}
```
> Part3 通过handler包装生成任务的方法，简化控制器的代码量

ElasticJobHandler.java

```
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
                                                                       String id,String parameters) {
        return LiteJobConfiguration.newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration
                        .newBuilder(jobName, cron, shardingTotalCount)
                        .shardingItemParameters(parameters)
                        .jobParameter(id).
                        build(),
                jobClass.getCanonicalName()));
    }

    /**
     * 添加一个定时任务
     *
     * @param jobName            任务名
     * @param cron               表达式
     * @param shardingTotalCount 分片数
     * @param parameters         当前参数
     */
    public void addJob(String jobName, String cron, Integer shardingTotalCount, String id,String parameters) {
        LiteJobConfiguration jobConfig = simpleJobConfigBuilder(jobName, MyJob2.class, shardingTotalCount, cron, id,parameters)
                .overwrite(true).build();

        new SpringJobScheduler(new MyJob2(), zookeeperRegistryCenter, jobConfig, elasticJobListener).init();
    }
}

```
控制器

```
@ResponseBody
    @RequestMapping("/add")
    public Object add(){

        Date startTime = new Date();
        startTime.setTime(startTime.getTime()+3000);

        String cron = DateUtil.getCron(startTime);

        try {
            elasticJobHandler.addJob("myjob:"+cron,cron,2,"66666","0=A,1=B");
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }

        return "success";
    }
    
```
时间工具类（主要是date转换为cron表达式）

```
public class DateUtil {
    /**
     * 日期转化为cron表达式
     * @param date
     * @return
     */
    public static String getCron(Date  date){
        String dateFormat="ss mm HH dd MM ? yyyy";
        return  DateUtil.fmtDateToStr(date, dateFormat);
    }

    /**
     * cron表达式转为日期
     * @param cron
     * @return
     */
    public static Date getCronToDate(String cron) {
        String dateFormat="ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = sdf.parse(cron);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * Description:格式化日期,String字符串转化为Date
     *
     * @param date
     * @param dtFormat
     *            例如:yyyy-MM-dd HH:mm:ss yyyyMMdd
     * @return
     */
    public static String fmtDateToStr(Date date, String dtFormat) {
        if (date == null)
            return "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dtFormat);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
```
