package co.com.ies.pruebas.webservice;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Random;

@Component
public class ProcessorDelayedRedis {
    Random rn = new SecureRandom();

    private final GreetingRepository greetingRepository;

    private final String hostAddress;

    private final FinishedTasckRedis finishedTasck;

    public ProcessorDelayedRedis(FinishedTasckRedis finishedTasck, GreetingRepository greetingRepository) {
        this.finishedTasck = finishedTasck;
        String hostAddress1;
        this.greetingRepository = greetingRepository;
        try {
            hostAddress1 = InetAddress.getLocalHost().getHostAddress() ;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostAddress1 = "no found";
        }
        this.hostAddress = hostAddress1;
    }

    @Async
    public void processElement(PendingTask<Greeting> greeting) {
        //final int timeSleep = rn.nextInt(2000);
        final int timeSleep = 2000;

        try {
            Thread.sleep(timeSleep);
            System.out.println("ProcessorDelayedRedis.processElement resolviendo element = " + greeting + " ,timeSleep = "+ timeSleep);
            final Greeting task = greeting.getTask();
            task.setIpTramited(hostAddress);

            greetingRepository.save(task);
            greetingRepository.flush();
            finishedTasck.add(greeting);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

