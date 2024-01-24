package com.example.readery;

import com.example.readery.repository.AuthorRepository;
import com.example.readery.repository.BookRepository;

import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import org.json.JSONObject;
import org.springframework.context.annotation.Import;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@SpringBootApplication
@Import(WebSecurityConfig.class)
public class ReaderyApplication {
	@Autowired
	BookRepository bookRepository;
	@Autowired
	AuthorRepository authorRepository;

	@Value("${datadump.location.works}")
	private String worksLocation;

	@Value("${datadump.location.authors}")
	private String authorsLocation;

	public static void main(String[] args) {
		SpringApplication.run(ReaderyApplication.class, args);
	}



	private void initAuthors(){
		try(Stream<String> authors = Files.lines(Paths.get(authorsLocation))){
			authors.forEach(line -> {
				String jsonString = line.substring(line.indexOf('{'));
				try{
					JSONObject jsonObject = new JSONObject(jsonString);
					Author author = new Author();
					author.setName(jsonObject.optString("name"));
					author.setLibraryId(jsonObject.optString("key").substring(9));
					System.out.println("Saving author " + author.getName());
					authorRepository.save(author);
				}
				catch(JSONException e){
					e.printStackTrace();
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initWorks(){
		try(Stream<String> works = Files.lines(Paths.get(worksLocation))){
			works.forEach(line -> {
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
						Set<Author> authors = new HashSet<>();
						for (int i = 0; i < authorsArr.length(); i++) {
							String authorId = authorsArr.getJSONObject(i).getJSONObject("author").getString("key").replace("/authors/", "");
							List<Author> allAuthorsWithID = authorRepository.findByLibraryId(authorId); //this is a custom query method
							if(allAuthorsWithID == null || allAuthorsWithID.isEmpty()){
								System.out.println("Author for book " + book.getTitle() + " not found in DB");
								continue;
							}
							//if there are multiple authors with the same id = they're duplicate rows in txt. only first is saved.
							authors.add(allAuthorsWithID.get(0));
						}
						book.setAuthors(authors);
					}
					System.out.println("Saving book " + book.getTitle() + " by " + book.getAuthorsNames());
					bookRepository.save(book);
				}
				catch(JSONException e){
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@PostConstruct
	public void start(){
		//initAuthors();
		//initWorks();
		//bookRepository.createGinIndex();
	}

}
