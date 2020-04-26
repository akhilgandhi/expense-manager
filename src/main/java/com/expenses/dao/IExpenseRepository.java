package com.expenses.dao;

import com.expenses.model.DailyExpense;
import com.expenses.model.Expense;
import com.expenses.model.MonthlyExpense;
import com.expenses.model.YearlyExpense;

import java.time.LocalDate;
import java.util.List;

public interface IExpenseRepository {

    public DailyExpense addDailyExpense(DailyExpense dailyExpense);

    public boolean setMonthlyExpenseLimit(String month, double budget);

    public List<MonthlyExpense> retrieveMonthlyExpenses(String month);

    public List<Expense> retrieveYearlyExpenses(int year);

    public List<DailyExpense> retrieveDailyExpenses(LocalDate date);

    public List<Expense> retrieveExpenses();

}
