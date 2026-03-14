package com.example.demo.book;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class BookServiceTests {
    @Autowired
    BookService bookService;

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        bookRepository.save(new Book("lRtdEAAAQBAJ", "Spring in Action", "Craig Walls"));
        bookRepository.save(new Book("12muzgEACAAJ", "Effective Java", "Joshua Bloch"));
    }

    @Test
    void positive_getAllBooks() {
        List<Book> result = bookService.getAllBooks();
        Assert.assertNotNull(result.stream().filter(book -> book.getId().equals("12muzgEACAAJ")).findFirst().orElse(null));
        Assert.assertNotNull(result.stream().filter(book -> book.getId().equals("lRtdEAAAQBAJ")).findFirst().orElse(null));
    }

    @Test
    void positive_addBookFromGoogle() {
        Book book = bookService.addBookFromGoogle("TTVu8A3K9ysC");
        Assert.assertNotNull(book);
        Assert.assertNotNull(book.getId());
        Assert.assertNotNull(book.getAuthor());
        Assert.assertNotNull(book.getPageCount());
        Assert.assertNotNull(book.getTitle());
    }

    @Test
    void negative_addBookFromGoogle() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> {
                    bookService.addBookFromGoogle("invalidId");
                });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}
