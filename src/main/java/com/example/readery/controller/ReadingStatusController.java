package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.PostgresUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ReadingStatusController {

    @Autowired
    ReadingStatusRepository readingStatusRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookToUser(@RequestParam MultiValueMap<String, String> formData,
                                      @AuthenticationPrincipal PostgresUserDetails principle,
                                      RedirectAttributes redirAttr) {
        if (principle == null || principle.getUser() == null || principle.getUser().getId() == 0) {
            return null;
        }
        ReadingStatus readingStatus = new ReadingStatus();
        ReadingStatusKey key = new ReadingStatusKey();

        key.setUserId(principle.getId());
        String bookId = formData.getFirst("bookId");
        if (bookId != null && !bookId.isEmpty()) {
            key.setBookId(Integer.parseInt(bookId));
            readingStatus.setId(key);
        } else {
            throw new RuntimeException("Book id was not provided for Reading " +
                    "Status update!");
        }

        String startDate = formData.getFirst("startDate");
        if (startDate != null && !startDate.isEmpty()) {
            readingStatus.setStartedDate(LocalDate.parse(startDate));
        }

        String finishDate = formData.getFirst("finishDate");
        if (finishDate != null && !finishDate.isEmpty()) {
            readingStatus.setFinishedDate(LocalDate.parse(finishDate));
        }

        readingStatus.setStatus(ReadingStatus.Status.valueOf(formData.getFirst("status")));

        String rating = formData.getFirst("rating");
        if (rating != null && !rating.isEmpty()) {
            readingStatus.setRating(Integer.parseInt(rating));
        }

        User user = userRepository.findById(principle.getId()).orElse(new User());
        Book book = bookRepository.findById(Integer.parseInt(bookId)).orElse(new Book());

        readingStatus.setUser(user);
        readingStatus.setBook(book);
        readingStatusRepository.save(readingStatus);

        ModelAndView modelAndView = new ModelAndView("redirect:/books/" + bookId);
        redirAttr.addFlashAttribute("success", "Update was successful!");
        return modelAndView;

    }
}
