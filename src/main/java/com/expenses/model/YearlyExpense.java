package com.expenses.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class YearlyExpense {
    private String year;
    private double totalExpense;
    private List<MonthlyExpense> monthlyExpenseList;
}
