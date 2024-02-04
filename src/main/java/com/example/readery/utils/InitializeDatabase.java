package com.example.readery.utils;

import com.example.readery.entity.Author;
import com.example.readery.entity.Book;
import com.example.readery.repository.AuthorRepository;
import com.example.readery.repository.BookRepository;
import com.google.common.collect.Iterators;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
public class InitializeDatabase {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;

    @Value("${datadump.location.works}")
    private String worksLocation;

    @Value("${datadump.location.authors}")
    private String authorsLocation;

    private static final int AUTHORS = 12570513;
    private static final int WORKS = 34230390;
    private static final int BATCH_SIZE = 100000;

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
                    author.setLibraryId(jsonObject.optString("key").substring(9));
                    return author;
                }
                catch(JSONException e){
                    e.printStackTrace();
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
            authorRepository.deleteDuplicates();
        } catch (IOException e) {
            e.printStackTrace();
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
                    //e.printStackTrace();
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
            bookRepository.updateAuthors();
            bookRepository.updateAuthorNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

