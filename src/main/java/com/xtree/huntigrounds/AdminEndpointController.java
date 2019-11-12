package com.xtree.huntigrounds;

import com.xtree.huntigrounds.data.Spot;
import com.xtree.huntigrounds.data.User;
import com.xtree.huntigrounds.database.LogService;
import com.xtree.huntigrounds.database.SpotService;
import com.xtree.huntigrounds.database.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AdminEndpointController {

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/admin/logs")
    public String adminLogs(Model model) {
        LogService service = appContext.getBean(LogService.class);
        model.addAttribute("logs",service.allLogs());
        return "logs";
    }

    @GetMapping("/admin/users")
    public String adminUsers(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);
        model.addAttribute("name", username);
        model.addAttribute("users",service.allUsers());
        return "users";
    }

    @GetMapping("/admin/spots")
    public String adminSpots(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        SpotService service = appContext.getBean(SpotService.class);
        model.addAttribute("name", username);
        model.addAttribute("spots",service.allSpots());
        return "spots";
    }


    @RequestMapping(value = "/admin/changespot/{code}", method = RequestMethod.POST)
    public String changeSpot(
            @PathVariable("code") String code,
            @RequestParam(name="place") String place,
            @RequestParam(name="might") int might,
            @RequestParam(name="description") String description,
            Model model, Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        SpotService service = appContext.getBean(SpotService.class);
        LogService logService = appContext.getBean(LogService.class);
        try{
            service.modifySpot(might,place,description,code);
        }
        catch (Exception e)
        {
            model.addAttribute("message", e.getMessage());
        }




        model.addAttribute("name", username);
        model.addAttribute("spots",service.allSpots());
        logService.saveLog(username, "changes spot m: "+might+" pl: "+ place + " desc: "+description +" code: "+code);
        return "spots";
    }

    @RequestMapping(value = "/admin/adduser", method = RequestMethod.POST)
    public String adduser(@RequestParam(name="username") String uname, Model model, Principal principal) {
        UserService service = appContext.getBean(UserService.class);
        LogService logService = appContext.getBean(LogService.class);
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();


        User user = new User();
        user.setRole("USER");
        user.setUsername(uname);
        String password = randomPassword();
        user.setPassword(password);
        user.setLimit(1);
        try {
            service.saveUser(user);
            model.addAttribute("message", uname + " : " + password);
        }catch (Exception e)
        {
            model.addAttribute("message", e.getMessage());
        }
        logService.saveLog(username, "adds new user "+ uname);
        return "adminmessage";
    }

    @RequestMapping(value = "/admin/updateuser/{id}", method = RequestMethod.POST)
    public String changemight(
            @PathVariable("id") long id,
            @RequestParam(name="might") int might,
            @RequestParam(name="limit") int limit,
            Model model,
            Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);
        LogService logService = appContext.getBean(LogService.class);
        if (might > 0 && limit > 0) {
            try {
                service.setMightLimitById(might,limit, id);
                model.addAttribute("message", "new might set");
            } catch (Exception e) {
                model.addAttribute("message", e.getMessage());
            }
        }
        else {
            model.addAttribute("message", "invalid values entered, nothing happened");
        }
        model.addAttribute("name", username);
        model.addAttribute("users",service.allUsers());
        logService.saveLog(username,"sets might and limit for userid "+ id + " might: "+ might + " limit: "+limit );
        return "users";
    }

    @RequestMapping(value = "/admin/deleteuser/{id}", method = RequestMethod.POST)
    public String changemight(
            @PathVariable("id") long id,
            Model model,
            Principal principal) {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService service = appContext.getBean(UserService.class);
        LogService logService = appContext.getBean(LogService.class);

        try {
            service.deleteUser(id);
        }catch (Exception e)
        {
            model.addAttribute("message", e.getMessage());
            return "adminmessage";
        }
        model.addAttribute("message", "uspesne odstranen");
        logService.saveLog(username,"removed userid "+ id );
        return "adminmessage";
    }

    @RequestMapping(value = "/admin/addspot", method = RequestMethod.POST)
    public String addspot(
            @RequestParam(name="code") String code,
            @RequestParam(name="might") int might,
            @RequestParam(name="address") String address,
            @RequestParam(name="description") String description,
            Model model, Principal principal) {
        SpotService service = appContext.getBean(SpotService.class);
        LogService logService = appContext.getBean(LogService.class);
        Spot spot = new Spot();
        spot.setCode(code);
        spot.setMight(might);
        spot.setAddress(address);
        spot.setDescription(description);
        try {
            service.saveSpot(spot);
        }catch (Exception e)
        {
            model.addAttribute("message", e.getMessage());
            return "adminmessage";
        }
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
        String username = token.getName();
        UserService userService = appContext.getBean(UserService.class);
        model.addAttribute("name", username);
        model.addAttribute("spots",service.allSpots());
        logService.saveLog(username, "adds new spot "+ spot.getCode());
        return "spots";

    }
//    @GetMapping("/secret")
//    public String secret(Model model, Principal principal) {
//        UserService service = appContext.getBean(UserService.class);
//        User user = service.getUser("admin");
////        User user = new User();
////        user.setRole("ADMIN");
////        user.setUsername("admin");
//        user.setPassword("blaa");
//        service.saveUser(user);
//        return "user";
//    }

    private String randomPassword()
    {
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789#?!_<>";
        StringBuilder builder = new StringBuilder();
        int count =5;
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}



