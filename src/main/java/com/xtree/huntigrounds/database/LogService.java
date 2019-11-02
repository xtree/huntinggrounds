package com.xtree.huntigrounds.database;

import com.xtree.huntigrounds.data.Log;
import com.xtree.huntigrounds.data.Spot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service("logService")
public class LogService {

    private LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository
                      ) {
        this.logRepository = logRepository;
    }

    public void saveLog(Log log) {
        logRepository.save(log);
    }

    public void saveLog(String who, String what) {
        Log log = new Log();
        log.setTime(Date.from(Instant.now()));
        log.setMessage(who + " " + what);
        logRepository.save(log);
    }

    public List<Log> allLogs(){
        return logRepository.findAll();
    }

}