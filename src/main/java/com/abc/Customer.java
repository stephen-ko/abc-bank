package com.abc;

import java.util.ArrayList;
import java.util.List;

import com.abc.Account.AccountType;

import static java.lang.Math.abs;

public class Customer {
    private String name;
    private List<Account> accounts;

    public Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<Account>();
    }

    public String getName() {
        return name;
    }

    public synchronized Customer openAccount(Account account) {
        accounts.add(account);
        return this;
    }

    public synchronized int getNumberOfAccounts() {
        return accounts.size();
    }

    public synchronized double totalInterestEarned() {
        double total = 0;
        for (Account a : accounts)
            total += a.interestEarned();
        return total;
    }

    // to protect accounts
    public synchronized String getStatement() {
        String statement = null;
        statement = "Statement for " + name + "\n";
        double total = 0.0;
        for (Account a : accounts) {
            statement += "\n" + statementForAccount(a) + "\n";
            total += a.sumAllTransactions();
        }
        statement += "\nTotal In All Accounts " + toDollars(total);
        return statement;
    }

    private synchronized String statementForAccount(Account a) {
        StringBuffer strBuf = new StringBuffer("");

       //Translate to pretty account type
        switch(a.getAccountType()){
            case CHECKING:
                strBuf.append("Checking Account\n");
                break;
            case SAVINGS:
                strBuf.append( "Savings Account\n");
                break;
            case MAXI_SAVINGS:
                strBuf.append("Maxi Savings Account\n");
                break;
        }

        //Now total up all the transactions
        double total = a.printAllTransactions(strBuf);
        
        strBuf.append( "Total " + toDollars(total)) ;

        return strBuf.toString();
    }

    private String toDollars(double d){
        return String.format("$%,.2f", abs(d));
    }
    
   
    // actually, migt be better to place transfer()  placed in Account, but it will require more work than 
    // time allow
    public synchronized boolean transfer(Account sAcct, Account tAcct, double amount)
    {
        boolean  transfer = false;
        
        boolean foundSrc = false;
        boolean foundTarget = false;
        
    	for (Account a : accounts) {
    	    if ( a == sAcct ) foundSrc = true;
    	    
    	    if ( a == tAcct ) foundTarget = true;
    		
    	}
  
    	transfer = (foundSrc && foundTarget);
    	
    	if ( transfer) {
    		
    		sAcct.withdraw(amount);
    		tAcct.deposit(amount);
    	}
    	
    	return transfer;
    	
    }
}
