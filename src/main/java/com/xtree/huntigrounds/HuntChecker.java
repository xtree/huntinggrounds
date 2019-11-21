package com.xtree.huntigrounds;

import com.xtree.huntigrounds.data.Pwning;
import com.xtree.huntigrounds.data.Spot;
import com.xtree.huntigrounds.data.User;
import com.xtree.huntigrounds.database.LogService;
import com.xtree.huntigrounds.database.PwningService;
import com.xtree.huntigrounds.database.SpotService;
import org.springframework.ui.Model;

import java.sql.Date;
import java.time.Duration;
import java.time.Instant;

public class HuntChecker {

    public static final Duration DURATION = Duration.ofHours(1); //temporary, need to be set to 24

    void doEverythig(Model model, SpotService spotService, PwningService pwningService, LogService logService, User user, Spot spot) {

        fillOwnerParam(model, user, spot);

        if (spot.getRandevouz() == null) {
            //new contest
            if (!containOrOwner(spot, user)) {
                if (!userIsOverLimit(user)) {
                    addToPwnings(pwningService, user, spot);
                    createRandevouz(spotService, spot, DURATION);
                    model.addAttribute("message", "jsi na seznamu útočníků, někdo si to s tebou vyřídí");
//                    model.addAttribute("message", "you are contesting for this spot");
                    logService.saveLog(user.getUsername(), "attacks to hunting ground " + spot.getCode() + " where is registered as attacker");
                } else {
                    model.addAttribute("message", "nedokážeš útočit na více lovišť");
//                    model.addAttribute("message", "you are not able to contest for another spot");
                    logService.saveLog(user.getUsername(), "tried to claim " + spot.getCode() + " over his limit.");
                }
            } else {
                model.addAttribute("message", "Jsi vlastník tohoto loviště");
//                model.addAttribute("message", "your are owner of the spot");
                logService.saveLog(user.getUsername(), "attacks his own hunting ground " + spot.getCode());
            }
        } else {
            if (spot.getRandevouz().before(Date.from(Instant.now())) &&
                    spot.getRandevouz().after(Date.from(Instant.now().minus(Duration.ofMinutes(10))))
            ) {
                //randevouz is now
                if (containOrOwner(spot, user)) {
                    //remove pwnings
                    removePwnings(pwningService, spot);
                    //add this user as owner
                    setNewOwner(spotService, user, spot);
                    model.addAttribute("message", "vyhrál jsi boj o toto loviště");
//                    model.addAttribute("message", "you have won this spot");
                    logService.saveLog(user.getUsername(), "becames owner of " + spot.getCode());
                } else {
                    //remove pwnings
                    removePwnings(pwningService, spot);
                    if (!userIsOverLimit(user)) {
                        //add user to pwnings
                        addToPwnings(pwningService, user, spot);
                        //create randevouz
                        createRandevouz(spotService, spot, DURATION);
                        model.addAttribute("message", "nová bitva začala, jsi na seznamu útočníků ");
//                        model.addAttribute("message", "new contest started, you are contesting for this spot ");
                        logService.saveLog(user.getUsername(), "starts new hunt for " + spot.getCode());
                    } else {
                        model.addAttribute("message", "nedokážeš útočit na více lovišť");
                        logService.saveLog(user.getUsername(), "tried to claim " + spot.getCode() + " over his limit and break the contest for the spot. ");
                    }
                }
            } else {
                if (spot.getRandevouz().after(Date.from(Instant.now()))) {
                    //randevouz is valid (is not in past)
                    if (!containOrOwner(spot, user)) {
                        addToPwnings(pwningService, user, spot);
                        model.addAttribute("message", "jsi na seznamu útočníků, někdo si to s tebou vyřídí");
                        logService.saveLog(user.getUsername(), "added to hunt for " + spot.getCode());
                    } else {
                        model.addAttribute("message", "už jsi na seznamu útočníků");
                        logService.saveLog(user.getUsername(), "attacks to hunting ground " + spot.getCode() + " where is registered as attacker (2)");
                    }
                } else {
                    //randevouz is not valid (is not in past) and is not cleared yet
                    //new contest
                    if (!containOrOwner(spot, user)) {
                        if (!userIsOverLimit(user)) {
                            addToPwnings(pwningService, user, spot);
                            createRandevouz(spotService, spot, DURATION);
                            model.addAttribute("message", "jsi na seznamu útočníků, někdo si to s tebou vyřídí");
                            logService.saveLog(user.getUsername(), "attacks to hunting ground " + spot.getCode() + " where is registered as attacker");
                        } else {
                            model.addAttribute("message", "nedokážeš útočit na více lovišť");
                            logService.saveLog(user.getUsername(), "tries to claim " + spot.getCode() + " over his limit.");
                        }
                    } else {
                        model.addAttribute("message", "Jsi vlastník tohoto loviště");
                        logService.saveLog(user.getUsername(), "attacks his own hunting ground " + spot.getCode());
                    }
                }
            }
        }
    }

    private boolean userIsOverLimit(User user) {
        return (user.getPwnings().size() + user.getSpotsOwned().size())
                        >=
                        user.getLimit();
    }

    private void setNewOwner(SpotService spotService, User user, Spot spot) {
        spot.setOwner(user);
        spot.setRandevouz(null);
        spotService.saveSpot(spot);
    }

    private void removePwnings(PwningService pwningService, Spot spot) {
        for (Pwning pwning : spot.getPwnings()) {
            pwningService.delete(pwning);
        }
        spot.getPwnings().clear();
    }

    void createRandevouz(SpotService spotService, Spot spot, Duration duration) {
        spot.setRandevouz(Date.from(Instant.now().plus(duration)));
        spotService.saveSpot(spot);
    }

    private void addToPwnings(PwningService pwningService, User user, Spot spot) {
        Pwning pwning = new Pwning();
        pwning.setUser(user);
        pwning.setCode(spot);
        pwningService.savePwning(pwning);
    }

    private void fillOwnerParam(Model model, User user, Spot spot) {
        if (spot.getOwner() == user) {
            model.addAttribute("owner", "you are owner of the spot");
        } else {
            model.addAttribute("owner", spot.getOwner());
        }
    }

    private boolean containOrOwner(Spot spot, User user) {
        if (spot.getOwner() == user) return true;
        for (Pwning pwning : spot.getPwnings()) {
            if (pwning.getUser() == user) return true;
        }
        return false;
    }
}
