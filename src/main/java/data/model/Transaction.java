package data.model;

import data.constant.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Transaction {
  private String id;
  private LocalDateTime timestamp;
  private Customer customer;
  private TransactionType type;
  private BigDecimal expense;
}
