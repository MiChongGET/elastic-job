package cn.buildworld.elasticjob.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-08 14:13
 * @Version: V1.0
 */

public class MyJob2 implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.err.println("我是任务:"+shardingContext.getJobName());
    }
}
