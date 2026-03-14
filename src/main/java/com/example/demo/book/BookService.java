package com.example.demo.book;

import ch.qos.logback.core.util.StringUtil;
import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    public BookService(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBookFromGoogle(String googleId) {

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
        return bookRepository.save(book);
    }
}
