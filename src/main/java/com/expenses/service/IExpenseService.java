package com.expenses.service;

import com.expenses.model.DailyExpense;
import com.expenses.model.Expense;
import com.expenses.model.MonthlyExpense;

import java.time.LocalDate;
import java.util.List;

public interface IExpenseService {

    public DailyExpense addDailyExpense(DailyExpense dailyExpense);

    public boolean setMonthlyExpenseLimit(String month, double budget);

    public List<MonthlyExpense> retrieveMonthlyExpenses(String month);

    public List<Expense> retrieveYearlyExpenses(int year);

    public List<DailyExpense> retrieveDailyExpenses(String date);

    public List<Expense> retrieveExpenses();

}
