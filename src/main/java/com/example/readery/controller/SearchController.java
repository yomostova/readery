package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    private final String COVER_IMAGE_BASE;
    BookRepository bookRepository;
    public SearchController(@Value("${cover.image.base}") String coverImageBase,
            BookRepository bookRepository){
        this.COVER_IMAGE_BASE = coverImageBase;
        this.bookRepository = bookRepository;
    }

    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> searchResult = bookRepository.searchBooks(query);
        if(!searchResult.isEmpty()){
            for (Book b: searchResult) {
                b.configureCover(COVER_IMAGE_BASE);
            }
            model.addAttribute("searchResult", searchResult);
            return "search-matches";
        } else {
            return "search-no-results";
        }
    }
}
