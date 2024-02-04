package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.utils.PostgresUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class BookController {
    private final String COVER_IMAGE_BASE = "https://covers.openlibrary.org/b/id/";
    @Autowired
    BookRepository bookRepository;

    @Autowired
    ReadingStatusRepository readingStatusRepository;

    @GetMapping(value="/books/{book_id}")
    public String getBook(@PathVariable int book_id, Model model,
                          @AuthenticationPrincipal PostgresUserDetails principle){
        Optional<Book> optionalBook = bookRepository.findById(book_id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            if(book.getCoverId() != null && !book.getCoverId().isEmpty()){
                String coverUrl = COVER_IMAGE_BASE + book.getCoverId() + "-M.jpg";
                if(book.getCoverUrl() == null){
                    book.setCoverUrl(coverUrl);
                }
            } else {
                book.setCoverUrl("/images/no-image.png");
            }
            bookRepository.save(book);
            model.addAttribute("coverImage", book.getCoverUrl());
            model.addAttribute("book", book);

            if(principle != null && principle.getUser() != null && principle.getId() != 0){
                int userId = principle.getId();
                model.addAttribute("loginId", userId);
                ReadingStatusKey key = new ReadingStatusKey();
                key.setBookId(book_id);
                key.setUserId(userId);
                ReadingStatus readingStatus =
                        readingStatusRepository.findById(key).orElse(new ReadingStatus());
                model.addAttribute("readingStatus", readingStatus);
            }
            return "book";

        } else {
            return "book-not-found";
        }
    }

    @PostMapping("/books")
    public Book addBook(@RequestBody Book book) {
        return this.bookRepository.save(book);
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable int id, @RequestBody Book bookRequest){
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookRequest.getTitle());
            book.setDescription(bookRequest.getDescription());
            book.setPublicationDate(bookRequest.getPublicationDate());
            book.setAuthors(bookRequest.getAuthors());
            return bookRepository.save(book);
        }).orElseThrow(() -> new EntityNotFoundException("BookId " + id + " not found"));
    }

}
