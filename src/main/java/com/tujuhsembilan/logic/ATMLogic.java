package com.tujuhsembilan.logic;

import data.constant.Feature;
import data.constant.TransactionType;
import data.model.ATM;
import data.model.Bank;
import data.model.Customer;
import data.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Scanner;

import static com.tujuhsembilan.logic.ConsoleUtil.*;

public class ATMLogic {

  private static final BigDecimal INTER_BANK_TRANSACTION_FEE = BigDecimal.valueOf(2500);

  private static Scanner in = ConsoleUtil.in;

  public static void login() {
    System.out.print("Masukkan nomor rekening: ");
    String accountNumber = in.next();
    System.out.print("Masukkan PIN: ");
    String pin = in.next();

    Optional<Customer> customer = getCurrentBank().findCustomerByAccount(accountNumber);
    if (customer.isPresent() && customer.get().getPin().equals(pin)) {
      System.out.println("Login berhasil. Selamat datang, " + customer.get().getFullName() + "!");
      showMenu(customer.get());
    } else {
      System.out.println("Login gagal. Nomor rekening atau PIN salah.");
      delay();
    }
  }

  private static Bank getCurrentBank() {
    return ATM.getInstance().getBank();
  }

  private static void showMenu(Customer customer) {
    boolean loggedIn = true;
    while (loggedIn) {
      printClear();
      printDivider();
      System.out.println(" Menu Utama");
      printDivider();
      int num = 1;
      for (Feature feature : Feature.values()) {
        System.out.println(" " + num + ". " + feature.name());
        num++;
      }
      printDivider("-");
      System.out.println(" 0. Keluar");
      printDivider();

      System.out.print(" > ");
      int selection = in.nextInt();
      switch (selection) {
        case 1:
          accountBalanceInformation(customer);
          break;
        case 2:
          moneyWithdrawal(customer);
          break;
        case 3:
          phoneCreditsTopUp(customer);
          break;
        case 4:
          electricityBillsToken(customer);
          break;
        case 5:
          accountMutation(customer);
          break;
        case 6:
          moneyDeposit(customer);
          break;
        case 0:
          loggedIn = false;
          break;
        default:
          System.out.println("Pilihan tidak valid.");
          delay();
          break;
      }
    }
  }

  public static void accountBalanceInformation(Customer customer) {
    System.out.println("Informasi Saldo");
    System.out.println("Saldo Anda saat ini: " + formatCurrency(customer.getBalance()));
    delay();
  }

  public static void moneyWithdrawal(Customer customer) {
    System.out.println("Tarik Uang");

    System.out.print("Masukkan jumlah yang ingin ditarik: ");
    BigDecimal withdrawalAmount = in.nextBigDecimal();

    if (withdrawalAmount.remainder(BigDecimal.TEN).compareTo(BigDecimal.ZERO) != 0) {
      System.out.println("Jumlah yang ingin ditarik harus dalam kelipatan 10.000.");
      delay();
      return;
    }

    if (withdrawalAmount.compareTo(BigDecimal.valueOf(2500000)) > 0) {
      System.out.println("Jumlah yang ingin ditarik melebihi batas transaksi maksimum per transaksi (Rp2.500.000,00).");
      delay();
      return;
    }

    BigDecimal atmBalance = ATM.getInstance().getBalance();
    if (withdrawalAmount.compareTo(atmBalance) > 0) {
      System.out.println("Maaf, saldo ATM tidak mencukupi untuk transaksi ini.");
      delay();
      return;
    }

    Bank currentBank = getCurrentBank();
    BigDecimal maxExpensePerUserDaily = currentBank.getMaxExpensePerUserDaily();
    BigDecimal userTotalDailyExpense = currentBank.findAllTransactionsByAccount(customer.getAccount())
            .stream()
            .filter(transaction -> transaction.getType() == TransactionType.WITHDRAWAL)
            .map(Transaction::getExpense)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    if (userTotalDailyExpense.add(withdrawalAmount).compareTo(maxExpensePerUserDaily) > 0) {
      System.out.println("Jumlah yang ingin ditarik melebihi batas transaksi harian maksimum (Rp5.000.000,00) Anda.");
      delay();
      return;
    }

    BigDecimal maxExpensePerWithdrawal = currentBank.getMaxExpensePerWithdrawal();
    if (withdrawalAmount.compareTo(maxExpensePerWithdrawal) > 0) {
      System.out.println("Jumlah yang ingin ditarik melebihi batas transaksi maksimum per transaksi (Rp2.500.000,00).");
      delay();
      return;
    }

    BigDecimal remainingBalance = customer.getBalance().subtract(withdrawalAmount);
    if (remainingBalance.compareTo(BigDecimal.TEN) < 0) {
      System.out.println("Transaksi gagal. Sisa saldo minimal harus Rp10.000,00.");
      delay();
      return;
    }

    customer.setBalance(remainingBalance);
    ATM.getInstance().subtract(withdrawalAmount);

    Transaction withdrawalTransaction = Transaction.builder()
            .timestamp(LocalDateTime.now())
            .customer(customer)
            .type(TransactionType.WITHDRAWAL)
            .expense(withdrawalAmount)
            .build();
    currentBank.getTransactions().add(withdrawalTransaction);

    System.out.println("Transaksi berhasil.");
    System.out.println("Sisa saldo Anda: " + formatCurrency(remainingBalance));
    delay();
  }

