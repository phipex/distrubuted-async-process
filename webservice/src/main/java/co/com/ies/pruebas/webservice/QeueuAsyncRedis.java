package co.com.ies.pruebas.webservice;

import co.com.ies.pruebas.webservice.Greeting;
import co.com.ies.pruebas.webservice.QueueAsyncAbstract;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class QeueuAsyncRedis  extends QueueAsyncAbstract<PendingTask<Greeting>> {

    private static final String KEY_QEUEU = "Pending.TaskTest_Qeueu";

    private final RedissonClient redissonClient;

    public QeueuAsyncRedis(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    protected void offer(PendingTask<Greeting> element) {

        Set<PendingTask<Greeting>> queue = getQueue();
        queue.add(element);

    }

    @Override
    protected Set<PendingTask<Greeting>> getQueue() {
        return redissonClient.getSet(KEY_QEUEU);
        //TODO mirar el tema de los listener

    }

    @Override
    protected void processElement(PendingTask<Greeting> element) {
        System.out.println("QeueuAsyncRedis.processElement "+ element);


    }
}

