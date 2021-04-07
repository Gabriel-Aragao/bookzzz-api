package tk.bookzzz.api.resource;


import java.util.ArrayList;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tk.bookzzz.api.model.Author;
import tk.bookzzz.api.model.Book;
import tk.bookzzz.api.model.Category;
import tk.bookzzz.api.model.Publisher;
import tk.bookzzz.api.model.dto.BookResponseDTO;
import tk.bookzzz.api.model.dto.BookRequestDTO;
import tk.bookzzz.api.service.AuthorService;
import tk.bookzzz.api.service.BookService;
import tk.bookzzz.api.service.CategoryService;
import tk.bookzzz.api.service.PublisherService;

@RestController
public class BookResource {
  
  @Autowired
  private BookService bookService;
  
  @Autowired
  private CategoryService categoryService;
  
  @Autowired
  private PublisherService publisherService;

  @Autowired
  private AuthorService authorService;


  @Autowired
  private ModelMapper modelMapper;

  @PostMapping(path= Paths.Books.PATH)
  public ResponseEntity<BookResponseDTO> saveBook(@RequestBody BookRequestDTO book){
    Book newBook = modelMapper.map(book, Book.class);

    newBook.setAuthors(new ArrayList<Author>());
    try {
      Category category = categoryService.findById(book.getCategory());
      category.getBooks().add(newBook);
      newBook.setCategory(category);

      Publisher publisher = publisherService.findById(book.getPublisher());
      publisher.getBooks().add(newBook);
      newBook.setPublisher(publisher);
      
      for(Long id: book.getAuthorsIds()){
        Author author = authorService.findById(id);
        author.getBooks().add(newBook);
        newBook.getAuthors().add(author);
      }
      
      Book savedBook = bookService.save(newBook);
      return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(savedBook, BookResponseDTO.class));
      
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BookResponseDTO());
    }
  }

  @GetMapping(path = Paths.Books.PATH)
  public ResponseEntity<Page<BookResponseDTO>> getBooks(){
    return getPageBooks(0);
  }

  @GetMapping(path = Paths.Books.PATH + "/page/{pgNumber}")
  public ResponseEntity<Page<BookResponseDTO>> getPageBooks(@PathVariable int pgNumber){
    Page<Book> page  = bookService.findBooks(pgNumber, 5);
    Page<BookResponseDTO> pageDTO = page.map(Book -> modelMapper.map(Book, BookResponseDTO.class));
    return ResponseEntity.status(HttpStatus.OK).body(pageDTO);
  }

  @PutMapping(path = Paths.Books.PATH + "/{id}")
  public ResponseEntity<BookResponseDTO> update(@PathVariable long id, @RequestBody BookRequestDTO book){
    Book newBook = modelMapper.map(book, Book.class);
    newBook.setId(id);
    newBook.setAuthors(new ArrayList<Author>());
    try {
      Book helpBook = bookService.findById(id);
      for(Author author: helpBook.getAuthors()){
        author.getBooks().remove(helpBook);
      }
      for(Long authorId: book.getAuthorsIds()){
        Author author = authorService.findById(authorId);
        author.getBooks().add(newBook);
        newBook.getAuthors().add(author);
      }
      
      Book savedBook = bookService.update(newBook);
      return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(savedBook, BookResponseDTO.class));
      
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BookResponseDTO());
    }
  }

  @DeleteMapping(path = Paths.Books.PATH + "/{id}")
  public ResponseEntity<BookResponseDTO> deleteBook(@PathVariable long id){
    try {
      Book book = bookService.findById(id);
      for(Author author: book.getAuthors()){
        author.getBooks().remove(book);
      }
      bookService.delete(id);
      return ResponseEntity.status(HttpStatus.OK).body(modelMapper.map(book, BookResponseDTO.class));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BookResponseDTO());
    }
    
  }
}
