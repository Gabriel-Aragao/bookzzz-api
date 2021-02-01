package tk.bookzzz.api.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class BookPostDTO {
  
  private String title;
  private String subtitle;

  private List<Long> authorsIds;
  private int year;
  private double price;
  
}
