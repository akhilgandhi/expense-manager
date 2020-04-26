package com.expenses.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Model class for Monthly Expenses
 */
@Data
@ToString
public class MonthlyExpense {
    private String month;
    private double monthlyBudgetLimit;
    private double totalExpense;
    private List<DailyExpense> dailyExpenseList;
}
