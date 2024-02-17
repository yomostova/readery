package com.example.readery.controller;

import com.example.readery.WebSecurityConfig;
import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.repository.BookRepository;
import com.example.readery.repository.ReadingStatusRepository;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.InitService;
import com.example.readery.utils.PostgresUserDetails;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {
    @MockBean
    InitService initService;
    @InjectMocks
    private BookController bookController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private ReadingStatusRepository readingStatusRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    void getBook_whenBookNotExists_returnBookNotFound() throws Exception {
        when(bookRepository.findById(11)).thenReturn(Optional.empty());
        mockMvc.perform(get("/books/{book_id}", 11))
                .andExpect(status().isOk())
                .andExpect(view().name("book-not-found"));
    }

    @Test
    void getBook_whenBookExistsAndAnonymousUser_returnBookPage() throws Exception {
        when(bookRepository.findById(11)).thenReturn(Optional.of(new Book()));
        mockMvc.perform(get("/books/{book_id}", 11))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("coverImage"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void getBook_whenBookExistsAndAuthenticatedUser_returnBookPage() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(bookRepository.findById(11)).thenReturn(Optional.of(new Book()));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        mockMvc.perform(get("/books/{book_id}", 11))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("loginId"))
                .andExpect(model().attributeExists("coverImage"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("readingStatus"));
    }

    @Test
    void getBook_whenBookExistsAndUserAuthenticatedAndReadingStatus_returnBookPage() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(bookRepository.findById(11)).thenReturn(Optional.of(new Book()));
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        ReadingStatusKey readingStatusKey = new ReadingStatusKey(11, 1);
        ReadingStatus readingStatus = new ReadingStatus();
        when(readingStatusRepository.findById(readingStatusKey)).thenReturn(Optional.of(readingStatus));
        mockMvc.perform(get("/books/{book_id}", 11))
                .andExpect(status().isOk())
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("loginId"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("coverImage"))
                .andExpect(model().attributeExists("readingStatus"))
                .andExpect(model().attribute("readingStatus", readingStatus));
    }

}
