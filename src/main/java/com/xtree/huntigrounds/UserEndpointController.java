package com.xtree.huntigrounds;

import com.xtree.huntigrounds.data.Pwning;
import com.xtree.huntigrounds.data.Spot;
import com.xtree.huntigrounds.data.User;
import com.xtree.huntigrounds.database.LogService;
import com.xtree.huntigrounds.database.PwningService;
import com.xtree.huntigrounds.database.SpotService;
import com.xtree.huntigrounds.database.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.util.calendar.ZoneInfo;

import java.security.Principal;
import java.sql.Timestamp;

@Controller
public class UserEndpointController {

    @Autowired
    private ApplicationContext appContext;

    @RequestMapping(value={"", "/", "user"})
    public String user(Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);

        model.addAttribute("name", username);
        User user = service.getUser(username);
        model.addAttribute("hunter", user);
        return "user";
    }

    @RequestMapping(value={"lowermight"})
    public String lowerMight(@RequestParam(name = "exchanged_might") int exchangedMight, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);
        LogService logService = appContext.getBean(LogService.class);

        model.addAttribute("name", username);
        User user = service.getUser(username);
        int loweredMight = user.getExchange_rate() * (exchangedMight/user.getExchange_rate());
        if ((user.getMight() > loweredMight) && (loweredMight >= 0)) {
            user.setMight(user.getMight() - loweredMight);
            service.saveUser(user);
            logService.saveLog(user.getUsername(), "exchanged "+loweredMight+ " might units for " + loweredMight / user.getExchange_rate() +" euros");
            model.addAttribute("message", "Právě jsi vyměnil "+loweredMight+ " bodů moci za " + loweredMight / user.getExchange_rate() +" eur");
        } else
        {
            logService.saveLog(user.getUsername(), "tried to exchange " + exchangedMight );
            model.addAttribute("message", "neplatné  - incident bude zalogován ");
        }
        model.addAttribute("hunter", user);
        return "user";
    }



    @GetMapping("/spot")
    public String place(@RequestParam(name = "code") String code, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService userService = appContext.getBean(UserService.class);
        SpotService spotService = appContext.getBean(SpotService.class);
        User user = userService.getUser(username);
        Spot spot = spotService.getSpot(code);
        for (Pwning p : spot.getPwnings()){
            if (p.getUser() == user){
                model.addAttribute(
                        "message",
                        "O toto loviště už soupeříš, souboj bude v "
                                + Renderer.getTime(
                                        new Timestamp(spot.getRandevouz().getTime()),
                                        (ZoneInfo) ZoneInfo.getTimeZone("CET")
                        )
                );
                model.addAttribute("competitor", true);
                break;
            }
        }
        if (user == spot.getOwner())
        {
            model.addAttribute(
                    "message",
                    "Tvé loviště je napadeno, souboj bude v "
                            + Renderer.getTime(
                            new Timestamp(spot.getRandevouz().getTime()),
                            (ZoneInfo) ZoneInfo.getTimeZone("CET")
                    )
            );
            model.addAttribute("competitor", true);
        }

        model.addAttribute("name", user.getUsername());
        model.addAttribute("spot", spot);
        return "spot";
    }

    @GetMapping("/hunt")
    public String hunt(@RequestParam(name = "code") String code, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService userService = appContext.getBean(UserService.class);
        SpotService spotService = appContext.getBean(SpotService.class);
        PwningService pwningService = appContext.getBean(PwningService.class);
        LogService logService = appContext.getBean(LogService.class);

        User user = userService.getUser(username);
        Spot spot = spotService.getSpot(code);

        if (spot.isEnabled()) {
            HuntChecker huntChecker = new HuntChecker();
            huntChecker.doEverythig(model, spotService, pwningService, logService, user, spot);
        } else {
            model.addAttribute("message", "Toto místo moci se mění, nelze o něj soupeřit.");
            logService.saveLog(username, "tried to attack disabled spot");
        }
        model.addAttribute("name", user.getUsername());
        model.addAttribute("spot", spot);
        return "spot";
    }

    @RequestMapping(value={"passchange"})
    public String passChange(@RequestParam(name = "old_pass") String oldPass,
                             @RequestParam(name = "new_pass") String newPass,
                             @RequestParam(name = "new_pass_two") String newPassTwo,
                             Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);
        LogService logService = appContext.getBean(LogService.class);

        User user = service.getUser(username);

        if (service.checkPassword(oldPass,user.getPassword())) {
            if (newPass != null &&
                    newPass.length() > 0 &&
                    newPass.equals(newPassTwo)
            ) {
                user.setPassword(newPass);
                service.saveUser(user);
                model.addAttribute("message", "Heslo úspěšně změněno.");
                logService.saveLog(username, " changed his password");

            } else {
                model.addAttribute("message", "nove heslo nesouhlasi");
                logService.saveLog(username, " tried to change the password");
            }
        } else {
            model.addAttribute("message", "stare heslo nesouhlasi");
            logService.saveLog(username, " tried to change the password");
        }
        model.addAttribute("name", username);
        model.addAttribute("hunter", user);
        return "user";
    }






}



