package com.example.demo;

import com.example.demo.book.BookService;
import com.example.demo.db.Book;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {
    private final GoogleBookService googleBookService;
    private final BookService bookService;

    public BookController(GoogleBookService googleBookService, BookService bookService) {
        this.googleBookService = googleBookService;
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
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
        return bookService.addBookFromGoogle(googleId);
    }
}
