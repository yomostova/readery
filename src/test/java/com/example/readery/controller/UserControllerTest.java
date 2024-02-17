package com.example.readery.controller;

import com.example.readery.WebSecurityConfig;
import com.example.readery.entity.User;
import com.example.readery.repository.UserRepository;
import com.example.readery.utils.InitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(WebSecurityConfig.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @MockBean
    InitService initService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    PasswordEncoder passwordEncoder;

    User user;

    @Test
    void showRegistrationForm_returnSignupFormForNewUser() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void processRegister_whenExistingUser_returnSignupForm() throws Exception {
        user = new User();
        user.setId(1);
        user.setUsername("existingUser");
        user.setPassword("existingPassword");
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        mockMvc.perform(post("/process_register")
                        .param("username", "existingUser")
                        .param("password", "existingPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("signup_form"));
    }

    @Test
    void processRegister_whenNewUser_returnRegisterSuccess() throws Exception {
        user = new User();
        user.setId(1);
        user.setUsername("newUser");
        user.setPassword("newPassword");
        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        mockMvc.perform(post("/process_register")
                        .param("username", "newUser")
                        .param("password", "newPassword")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register_success"));
    }

}
