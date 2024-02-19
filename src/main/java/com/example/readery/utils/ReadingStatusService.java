package com.example.readery.utils;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDate;

@Service
public class ReadingStatusService {
    ReadingStatusRepository readingStatusRepository;
    BookRepository bookRepository;
    UserRepository userRepository;

    public ReadingStatusService(ReadingStatusRepository readingStatusRepository,
                                   BookRepository bookRepository,
                                   UserRepository userRepository){
        this.readingStatusRepository = readingStatusRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public void addBookToUser(MultiValueMap<String, String> formData,
                              String bookId, int userId){
        ReadingStatus readingStatus = parseFormToReadingStatus(formData, bookId,
                userId);
        User user = userRepository.findById(userId).orElse(new User());
        Book book = bookRepository.findById(Integer.parseInt(bookId)).orElse(new Book());
        readingStatus.setUser(user);
        readingStatus.setBook(book);
        readingStatusRepository.save(readingStatus);
    }

    public void deleteUserBook(String readingStatusId){
        String[] ids = readingStatusId.split("-");
        int bookId = Integer.parseInt(ids[0]);
        int userId = Integer.parseInt(ids[1]);
        ReadingStatusKey key = new ReadingStatusKey();
        key.setUserId(userId);
        key.setBookId(bookId);
        readingStatusRepository.deleteById(key);
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
