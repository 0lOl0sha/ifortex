package com.ifortex.bookservice.service.impl;

import static java.util.Collections.reverseOrder;

import com.ifortex.bookservice.repository.BookRepository;
import com.ifortex.bookservice.service.BookService;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  @Override
  public Map<String, Long> getBooks() {
    var books = bookRepository.findAll();
    var map = new HashMap<String, Long>();
    books.forEach(book ->
        book.getGenres().forEach(genre -> {
          var value = map.get(genre);
          if (value != null) {
            map.replace(genre, ++value);
          } else {
            map.put(genre, 1L);
          }
        })
    );
    var list = map.entrySet().stream().sorted(reverseOrder(Entry.comparingByValue())).toList();
    var result = new LinkedHashMap<String, Long>();
    for (var entry : list) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }
}
