package pl.akulov.executiontime.service.impl;

import org.springframework.stereotype.Service;
import pl.akulov.executiontime.annotation.ExecutionTime;
import pl.akulov.executiontime.annotation.LogStartStop;
import pl.akulov.executiontime.service.SchedulerService;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    @Override
    @ExecutionTime(dynamicLevel = true)
    public void printMessage() throws InterruptedException {
        Thread.sleep(200);
        System.out.println("Hello !!!");
    }

    @Override
    @LogStartStop
    public void printStartStopMessage() throws InterruptedException {
        Thread.sleep(400);
        System.out.println("Hello !!!");
    }
}
