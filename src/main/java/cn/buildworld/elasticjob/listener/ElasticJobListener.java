package cn.buildworld.elasticjob.listener;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import org.springframework.stereotype.Component;

/**
 * 任务监听器
 *
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-08 14:46
 * @Version: V1.0
 */
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
