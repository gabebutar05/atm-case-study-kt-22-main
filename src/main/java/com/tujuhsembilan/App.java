package com.tujuhsembilan;

import data.constant.BankCompany;
import data.model.ATM;
import data.model.Bank;
import data.repository.BankRepo;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tujuhsembilan.logic.ConsoleUtil.*;

public class App {

    public static void main(String[] args) {
        boolean loop = true;
        while (loop) {
            printClear();
            printDivider();
            int num = 1;
            for (String menu : Arrays.asList(BankCompany.values()).stream()
                    .map(item -> "ATM " + item.getName())
                    .collect(Collectors.toList())) {
                System.out.println(" " + num + ". " + menu);
                num++;
            }
            printDivider("-");
            System.out.println(" 0. EXIT");
            printDivider();

            System.out.print(" > ");
            int selection = in.nextInt() - 1;
            if (selection >= 0 && selection < BankCompany.values().length) {
                Optional<Bank> bank = BankRepo.findBankByName(BankCompany.getByOrder(selection).getName());
                bank.ifPresent(value -> new ATM(value).start());
            } else if (selection == -1) {
                loop = false;
            } else {
                System.out.println("Invalid input");
                delay();
            }
        }
    }
}
