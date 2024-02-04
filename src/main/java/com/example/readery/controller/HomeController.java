package com.example.readery.controller;
import com.example.readery.utils.PostgresUserDetails;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.User;
import com.example.readery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String home(@AuthenticationPrincipal PostgresUserDetails principle, Model model){
        if(principle == null || principle.getUser() == null || principle.getId() == 0) {
            return "start";
        }
        User u = userRepository.findByUsername(principle.getUsername()).orElseThrow();
        Set<ReadingStatus> statusesOfUser = u.getReadingStatuses().stream()
                .limit(50)
                .collect(Collectors.toSet());
        model.addAttribute("userStatuses", statusesOfUser);
        return "welcome";
    }

}
