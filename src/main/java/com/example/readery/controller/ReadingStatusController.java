package com.example.readery.controller;
import com.example.readery.utils.PostgresUserDetails;
import com.example.readery.utils.ReadingStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ReadingStatusController {

    @Autowired
    ReadingStatusService readingStatusService;

    @PostMapping("/addUserBook")
    public ModelAndView addBookToUser(@RequestParam MultiValueMap<String, String> formData,
                                      @AuthenticationPrincipal PostgresUserDetails principle,
                                      RedirectAttributes redirAttr) {
        if (principle == null || principle.getUser() == null || principle.getUser().getId() == 0) {
            return null;
        }
        int userId = principle.getUser().getId();
        String bookId = formData.getFirst("bookId");
        if (bookId == null || bookId.isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        readingStatusService.addBookToUser(formData, bookId, userId);
        ModelAndView modelAndView = new ModelAndView("redirect:/books/" + bookId);
        redirAttr.addFlashAttribute("success", "Update was successful!");
        return modelAndView;
    }

    @PostMapping("/deleteUserBook")
    public ModelAndView deleteUserBook(@RequestParam MultiValueMap<String, String>  params,
                                 @AuthenticationPrincipal PostgresUserDetails principle){
        if (principle == null || principle.getUser() == null || principle.getUser().getId() == 0) {
             return null;
        }
        String readingStatusId = params.getFirst("readingStatusId");
        if (readingStatusId == null || readingStatusId.isEmpty()) {
            throw new IllegalArgumentException("Reading status is empty");
        }
        readingStatusService.deleteUserBook(readingStatusId);
        return new ModelAndView("redirect:/");
    }
}
