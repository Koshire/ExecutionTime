package pl.akulov.executiontime.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.akulov.executiontime.service.SchedulerService;

@Component
public class TestScheduler {

    private final SchedulerService service;

    public TestScheduler(SchedulerService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 4000)
    public void testScheduler() throws InterruptedException {
        service.printMessage();
        service.printStartStopMessage();
    }
}
