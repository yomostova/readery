package com.example.readery.controller;

import com.example.readery.entity.Book;
import com.example.readery.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {
    private final String COVER_IMAGE_BASE = "http://covers.openlibrary.org/b/id/";
    @Autowired
    BookRepository bookRepository;

    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query, Model model) {
        List<Book> searchResult = bookRepository.searchBooksByTitle(query);
        if(!searchResult.isEmpty()){
            for (Book b: searchResult) {
                if(b.getCoverId() != null && !b.getCoverId().isEmpty()){
                    String coverUrl = COVER_IMAGE_BASE + b.getCoverId() + "-M.jpg";
                    if(b.getCoverUrl() == null){
                        b.setCoverUrl(coverUrl);
                    }
                } else {
                    b.setCoverUrl("/images/no-image.png");
                }
            }
            model.addAttribute("searchResult", searchResult);
            return "search-matches";
        } else {
            return "search-no-results";
        }
    }
}
