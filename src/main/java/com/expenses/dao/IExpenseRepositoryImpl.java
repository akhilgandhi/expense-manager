package com.expenses.dao;

import com.expenses.model.DailyExpense;
import com.expenses.model.Expense;
import com.expenses.model.MonthlyExpense;
import com.expenses.model.YearlyExpense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class IExpenseRepositoryImpl implements IExpenseRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public DailyExpense addDailyExpense(DailyExpense dailyExpense) {
        if(!mongoTemplate.collectionExists(Expense.class)) {
            mongoTemplate.createCollection(Expense.class);
        }
        Expense result = null;
        String month = dailyExpense.getDate().getMonth().toString();
        int year = dailyExpense.getDate().getYear();
        List<Expense> expensesByYear = getExpensesByYear(year);
        if(CollectionUtils.isEmpty(expensesByYear)) {
            Expense expense = buildExpense(dailyExpense, month, year);
            result = mongoTemplate.insert(expense);
        }
        else {
            List<Expense> expensesByMonth = getExpensesByMonth(month, year);
            if (CollectionUtils.isEmpty(expensesByMonth)) {
                MonthlyExpense monthlyExpense = buildMonthlyExpense(dailyExpense, month);
                Query query = new Query();
                query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(year)));
                Update update = new Update();
                update.push("yearlyExpense.monthlyExpenseList", monthlyExpense);
                update.set("yearlyExpense.totalExpense",
                        expensesByYear.get(0).getYearlyExpense().getTotalExpense() + dailyExpense.getAmount());
                result = mongoTemplate.findAndModify(query, update, Expense.class);
            }
            else {
                YearlyExpense yearlyExpense = expensesByMonth.get(0).getYearlyExpense();
                int monthIndex = getDBMonthIndex(yearlyExpense, month);
                Query query = new Query();
                query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(year))
                        .andOperator(Criteria.where("yearlyExpense.monthlyExpenseList.month").is(month)));
                Update update = new Update();
                update.push("yearlyExpense.monthlyExpenseList." + month + ".dailyExpenseList", dailyExpense);
                update.set("yearlyExpense.monthlyExpenseList." + month + ".totalExpense",
                        yearlyExpense.getMonthlyExpenseList().get(monthIndex).getTotalExpense() + dailyExpense.getAmount());
                update.set("yearlyExpense.totalExpense", yearlyExpense.getTotalExpense() + dailyExpense.getAmount());
                result = mongoTemplate.findAndModify(query, update, Expense.class);
            }
        }
        if (null != result) {
            return dailyExpense;
        }
        return null;
    }

    private int getDBMonthIndex(YearlyExpense yearlyExpense, String month) {
        int monthIndex = 0;
        for (MonthlyExpense expense : yearlyExpense.getMonthlyExpenseList()) {
            if (expense.getMonth().equals(month)) {
                break;
            }
            monthIndex++;
        }
        return monthIndex;
    }

    private Expense buildExpense(DailyExpense dailyExpense, String month, int year) {
        Expense expense = new Expense();

        YearlyExpense yearlyExpense = new YearlyExpense();
        yearlyExpense.setMonthlyExpenseList(Arrays.asList(buildMonthlyExpense(dailyExpense, month)));
        yearlyExpense.setTotalExpense(dailyExpense.getAmount());
        yearlyExpense.setYear(String.valueOf(year));

        expense.setYearlyExpense(yearlyExpense);

        return expense;
    }

    private MonthlyExpense buildMonthlyExpense(DailyExpense dailyExpense, String month) {
        MonthlyExpense monthlyExpense = new MonthlyExpense();
        monthlyExpense.setMonth(month);
        monthlyExpense.setMonthlyBudgetLimit(0);
        monthlyExpense.setDailyExpenseList(Arrays.asList(dailyExpense));
        monthlyExpense.setTotalExpense(dailyExpense.getAmount());

        return monthlyExpense;
    }

    private List<Expense> getExpensesByYear(int year) {
        Query query = new Query();
        query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(year)));
        return mongoTemplate.find(query, Expense.class);
    }

    private List<Expense> getExpensesByMonth(String month, int year) {
        Query query = new Query();
        query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(year))
                .andOperator(Criteria.where("yearlyExpense.monthlyExpenseList.month").is(month)));
        return mongoTemplate.find(query, Expense.class);
    }

    @Override
    public boolean setMonthlyExpenseLimit(String month, double budget) {
        boolean isBudgetLimitSet = false;
        Expense result = null;
        int year = LocalDate.now().getYear();
        if (null == month) {
            month = LocalDate.now().getMonth().toString();
        }
        List<Expense> expensesByYear = getExpensesByYear(year);
        if ((CollectionUtils.isEmpty(expensesByYear))) {
            DailyExpense dailyExpense = new DailyExpense();
            Expense expense = buildExpense(dailyExpense, month, year);
            expense.getYearlyExpense().getMonthlyExpenseList().get(0).setMonthlyBudgetLimit(budget);
            expense.getYearlyExpense().getMonthlyExpenseList().get(0).setDailyExpenseList(new ArrayList<>());
            result = mongoTemplate.insert(expense);
        }
        else {
            YearlyExpense yearlyExpense = expensesByYear.get(0).getYearlyExpense();
            int monthIndex = getDBMonthIndex(yearlyExpense, month);
            Query query = new Query();
            query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(year))
                    .andOperator(Criteria.where("yearlyExpense.monthlyExpenseList.month").is(month)));
            Update update = new Update();
            update.set("yearlyExpense.monthlyExpenseList." + monthIndex + ".monthlyBudgetLimit", budget);
            result = mongoTemplate.findAndModify(query, update, Expense.class);
        }

        if (null != result) {
            isBudgetLimitSet = true;
        }
        return isBudgetLimitSet;
    }

    @Override
    public List<MonthlyExpense> retrieveMonthlyExpenses(String month) {
        Query query = new Query();
        query.addCriteria(Criteria.where("yearlyExpense.year").is(String.valueOf(LocalDate.now().getYear()))
                .andOperator(Criteria.where("yearlyExpense.monthlyExpenses.month").is(month)));
        List<Expense> expenses = mongoTemplate.find(query, Expense.class);
        if (expenses.size() > 0) {
            return expenses.get(0).getYearlyExpense().getMonthlyExpenseList().stream()
                    .filter(monthlyExpense -> monthlyExpense.getMonth().equalsIgnoreCase(month))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Expense> retrieveYearlyExpenses(int year) {
        return getExpensesByYear(year);
    }

    @Override
    public List<DailyExpense> retrieveDailyExpenses(LocalDate date) {
        List<DailyExpense> dailyExpenses = new ArrayList<>();
        List<MonthlyExpense> monthlyExpenses = retrieveMonthlyExpenses(date.getMonth().toString());
        if (monthlyExpenses.size() > 0) {
            for (DailyExpense dailyExpense : monthlyExpenses.get(0).getDailyExpenseList()) {
                if (dailyExpense.getDate().compareTo(date) == 0) {
                    dailyExpenses.add(dailyExpense);
                }
            }
        }
        return dailyExpenses;
    }

    @Override
    public List<Expense> retrieveExpenses() {
        return mongoTemplate.findAll(Expense.class);
    }
}
