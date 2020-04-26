package com.expenses.service;

import com.expenses.dao.IExpenseRepository;
import com.expenses.model.DailyExpense;
import com.expenses.model.Expense;
import com.expenses.model.MonthlyExpense;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@Service
public class IExpenseServiceImpl implements IExpenseService{

    @Autowired
    private IExpenseRepository expenseRepository;

    @Override
    public DailyExpense addDailyExpense(DailyExpense dailyExpense) {
        return expenseRepository.addDailyExpense(dailyExpense);
    }

    @Override
    public boolean setMonthlyExpenseLimit(String month, double budget) {
        month = month.toUpperCase();
        if(StringUtils.isEmpty(month)) {
            return expenseRepository.setMonthlyExpenseLimit(null, budget);
        }
        else if (isValidMonth(month) && isNotPastMonth(month)) {
            return expenseRepository.setMonthlyExpenseLimit(month, budget);
        }
        return false;
    }

    private boolean isNotPastMonth(String month) {
        return LocalDate.now().getMonth().compareTo(Month.valueOf(month)) <= 0 ? true : false;
    }

    private boolean isValidMonth(String month) {
        boolean isValid = false;
        for (Month m : Month.values()) {
            if(m.toString().equals(month)) {
                isValid = true;
            }
        }
        return isValid;
    }

    @Override
    public List<MonthlyExpense> retrieveMonthlyExpenses(String month) {
        month = month.toUpperCase();
        if (StringUtils.isEmpty(month) || !isValidMonth(month)) {
            return null;
        }
        return expenseRepository.retrieveMonthlyExpenses(month);
    }

    @Override
    public List<Expense> retrieveYearlyExpenses(int year) {
        return expenseRepository.retrieveYearlyExpenses(year);
    }

    @Override
    public List<DailyExpense> retrieveDailyExpenses(String date) {
        if (StringUtils.isEmpty(date)) {
            return new ArrayList<>();
        }
        return expenseRepository.retrieveDailyExpenses(LocalDate.parse(date));
    }

    @Override
    public List<Expense> retrieveExpenses() {
        return expenseRepository.retrieveExpenses();
    }
}
