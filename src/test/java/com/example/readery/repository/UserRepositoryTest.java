package com.example.readery.repository;

import com.example.readery.entity.User;
import com.example.readery.utils.InitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockBean
    InitService initService;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("user");
        user.setPassword("password");
        userRepository.save(user);
    }

    @Test
    void findByUsername_whenUserExists_returnsCorrectUser(){
        Optional<User> userOptional = userRepository.findByUsername("user");
        assertTrue(userOptional.isPresent());
        assertEquals(userOptional.get(),user);
    }

    @Test
    void findByUsername_whenUserNotExists_returnsEmpty(){
        Optional<User> userOptional = userRepository.findByUsername("fish");
        assertTrue(userOptional.isEmpty());
    }

    @Test
    void existsByUsername_whenUserExists_returnsTrue(){
        assertTrue(userRepository.existsByUsername("user"));
    }

    @Test
    void existsByUsername_whenUserNotExists_returnsFalse(){
        assertFalse(userRepository.existsByUsername("fish"));
    }

    @AfterEach
    void tearDown() {
        user = null;
        userRepository.deleteAll();
    }
}
