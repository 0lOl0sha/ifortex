package com.ifortex.bookservice.service.impl;

import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.repository.BookRepository;
import com.ifortex.bookservice.service.BookService;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

  private final BookRepository bookRepository;

  /**
   * Returns the count of the {@link Book}s by each genre, ordered from the genre with the most
   * books to the least
   *
   * @return desired result as a Map where key is book genre and value is count of the books
   */
  @Override
  public Map<String, Long> getBooks() {
    var map = countBookForEachGenre();
    return map.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
            .thenComparing(Map.Entry.comparingByKey()))
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            Map.Entry::getValue,
            (e1, e2) -> e1,
            LinkedHashMap::new));
  }

  /* Implementation via ForEach.
     Pros: one pass through collection.
     Cons: explicit map creation. */
  private Map<String, Long> countBookForEachGenre() {
    var bookList = bookRepository.findAll();
    var genreMap = new TreeMap<String, Long>();
    bookList.forEach(book ->
        book.getGenres().forEach(genre ->
            genreMap.put(genre, Optional.ofNullable(genreMap.get(genre)).orElse(0L) + 1)
        )
    );
    return genreMap;
  }

  /* Implementation via Stream.
     Pros: more readability due to logical split.
     Cons: two passes through collection. */
  private Map<String, Long> countBookForEachGenreV2() {
    var books = bookRepository.findAll();

    var genres = books.stream()
        .map(Book::getGenres)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());

    return genres.stream()
        .collect(Collectors.toMap(Function.identity(),
            genre -> books.stream()
                .filter(book -> book.getGenres().contains(genre))
                .count()));
  }
}
