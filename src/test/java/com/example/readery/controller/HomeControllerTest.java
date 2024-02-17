package com.example.readery.controller;

import com.example.readery.WebSecurityConfig;
import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.InitService;
import com.example.readery.utils.PostgresUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(HomeController.class)
class HomeControllerTest {
    @MockBean
    InitService initService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    void home_whenNullPrinciplal_returnStartPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("start"))
                .andExpect(model().size(0));
    }

    @Test
    void home_whenNullUser_returnStartPage() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("start"))
                .andExpect(model().size(0));
    }

    @Test
    void home_whenValidUser_returnWelcomePage() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Set<ReadingStatus> readingStatuses = new HashSet<>();
        ReadingStatus readingStatus = new ReadingStatus();
        ReadingStatusKey readingStatusKey = new ReadingStatusKey();
        readingStatusKey.setBookId(1);
        readingStatusKey.setUserId(user.getId());
        readingStatus.setId(readingStatusKey);
        readingStatus.setUser(user);
        readingStatus.setBook(new Book());
        readingStatus.setStatus(ReadingStatus.Status.WISHLIST);
        readingStatuses.add(readingStatus);
        user.setReadingStatuses(readingStatuses);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(model().attributeExists("userStatuses"));
    }
}
