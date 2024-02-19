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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Import(WebSecurityConfig.class)
@WebMvcTest(ReadingStatusController.class)
public class ReadingStatusControllerTest {
    @MockBean
    InitService initService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ReadingStatusRepository readingStatusRepository;

    @MockBean
    private BookRepository bookRepository;

    @Test
    void addBookToUser_whenAnonymousUser_returnNull() throws Exception {
        MvcResult result = mockMvc.perform(post("/addUserBook")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.endsWith("/login"));
    }

    @Test
    void deleteUserBook_whenAnonymousUser_returnNull() throws Exception {
        MvcResult result = mockMvc.perform(post("/deleteUserBook")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String redirectedUrl = result.getResponse().getRedirectedUrl();
        assertNotNull(redirectedUrl);
        assertTrue(redirectedUrl.endsWith("/login"));
    }

    @Test
    void addBookToUser_whenInvalidBook_throwIllegalArgumentException() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(post("/addUserBook")
                        .param("bookId", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUserBook_whenInvalidReadingStatus_throwIllegalArgumentException() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        mockMvc.perform(post("/addUserBook")
                        .param("readingStatusId", "")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookToUser_whenAuthenticatedUserAndValidBook_successfulUpdate() throws Exception {

        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findById(1)).thenReturn(java.util.Optional.of(user));
        when(bookRepository.findById(11)).thenReturn(java.util.Optional.of(new Book()));

        mockMvc.perform(post("/addUserBook")
                        .param("bookId", "1")
                        .param("startDate", "2022-01-01")
                        .param("finishDate", "2022-01-10")
                        .param("status", String.valueOf(ReadingStatus.Status.READING))
                        .param("rating", "5")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/books/1"));
    }

    @Test
    void deleteUserBook_whenAuthenticatedUserAndValidStatus_successfulDelete() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        PostgresUserDetails principle = new PostgresUserDetails(user);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principle, null,
                        principle.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("readingStatusId", "123-1");
        doNothing().when(readingStatusRepository).deleteById(any(ReadingStatusKey.class));
        mockMvc.perform(post("/deleteUserBook")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(params).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/"));
    }

}
