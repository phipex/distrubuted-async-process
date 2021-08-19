package co.com.ies.pruebas.webservice;

import co.com.ies.pruebas.webservice.Greeting;
import co.com.ies.pruebas.webservice.QueueAsyncAbstract;
import org.redisson.api.*;
import org.springframework.stereotype.Component;

import java.util.Optional;
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

        final Set<PendingTask<Greeting>> queue = getQueue();
        final boolean noContains = !isContains(element, queue);
        if(noContains){
            queue.add(element);
        }else{
            System.out.println("QeueuAsyncRedis.offer ya habia sido agregada >>>>>>>>>>>>>>>>"+element);
        }

    }

    @Override
    protected void updateElement(PendingTask<Greeting> element) {
        final Set<PendingTask<Greeting>> queue = getQueue();
        final Optional<PendingTask<Greeting>> byTaskId = findByTaskId(element, queue);
        byTaskId.ifPresent(queue::remove);
        queue.add(element);

    }

    @Override
    protected boolean isContains(PendingTask<Greeting> element, Set<PendingTask<Greeting>> queue) {
        final Optional<PendingTask<Greeting>> first = findByTaskId(element, queue);
        return first.isPresent();

    }

    @Override
    protected boolean isContains(PendingTask<Greeting> element) {
        final Set<PendingTask<Greeting>> queue = getQueue();
        return isContains(element, queue);
    }

    private Optional<PendingTask<Greeting>> findByTaskId(PendingTask<Greeting> element, Set<PendingTask<Greeting>> queue) {
        return queue.stream().filter(item -> {
            final Greeting task = item.getTask();
            final Long id = task.getId();
            return id.equals(element.getTask().getId());
        }).findFirst();

    }

    @Override
    protected boolean remove(PendingTask<Greeting> element) {
        Set<PendingTask<Greeting>> queue = getQueue();
        final Optional<PendingTask<Greeting>> first = findByTaskId(element, queue);
        if(first.isPresent()){
            queue.remove(first.get());
            return true;
        }
        System.out.println("QeueuAsyncRedis.remove no habia sido agregada >>>>>>>>>>>>>>>>");

        return false;
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

    @Override
    protected int size() {
        return getQueue().size();
    }
}

