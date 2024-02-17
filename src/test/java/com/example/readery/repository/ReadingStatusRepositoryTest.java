package com.example.readery.repository;

import com.example.readery.entity.Book;
import com.example.readery.entity.ReadingStatus;
import com.example.readery.entity.ReadingStatusKey;
import com.example.readery.entity.User;
import com.example.readery.utils.InitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReadingStatusRepositoryTest {
    @Autowired
    private ReadingStatusRepository readingStatusRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @MockBean
    InitService initService;
    ReadingStatus readingStatus;
    ReadingStatusKey readingStatusKey;
    User user;
    Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1);
        bookRepository.save(book);

        user = new User();
        user.setId(1);
        user.setUsername("user");
        userRepository.save(user);

        readingStatusKey = new ReadingStatusKey();
        readingStatusKey.setBookId(book.getId());
        readingStatusKey.setUserId(user.getId());
        readingStatus = new ReadingStatus();
        readingStatus.setId(readingStatusKey);
        readingStatus.setUser(user);
        readingStatus.setBook(book);
        readingStatusRepository.save(readingStatus);
    }

    @Test
    void findAllById_whenUserExistsAndHasReadingStatuses_returnsCorrectReadingStatus(){
        Set<ReadingStatus> readingStatuses =
                readingStatusRepository.findAllById(user.getId());
        assertEquals(1, readingStatuses.size());
        assertEquals(readingStatus, readingStatuses.iterator().next());
    }

    @Test
    void findAllById_whenUserExistsAndHasNoStatuses_returnsEmptySet(){
        User user2 = new User();
        user2.setId(22);
        user2.setUsername("fish");
        userRepository.save(user2);
        Set<ReadingStatus> readingStatuses =
                readingStatusRepository.findAllById(22);
        assertTrue(readingStatuses.isEmpty());
    }

    @Test
    void findAllById_whenUserNotExists_returnsEmptySet(){
        Set<ReadingStatus> readingStatuses =
                readingStatusRepository.findAllById(7);
        assertTrue(readingStatuses.isEmpty());
    }

    @AfterEach
    void tearDown() {
        book = null;
        user = null;
        readingStatus = null;
        readingStatusRepository.deleteAll();
    }
}
