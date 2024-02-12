package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.utils.PostgresUserDetails;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class BookController{
    private final String COVER_IMAGE_BASE;
    BookRepository bookRepository;
    ReadingStatusRepository readingStatusRepository;

    public BookController(@Value("${cover.image.base}") String coverImageBase
            ,BookRepository bookRepository, ReadingStatusRepository readingStatusRepository){
        this.COVER_IMAGE_BASE = coverImageBase;
        this.bookRepository = bookRepository;
        this.readingStatusRepository = readingStatusRepository;
    }

    @GetMapping(value="/books/{book_id}")
    public String getBook(@PathVariable int book_id, Model model,
                          @AuthenticationPrincipal PostgresUserDetails principle){
        Optional<Book> optionalBook = bookRepository.findById(book_id);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            book.configureCover(COVER_IMAGE_BASE);
            bookRepository.save(book);
            model.addAttribute("coverImage", book.getCoverUrl());
            model.addAttribute("book", book);

            if(principle != null && principle.getUser() != null && principle.getId() != 0){
                int userId = principle.getId();
                model.addAttribute("loginId", userId);
                ReadingStatusKey key = new ReadingStatusKey(book_id, userId);
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
