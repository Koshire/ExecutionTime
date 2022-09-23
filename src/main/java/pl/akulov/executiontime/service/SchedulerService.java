package pl.akulov.executiontime.service;

import pl.akulov.executiontime.annotation.ExecutionTime;
import pl.akulov.executiontime.annotation.LogStartStop;

public interface SchedulerService {
    @ExecutionTime(dynamicLevel = true)
    void printMessage() throws InterruptedException;

    @LogStartStop
    void printStartStopMessage() throws InterruptedException;
}
