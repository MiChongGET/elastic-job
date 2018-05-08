package cn.buildworld.elasticjob.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;

import java.util.List;

/**
 * 数据流任务
 *
 * @Author MiChong
 * @Email: 1564666023@qq.com
 * @Create 2018-05-08 11:32
 * @Version: V1.0
 */

public class MyDataFowJob implements DataflowJob {
    @Override
    public List fetchData(ShardingContext shardingContext) {
        return null;
    }

    @Override
    public void processData(ShardingContext shardingContext, List list) {

    }
}
