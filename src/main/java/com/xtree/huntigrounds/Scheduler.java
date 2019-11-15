package com.xtree.huntigrounds;

import com.xtree.huntigrounds.data.Pwning;
import com.xtree.huntigrounds.data.Spot;
import com.xtree.huntigrounds.data.User;
import com.xtree.huntigrounds.database.LogService;
import com.xtree.huntigrounds.database.PwningService;
import com.xtree.huntigrounds.database.SpotService;
import com.xtree.huntigrounds.database.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

@Component
public class Scheduler {

    @Autowired
    UserService service;

    @Autowired
    SpotService spotsService;

    @Autowired
    PwningService pwningService;

    @Autowired
    LogService logService;

    @Transactional
    @Scheduled(cron="0 */10 * * * *")
    public void addMight() {
        System.out.println(Instant.now().toString()+" tick");
        for (User user : service.allUsers()){
            int deltaMight = 0;
            for (Spot spot : user.getSpotsOwned()) {
                if (spot.isEnabled()) {
                    deltaMight += spot.getMight() / (24 * 6);
                }
            }
            if (deltaMight != 0) {
                user.setMight(user.getMight() + deltaMight);
                service.saveUser(user);
            }
        }
    }

    @Transactional
    @Scheduled(cron="0 * * * * *")
    public void clearPwnings() {
        //get all spots
        for (Spot spot : spotsService.allSpots()) {
            if (spot.getRandevouz() != null && spot.getRandevouz()
                    .before(
                            Date.from(
                                    Instant.now().minus(Duration.ofMinutes(10))
                            )
                    )
            ) {
                spot.setRandevouz(null);
                removePwnings(spot);
                spotsService.saveSpot(spot);
                logService.saveLog(spot.getOwner() != null ? spot.getOwner().getUsername() : "Nobody", "successfully defended his territory");
            }
        }
    }

    private void removePwnings(Spot spot) {
        for (Pwning pwning : spot.getPwnings()) {
            pwningService.delete(pwning);
        }
        spot.getPwnings().clear();
    }
}