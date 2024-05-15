package data.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ATM {
  private static ATM instance;

  private String id;
  private Bank bank;
  private BigDecimal balance;

  public ATM(Bank bank) {
    this.bank = bank;
  }

  public void start() {
    System.out.println("ATM started for bank: " + bank.getName());

  }

  // Static method to get singleton instance
  public static ATM getInstance() {
    if (instance == null) {
      throw new IllegalStateException("ATM instance has not been initialized.");
    }
    return instance;
  }
  public void subtract(BigDecimal amount) {
    this.balance = this.balance.subtract(amount);
  }
}
