package data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  private String id;

  private String account;
  private String pin;

  private String fullName;

  private BigDecimal balance;

  private Integer invalidTries;

  /**
   * Use this function to add balance to Customer
   *
   * @param amount
   */
  public void add(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
