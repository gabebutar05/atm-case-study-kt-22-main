package data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Data
@Builder
@AllArgsConstructor
public class Bank {
  private String id;
  private String name;
  private boolean depositFeature;
  private BigDecimal maxExpensePerWithdrawal;
  private BigDecimal maxExpensePerUserDaily;
  @Builder.Default
  private Set<Customer> customers = new HashSet<>();
  @Builder.Default
  private Set<Transaction> transactions = new HashSet<>();

  public Optional<Customer> findCustomerByAccount(String account) {
    return customers.stream().filter(item -> account.equals(item.getAccount())).findAny();
  }

  public Set<Transaction> findAllTransactionsByAccount(String account) {
    return transactions.stream().filter(item -> account.equals(item.getCustomer().getAccount()))
            .collect(Collectors.toSet());
  }
}
