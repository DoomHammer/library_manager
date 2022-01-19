package com.km.librarydata.controllers;

import com.km.librarydata.contracts.BookDto;
import com.km.librarydata.model.Book;
import com.km.librarydata.model.Book_;
import com.km.librarydata.services.BookService;
import com.km.librarydata.wrappers.BookCreationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.metamodel.SingularAttribute;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity getBooks(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "isbn",required = false) String isbn,
            @RequestParam(value = "book_description",required = false) String book_description,
            @RequestParam(value = "author",required = false) String author,
            @RequestParam(value = "publisher",required = false) String publisher,
            @RequestParam(value = "publish_year",required = false) Integer publishYear
    ){
        Map<SingularAttribute<Book, ?>, Object> params = new HashMap<>();

        if(title != null) params.put(Book_.title, title);
        if(isbn != null) params.put(Book_.isbn, isbn);
        if(publishYear != null) params.put(Book_.publishYear, publishYear);
        if(book_description != null) params.put(Book_.bookDescription, book_description);
        if(author != null) params.put(Book_.author, author);
        if(publisher != null) params.put(Book_.publisher, publisher);


        return ResponseEntity.ok(bookService.getBooksFromDB(params));
    }

    @PostMapping
    public ResponseEntity<BookDto> addBook(@RequestBody BookDto bookDto){
        bookService.putBookToRepository(bookDto);
        return new ResponseEntity<>(bookDto, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public String showAll(Model model) {
        model.addAttribute("books", bookService.getBooks());
        return "allBooks";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        BookCreationDto booksForm = new BookCreationDto();
        booksForm.addBook(new BookDto());
        model.addAttribute("form", booksForm);
        return "createBooksForm";
    }

    @PostMapping("/save")
    public String saveBooks(@ModelAttribute BookCreationDto form, Model model) {

        form.getBooks().forEach(bookService::putBookToRepository);

        model.addAttribute("books", bookService.getBooks());
        return "redirect:/books/all";
    }

}
