package co.com.ies.pruebas.webservice;

import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class AsyncProcessServiceImpl implements AsyncProcessService{

    public static final String LOCK_ADD_TASK = "Lock.AddTask";
    public static final String LOCK_ADQUIRE_TASK = "Lock.AdquireTask";

    private final GreetingRepository greetingRepository;
    private final RedissonClient redissonClient;
    private final QeueuAsyncRedis qeueuAsyncRedis;
    private final String hostAddress;

    private final ProcessorDelayedRedis processorDelayed;

    public AsyncProcessServiceImpl(GreetingRepository greetingRepository, RedissonClient redissonClient, QeueuAsyncRedis qeueuAsyncRedis, ProcessorDelayedRedis processorDelayed) {

        this.greetingRepository = greetingRepository;
        this.redissonClient = redissonClient;
        this.qeueuAsyncRedis = qeueuAsyncRedis;
        this.processorDelayed = processorDelayed;
        String hostAddress1;
        try {
            hostAddress1 = InetAddress.getLocalHost().getHostAddress() ;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostAddress1 = "no found";
        }
        hostAddress = hostAddress1;
    }

    //@Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayAddTask() {
        //System.out.println("scheduleFixedDelayAddTask Fixed delay task - " + System.currentTimeMillis() / 1000);

        RSemaphore semaphore = redissonClient.getSemaphore(LOCK_ADD_TASK);
        final int availablePermits = semaphore.availablePermits();

        if(availablePermits == 0){
            final boolean trySetPermits = semaphore.trySetPermits(1);
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAddTask trySetPermits" + trySetPermits);
        }
        final boolean tryAcquire = semaphore.tryAcquire();
        //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAddTask " + tryAcquire + " availablePermits = " + availablePermits);
        if(tryAcquire){
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAddTask adquire");
            addTasks();
            semaphore.release();
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAddTask release");
        }

    }

    @Override
    public void addTasks(){
        final List<Greeting> greetingsByIpTramitedIsNull = greetingRepository.getGreetingsByIpTramitedIsNull();

        if(greetingsByIpTramitedIsNull.isEmpty()){
            return;
        }

        final List<PendingTask<Greeting>> collect = greetingsByIpTramitedIsNull.stream()
                .map(PendingTask::new)
                .collect(Collectors.toList());

        qeueuAsyncRedis.offerTascks(collect);
        final int size = greetingsByIpTramitedIsNull.size();
        System.out.println("AsyncProcessServiceImpl.addTasks size = " + size);
    }

    //@Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayAdquireTask() {

        //System.out.println("scheduleFixedDelayAdquireTask Fixed delay task - " + System.currentTimeMillis() / 1000);

        RSemaphore semaphore = redissonClient.getSemaphore(LOCK_ADQUIRE_TASK);
        final int availablePermits = semaphore.availablePermits();

        if(availablePermits == 0){
            final boolean trySetPermits = semaphore.trySetPermits(1);
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAdquireTask trySetPermits" + trySetPermits);
        }
        final boolean tryAcquire = semaphore.tryAcquire();
        //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAdquireTask " + tryAcquire + " availablePermits = " + availablePermits);
        if(tryAcquire){
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAdquireTask adquire");
            adquireTasks();
            semaphore.release();
            //System.out.println("AsyncProcessServiceImpl.scheduleFixedDelayAdquireTask release");
        }

    }

    @Override
    public void adquireTasks() {
        final Set<PendingTask<Greeting>> queue = qeueuAsyncRedis.getQueue();
        List<PendingTask<Greeting>> lista = new ArrayList<>(queue);
        System.out.println("AsyncProcessServiceImpl.adquireTasks lista = " + lista.size());

        Predicate<PendingTask<Greeting>> vaciosPropios =
                value -> "".equals(value.getManageFor()) || value.getManageFor() == null;// || hostAddress.equals(value.getManageFor());

        lista = lista.stream().filter(vaciosPropios).collect(Collectors.toList());
        for (PendingTask<Greeting> next : lista) {
            final Set<PendingTask<Greeting>> queue1 = qeueuAsyncRedis.getQueue();
            final int size = queue1.size();
            System.out.println("AsyncProcessServiceImpl.adquireTasks antessize = " + size);

            queue1.remove(next);
            next.setManageFor(hostAddress);
            qeueuAsyncRedis.getQueue().add(next);
            System.out.println("AsyncProcessServiceImpl.adquireTasks despues = " + qeueuAsyncRedis.getQueue().size());

        }
        processTaskList(lista);
    }

    private void processTaskList(List<PendingTask<Greeting>> lista) {
        for(PendingTask<Greeting> element: lista){

            processorDelayed.processElement(element);
            qeueuAsyncRedis.getQueue().remove(element);
        }
    }

}
