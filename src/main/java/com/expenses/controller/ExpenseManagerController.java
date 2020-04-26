package com.expenses.controller;

import com.expenses.model.DailyExpense;
import com.expenses.model.Expense;
import com.expenses.model.MonthlyExpense;
import com.expenses.service.IExpenseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/expenses")
@Api
@Slf4j
public class ExpenseManagerController {

    @Autowired
    private IExpenseService expenseService;

    @ApiOperation(value = "Add Daily Expenses", response = DailyExpense.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "Content-Type",
                    value = "application/json",
                    required = true,
                    dataType = "string",
                    paramType = "header"
            )
    })
    @PostMapping(value = "/addDailyExpense", consumes = "application/json", produces = "application/json")
    public DailyExpense addDailyExpense(@RequestBody DailyExpense dailyExpense) {
        log.info("Adding daily expenses {0}", dailyExpense.toString());
        return expenseService.addDailyExpense(dailyExpense);
    }

    @ApiOperation(value = "Set Monthly Budget Limit", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "month",
                    value = "JANUARY",
                    required = true,
                    dataType = "string",
                    paramType = "path"
            ),
            @ApiImplicitParam(
                    name = "budgetLimit",
                    value = "0",
                    required = true,
                    dataType = "double",
                    paramType = "path"
            )
    })
    @PostMapping(value = "/setBudgetLimit/{month}/{budgetLimit}")
    public boolean setMonthlyBudgetLimit(@PathVariable String month, @PathVariable double budgetLimit) {
        log.info("Setting monthly budget limit {0} for the month {1}",
                budgetLimit,
                month);
        return expenseService.setMonthlyExpenseLimit(month, budgetLimit);
    }

    @ApiOperation(value = "Set Current month's Budget Limit", response = Boolean.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "budgetLimit",
                    value = "0",
                    required = true,
                    dataType = "double",
                    paramType = "path"
            )
    })
    @PostMapping(value = "/setBudgetLimit/{budgetLimit}")
    public boolean setMonthlyBudgetLimit(@PathVariable double budgetLimit) {
        log.info("Setting monthly budget limit {0}",
                budgetLimit);
        return expenseService.setMonthlyExpenseLimit("", budgetLimit);
    }

    @ApiOperation(value = "Retrieve Daily Expenses", response = List.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "date",
                    value = "YYYY-MM-DD",
                    required = true,
                    dataType = "string",
                    paramType = "path"
            )
    })
    @GetMapping(value = "/retrieveExpensesByDate/{date}", produces = "application/json")
    public List<DailyExpense> retrieveDailyExpenses(@PathVariable String date) {
        log.info("Finding expenses for {0}", date);
        return expenseService.retrieveDailyExpenses(date);
    }

    @ApiOperation(value = "Retrieve Monthly Expenses", response = List.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "month",
                    value = "JANUARY",
                    required = true,
                    dataType = "string",
                    paramType = "path"
            )
    })
    @GetMapping(value = "/retrieveExpensesByMonth/{month}", produces = "application/json")
    public List<MonthlyExpense> retrieveMonthlyExpenses(@PathVariable String month) {
        log.info("Finding monthly expenses for {0}", month);
        return expenseService.retrieveMonthlyExpenses(month);
    }

    @ApiOperation(value = "Retrieve Yearly Expenses", response = List.class)
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "year",
                    value = "YYYY",
                    required = true,
                    dataType = "integer",
                    paramType = "path"
            )
    })
    @GetMapping(value = "/retrieveExpensesByYear/{year}", produces = "application/json")
    public List<Expense> retrieveYearlyExpenses(@PathVariable int year) {
        log.info("Finding yearly expenses for {0}", year);
        return expenseService.retrieveYearlyExpenses(year);
    }

    @ApiOperation(value = "Retrieve All Expenses", response = List.class)
    @GetMapping(value = "/retrieveAllExpenses", produces = "application/json")
    public List<Expense> retrieveAllExpenses() {
        log.info("Finding all the expenses");
        return expenseService.retrieveExpenses();
    }
}
