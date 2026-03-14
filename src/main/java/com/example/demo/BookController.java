package com.example.demo;

import ch.qos.logback.core.util.StringUtil;
import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    @Autowired
    public BookController(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/google")
    public GoogleBook searchGoogleBooks(@RequestParam("q") String query,
                                        @RequestParam(value = "maxResults", required = false) Integer maxResults,
                                        @RequestParam(value = "startIndex", required = false) Integer startIndex) {
        return googleBookService.searchBooks(query, maxResults, startIndex);
    }

    @PostMapping("/books/{googleId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@PathVariable String googleId) {
        GoogleBook.Item item;
        try {
            item = googleBookService.getGoogleVolume(googleId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (item == null || StringUtil.isNullOrEmpty(item.id()) || item.volumeInfo() == null || CollectionUtils.isEmpty(item.volumeInfo().authors()) || StringUtil.isNullOrEmpty(item.volumeInfo().title()) || item.volumeInfo().pageCount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Book book = new Book();
        book.setId(item.id());
        book.setAuthor(item.volumeInfo().authors().get(0));
        book.setTitle(item.volumeInfo().title());
        book.setPageCount(item.volumeInfo().pageCount());
        bookRepository.save(book);
        return book;
    }
}
