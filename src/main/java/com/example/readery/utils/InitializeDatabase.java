package com.example.readery.utils;

import com.example.readery.entity.Author;
import com.example.readery.entity.Book;
import com.example.readery.repository.AuthorRepository;
import com.example.readery.repository.BookRepository;
import com.google.common.collect.Iterators;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

@Component
// Used only for the initial population of database with Open Library data
@Slf4j
public class InitializeDatabase {
    BookRepository bookRepository;
    AuthorRepository authorRepository;

    @Value("${datadump.location.works}")
    private String worksLocation;

    @Value("${datadump.location.authors}")
    private String authorsLocation;

    //TODO remove hardcoded values
    private static final int AUTHORS = 12570513;
    private static final int WORKS = 34230390;
    private static final int BATCH_SIZE = 100000;

    public InitializeDatabase(BookRepository bookRepository,
                             AuthorRepository authorRepository){
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    public void setUp(){
        initAuthors();
        initWorks();
        System.out.println("----Initalization complete------");
    }

    private void initAuthors(){
        System.out.println("START AUTHOR INSERT");
        try(Stream<String> authors = Files.lines(Paths.get(authorsLocation))){
            Stream<Author> authorsToInsert = authors.map(line -> {
                String jsonString = line.substring(line.indexOf('{'));
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    if(jsonObject.has("key")){
                        author.setLibraryId(jsonObject.optString("key").substring(9));
                    }else{
                        author.setLibraryId("");
                        log.info(author.getName() + " does not have Library " +
                                "ID");
                    }
                    return author;
                }
                catch(JSONException e){
                    log.error("Error parsing JSON: " + jsonString, e);
                    return null;
                }
            }).filter(Objects::nonNull);
            int i = 0;
            for (var iter = Iterators.partition(authorsToInsert.iterator(),
                    BATCH_SIZE); iter.hasNext(); ) {
                List<Author> authorList = iter.next();
                System.out.println("Starting save batch of authors");
                authorRepository.saveAll(authorList);
                i=i+1;
                System.out.println("Progress " + ((double)i*BATCH_SIZE/AUTHORS)*100 +
                        " %");
            };
            System.out.println("---Saving rest of authors to DB---");
            System.out.println("---All authors have been saved");
            //authorRepository.deleteDuplicates();
        } catch (IOException e) {
            log.error("Error parsing the file: " + authorsLocation, e);
        }
    }

    private void initWorks(){
        System.out.println("START BOOK INSERT");
        try(Stream<String> works = Files.lines(Paths.get(worksLocation))){
            Stream<Book> books = works.map(line -> {
                String jsonString = line.substring(line.indexOf('{'));
                try{
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Book book = new Book();
                    book.setTitle(jsonObject.optString("title"));
                    JSONObject descr = jsonObject.optJSONObject("description");

                    if(descr != null){
                        book.setDescription(descr.optString("value"));
                    }

                    JSONObject published = jsonObject.optJSONObject("created");
                    if(published != null){
                        String timestamp = published.optString("value");
                        if(!timestamp.isEmpty()){
                            LocalDate date = LocalDate.parse(timestamp.substring(0,10));
                            book.setPublicationDate(date);
                        }
                    }

                    JSONArray coverArr = jsonObject.optJSONArray("covers");
                    if(coverArr != null){
                        book.setCoverId(coverArr.get(0).toString());
                    }

                    JSONArray authorsArr = jsonObject.optJSONArray("authors");
                    if(authorsArr != null) {
                        Set<String> authorLibraryIds = new HashSet<>();
                        for (int i = 0; i < authorsArr.length(); i++) {
                            String authorId = authorsArr.getJSONObject(i).getJSONObject("author").getString("key").replace("/authors/", "");
                            authorLibraryIds.add(authorId);
                        }
                        book.setAuthorLibraryIds(authorLibraryIds);
                    }
                    return book;
                }
                catch(JSONException e){
                    log.error("Error parsing JSON: " + jsonString, e);
                    return null;
                }
            }).filter(Objects::nonNull);
            int i = 0;
            for (var iter = Iterators.partition(books.iterator(),
                    BATCH_SIZE); iter.hasNext(); ) {
                var booksList = iter.next();
                System.out.println("Starting save batch of books");
                bookRepository.saveAll(booksList);
                i=i+1;
                System.out.println("Progress " + ((double)i*BATCH_SIZE/WORKS)*100 +
                        " %");
            }
            System.out.println("---Saving rest of books to DB---");
            //custom SQL query to join books and authors through OpenLibrary id
            System.out.println("---All books have been saved---");
            System.out.println("---Making final setup---");
            bookRepository.updateAuthors();
            bookRepository.updateAuthorNames();
        } catch (IOException e) {
            log.error("Error parsing the file: " + worksLocation, e);
        }
    }
}

