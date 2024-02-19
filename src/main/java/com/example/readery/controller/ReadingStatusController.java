package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.PostgresUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ReadingStatusController {

    ReadingStatusRepository readingStatusRepository;
    BookRepository bookRepository;
    UserRepository userRepository;

    public ReadingStatusController(ReadingStatusRepository readingStatusRepository,
                                   BookRepository bookRepository,
                                   UserRepository userRepository){
        this.readingStatusRepository = readingStatusRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/addUserBook")
    public ModelAndView addBookToUser(@RequestParam MultiValueMap<String, String> formData,
                                      @AuthenticationPrincipal PostgresUserDetails principle,
                                      RedirectAttributes redirAttr) {
        if (principle == null || principle.getUser() == null || principle.getUser().getId() == 0) {
            return null;
        }
        String bookId = formData.getFirst("bookId");
        if (bookId == null || bookId.isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        ReadingStatus readingStatus = parseFormToReadingStatus(formData, bookId,
                principle.getId());
        User user = userRepository.findById(principle.getId()).orElse(new User());
        Book book =
                bookRepository.findById(Integer.parseInt(bookId)).orElse(new Book());
        readingStatus.setUser(user);
        readingStatus.setBook(book);
        readingStatusRepository.save(readingStatus);

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
        String[] ids = readingStatusId.split("-");
        int bookId = Integer.parseInt(ids[0]);
        int userId = Integer.parseInt(ids[1]);

        ReadingStatusKey key = new ReadingStatusKey();
        key.setUserId(userId);
        key.setBookId(bookId);
        readingStatusRepository.deleteById(key);

        ModelAndView modelAndView = new ModelAndView("redirect:/");
        return modelAndView;
    }

    private ReadingStatus parseFormToReadingStatus(MultiValueMap<String,
            String> formData, String bookId, int userId){
        ReadingStatus readingStatus = new ReadingStatus();
        ReadingStatusKey key = new ReadingStatusKey();
        key.setBookId(Integer.parseInt(bookId));
        key.setUserId(userId);
        readingStatus.setId(key);

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
        return readingStatus;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