  public static void phoneCreditsTopUp(Customer customer) {
    System.out.println("Isi Ulang Pulsa Telepon");

    // Input nomor telepon
    System.out.print("Masukkan nomor telepon: ");
    String phoneNumber = in.next();

    // Validasi panjang nomor telepon
    if (phoneNumber.length() < 3 || phoneNumber.length() > 15) {
      System.out.println("Nomor telepon harus memiliki panjang antara 3 dan 15 digit.");
      delay();
      return;
    }

    // Pilih nominal pulsa
    System.out.println("Pilih nominal pulsa:");
    System.out.println("1. Rp10.000,00");
    System.out.println("2. Rp20.000,00");
    System.out.println("3. Rp50.000,00");
    System.out.println("4. Rp100.000,00");
    System.out.print(" > ");
    int option = in.nextInt();
    BigDecimal topUpAmount;
    switch (option) {
      case 1:
        topUpAmount = BigDecimal.valueOf(10000);
        break;
      case 2:
        topUpAmount = BigDecimal.valueOf(20000);
        break;
      case 3:
        topUpAmount = BigDecimal.valueOf(50000);
        break;
      case 4:
        topUpAmount = BigDecimal.valueOf(100000);
        break;
      default:
        System.out.println("Pilihan tidak valid.");
        delay();
        return;
    }

    // Lakukan proses pengisian pulsa
    // Anda bisa menambahkan logika yang sesuai di sini
  }

  public static void electricityBillsToken(Customer customer) {
    System.out.println("Token Tagihan Listrik");

    // Input nomor tagihan
    System.out.print("Masukkan nomor tagihan listrik: ");
    String billNumber = in.next();

    // Pilih nominal token
    System.out.println("Pilih nominal token:");
    System.out.println("1. Rp50.000,00");
    System.out.println("2. Rp100.000,00");
    System.out.println("3. Rp200.000,00");
    System.out.println("4. Rp500.000,00");
    System.out.print(" > ");
    int option = in.nextInt();
    BigDecimal tokenAmount;
    switch (option) {
      case 1:
        tokenAmount = BigDecimal.valueOf(50000);
        break;
      case 2:
        tokenAmount = BigDecimal.valueOf(100000);
        break;
      case 3:
        tokenAmount = BigDecimal.valueOf(200000);
        break;
      case 4:
        tokenAmount = BigDecimal.valueOf(500000);
        break;
      default:
        System.out.println("Pilihan tidak valid.");
        delay();
        return;
    }

    // Lakukan proses pembelian token listrik
    // Anda bisa menambahkan logika yang sesuai di sini
  }

  public static void accountMutation(Customer customer) {
    System.out.println("Mutasi Rekening (Transfer Dana)");

    // Input nomor rekening tujuan
    System.out.print("Masukkan nomor rekening tujuan: ");
    String destinationAccountNumber = in.next();

    // Input jumlah transfer
    System.out.print("Masukkan jumlah transfer: ");
    BigDecimal transferAmount = in.nextBigDecimal();

    // Validasi saldo mencukupi
    if (transferAmount.compareTo(customer.getBalance()) > 0) {
      System.out.println("Saldo Anda tidak mencukupi untuk transfer ini.");
      delay();
      return;
    }

    // Lakukan proses transfer
    // Anda bisa menambahkan logika yang sesuai di sini
  }

  public static void moneyDeposit(Customer customer) {
    System.out.println("Deposit Uang");

    // Input jumlah yang akan disetorkan
    System.out.print("Masukkan jumlah yang akan disetorkan: ");
    BigDecimal depositAmount = in.nextBigDecimal();

    // Lakukan proses setor uang
    // Anda bisa menambahkan logika yang sesuai di sini
  }
  private static String formatCurrency(BigDecimal amount) {
    // Format mata uang ke string standar Rupiah dalam Bahasa Indonesia
    return "Rp" + amount.setScale(2).toString();
  }
}
