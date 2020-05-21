package org.basecode.core.sequence.baidu;

import org.basecode.core.config.base.RedisClientTemplate;
import org.basecode.core.config.base.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerIdFromRedis implements WorkerIdAssigner{
    private final static Logger logger = LoggerFactory.getLogger(WorkerIdFromRedis.class);

    @Override
    public long assignWorkerId(){
        long workerId = 0;
        try {
            RedisClientTemplate redis = SpringBeanUtil.getBean(RedisClientTemplate.class);
            workerId = redis.incr("workerid");
        }catch (Exception e){
            logger.error("无法动态分配workerId，将以0代替");
        }
        return workerId;
    }
}
