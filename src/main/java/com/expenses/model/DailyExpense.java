package com.expenses.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Model class for Daily Expenses
 */
@Data
@ToString
public class DailyExpense {
     private LocalDate date;
     private String category;
     private double amount;
     private String remark;
}
