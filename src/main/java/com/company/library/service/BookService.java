package com.company.library.service;

import com.company.library.dto.BookListItemResponse;
import com.company.library.dto.BookResponse;
import com.company.library.dto.requests.BookSaveRequest;
import com.company.library.model.Book;
import com.company.library.model.BookStatus;
import com.company.library.model.User;
import com.company.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final AuthService authService;

    public BookService(BookRepository bookRepository, CategoryService categoryService, UserService userService, AuthService authService) {
        this.bookRepository = bookRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.authService = authService;
    }

    @Transactional
    public BookListItemResponse save(BookSaveRequest bookSaveRequest){

        Book book = new Book.Builder().title(bookSaveRequest.getTitle()).authorName(bookSaveRequest.getAuthorName())
                .bookStatus(bookSaveRequest.getBookStatus()).publisher(bookSaveRequest.getPublisher())
                .lastPageNumber(bookSaveRequest.getLastPageNumber()).totalPage(bookSaveRequest.getTotalPage())
                .category(categoryService.getCategoryById(bookSaveRequest.getCategoryId())).build();

        final Book fromDb = bookRepository.save(book);

        return new BookListItemResponse.Builder().id(fromDb.getId()).title(fromDb.getTitle())
                .authorName(fromDb.getAuthorName()).bookStatus(fromDb.getBookStatus())
                .publisher(fromDb.getPublisher()).lastPageNumber(fromDb.getLastPageNumber())
                .totalPage(fromDb.getTotalPage()).categoryName(fromDb.getCategory().getName()).build();

    }

    private static List<BookResponse> convertToResponse(@NotNull List<Book> books) {
        return books.stream().map(b -> new BookResponse.Builder().id(b.getId())
                        .title(b.getTitle()).authorName(b.getAuthorName())
                        .imageUrl(b.getImage().getImageUrl()).build())
                .collect(Collectors.toList());
    }



    public List<BookResponse> listBooks(Integer pageNo, Integer size) {
        List<Book> books = bookRepository.findAll(PageRequest.of(pageNo - 1, size)).getContent();
        return convertToResponse(books);
    }

    public List<BookResponse> searchByCategory(String categoryName) {
        List<Book> books = categoryService.getCategoryByName(categoryName).getBooks();
        return convertToResponse(books);
    }

    public List<BookResponse> searchByBookStatus(BookStatus bookStatus) {
        User user = userService.findUserByUsername(authService.getLoggedInUser().getUsername());

        List<Book> books = bookRepository.getBooksByBookStatusAndUserId(bookStatus,user.getId());
        return convertToResponse(books);
    }

    public List<BookResponse> searchByTitle(String title){
        List<Book> books = bookRepository.getBooksByTitle(title);
        return convertToResponse(books);
    }


}
