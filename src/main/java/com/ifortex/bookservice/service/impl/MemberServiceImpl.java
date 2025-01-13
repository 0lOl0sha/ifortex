package com.ifortex.bookservice.service.impl;

import static java.util.Collections.reverseOrder;

import com.ifortex.bookservice.model.Book;
import com.ifortex.bookservice.model.Member;
import com.ifortex.bookservice.repository.MemberRepository;
import com.ifortex.bookservice.service.MemberService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;

  /**
   * Finds the {@link Member} who read the oldest book in Romance genre and who was most recently
   * registered on the platform
   *
   * @return desired member
   */
  @Override
  public Member findMember() {
    var members = memberRepository.findAll().stream()
        .sorted(
            Comparator.comparing((Member member) -> {
                  var books = member.getBorrowedBooks().stream()
                      .filter(book -> book.getGenres().contains("Romance"))
                      .sorted(Comparator.comparing(Book::getPublicationDate))
                      .toList();
                  return books.isEmpty() ? LocalDateTime.MAX : books.get(0).getPublicationDate();
                }
            ).thenComparing(reverseOrder(Comparator.comparing(Member::getMembershipDate))))
        .toList();
    return members.isEmpty() ? null : members.get(0);
  }

  /**
   * Finds {@link Member}s who register in 2023 year, but didn't read any books
   *
   * @return desired member
   */
  @Override
  public List<Member> findMembers() {
    var members = memberRepository.findAll();
    var latestDate = LocalDate.of(2024, 1, 1);
    var earliestDate = LocalDate.of(2022, 12, 31);
    return members.stream()
        .filter(member ->
            member.getBorrowedBooks().isEmpty()
                && latestDate.isAfter(member.getMembershipDate().toLocalDate())
                && earliestDate.isBefore(member.getMembershipDate().toLocalDate())
        ).toList();
  }
}
