package com.example.readery.controller;

import com.example.readery.WebSecurityConfig;
import com.example.readery.entity.Book;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.InitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @MockBean
    InitService initService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void searchBooks_whenNoMatches_returnSearchNoResults() throws Exception {
        when(bookRepository.searchBooks("query")).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/search")
                        .param("query", "query"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-no-results"))
                .andExpect(model().size(0));
    }

    @Test
    void searchBooks_whenFoundMatches_returnSearchMatches() throws Exception {
        List<Book> searchResult = new ArrayList<>();
        Book book = new Book();
        searchResult.add(book);
        when(bookRepository.searchBooks("query")).thenReturn(searchResult);
        mockMvc.perform(get("/search")
                        .param("query", "query"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-matches"))
                .andExpect(model().attributeExists("searchResult"));

    }



}
