package tk.bookzzz.api.repository;

import tk.bookzzz.api.model.Book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>{
  
  Page<Book> findAllByTitle(String search, Pageable pageable);

}
