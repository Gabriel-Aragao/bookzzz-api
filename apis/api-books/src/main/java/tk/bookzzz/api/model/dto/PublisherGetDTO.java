package tk.bookzzz.api.model.dto;

import lombok.Data;

@Data
public class PublisherGetDTO {
  
  private Long id;
  private String name;
  private int booksCount;
}
