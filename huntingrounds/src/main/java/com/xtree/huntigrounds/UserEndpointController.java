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

import java.security.Principal;

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



    @GetMapping("/spot")
    public String place(@RequestParam(name = "code") String code, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService userService = appContext.getBean(UserService.class);
        SpotService spotService = appContext.getBean(SpotService.class);
        User user = userService.getUser(username);
        Spot spot = spotService.getSpot(code);

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

        HuntChecker huntChecker = new HuntChecker();
        huntChecker.doEverythig(model, spotService, pwningService, logService, user, spot);

        model.addAttribute("name", user.getUsername());
        model.addAttribute("spot", spot);
        return "spot";
    }






}



