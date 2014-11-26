package com.abc;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.abc.Transaction;

public class Account {

    // much safer than static int
    public static enum AccountType { CHECKING, SAVINGS, MAXI_SAVINGS ;} ;

    private final AccountType accountType;
    private List<Transaction> transactions;
    private double balance = 0.0;

    public Account(AccountType accountType) {
        this.accountType = accountType;
        this.transactions = new ArrayList<Transaction>();
        this.balance = 0.0;
    }
 
    // it really need to lock transactions, but in this case 
    // synchronized will do, don't need to overkill
    public  synchronized void  deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
            transactions.add(new Transaction(amount));
            balance += amount;
        }
    }

    public  synchronized void withdraw(double amount) {
    	if (amount <= 0) {
    		throw new IllegalArgumentException("amount must be greater than zero");
    	} else {
    		if ( balance < amount) {
    			throw new IllegalArgumentException("Try to withdraw more than balance ");
    		}
    		transactions.add(new Transaction(-amount));
    		balance -= amount;
    		
    	}
    }

 
    // synchronized is to safe guard accountType
    public synchronized double interestEarned() {
   
        double amount = sumAllTransactions();
        double accurInt = 0.0;
        switch(accountType){
            case SAVINGS:
                if (amount <= 1000) {
                    accurInt = amount * 0.001;
                } 
                else {
                    accurInt =  1 + (amount-1000) * 0.002;
                }
                break;
            case MAXI_SAVINGS:
                if (amount <= 1000) {
                    accurInt = amount * 0.02;
                }
                if (amount <= 2000) {
                    accurInt = 20 + (amount-1000) * 0.05;
                }
                
                accurInt  = 70 + (amount-2000) * 0.1;
                break;
            default:
            	// probably need to logging error instead fall through as CHECKING
            	// but in this scope, it is OK to let it slide
                accurInt = amount * 0.001;
                break;
        }
        
        return accurInt;
    }
    
    // accept Date, more flexible, could use past Date
    public synchronized double interestEarnedWithDays(Date targetDate) 
    {
     
        
        if ( accountType ==  AccountType.SAVINGS) {
        	return interestEarnedSavings(targetDate);
        }
        else if ( accountType ==  AccountType.MAXI_SAVINGS) {
        	return interestEarnedMaxi(targetDate);
        }
        else {
        	return interestEarnedChecking(targetDate);
        	
        }
            
    }


    public synchronized double interestEarnedChecking(Date targetDate) 
    {
        double accurInt = 0.0;
     
        int size = transactions.size() ;
        Date day1 = transactions.get(0).getTransactionDate();
        Date day2 = null;
        int days = 0;
        
        double runningBalance = transactions.get(0).getAmount();
        boolean cutOff = targetDate.before(day1) ;
        
        for ( int i = 1; i < size ; i++ ) {
        	
        	day2 = transactions.get(i).getTransactionDate();
        	
        	if ( targetDate.before(day2) ) {
        		day2 = targetDate;
        		cutOff = true;
        	}
            days = daysDiff(day1, day2);
            
                   
            if ( days > 0 ) {
               accurInt += runningBalance * 0.001 * ( days / 365.0)  ; 
            }
            
            runningBalance += transactions.get(i).getAmount();
         
        }        	
         
        if ( !cutOff) {
        	days = daysDiff(day2, targetDate);
        	accurInt += runningBalance * 0.001 * ( days / 365.0)  ;
        }
        
        return accurInt;
    }   
    
    // TODO : no enough time to code this, already use 3 hours ...
    public synchronized double interestEarnedSavings(Date targetDate) 
    {
        double accurInt = 0.0;
     
     
        return accurInt;
    }   
    
   
    // tragteDate not necessary today, maybe past date
    public synchronized double interestEarnedMaxi(Date targetDate) 
    {
        double accurInt = 0.0;
        
        boolean noWithdraw = true;
        for (Transaction t: transactions) {
            int days = daysDiff(targetDate, t.getTransactionDate());
            if ( days < 10 && (t.getAmount() < 0 ) ) {
               noWithdraw = false; 
            }
        }        	
        
        double amount = sumAllTransactions(targetDate);
        double intRate = 0.001;
        if ( noWithdraw) {
        	intRate = 0.05;
        }
      
        int days = daysDiff(targetDate, transactions.get(0).getTransactionDate());
        
        accurInt += amount * intRate * ( days / 365.0)  ; 
         
        return accurInt;
    }   
    public int daysDiff(Date from, Date to) 
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(from);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        Calendar cal1 = Calendar.getInstance();
        cal1.set(year, month, day ); //Year, month and day of month
        Date d1 = cal1.getTime();
        
        cal.setTime(to);
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.set(year, month, day ); //Year, month and day of month
        Date d2 = cal1.getTime();
        
        
        long diff = d2.getTime() - d1.getTime();
        int days = 0;
        if ( d2.after(d1) ) {		
           days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) ;
        }
    	return days;
    }
    
    public synchronized double sumAllTransactions( ) 
    {
        double amount = 0.0;
        for (Transaction t: transactions)
            amount += t.getAmount();
        return amount;
    }
    
    
    public synchronized double sumAllTransactions(Date TargetDate ) 
    {
        double amount = 0.0;
        for (Transaction t: transactions) {
        
        	if ( t.getTransactionDate().before(TargetDate))
        		continue;
        
            amount += t.getAmount();
        
        }
        
        return amount;
    }
    public synchronized double printAllTransactions(StringBuffer s )
    {
        double total = 0.0;
    	for (Transaction t : transactions) {
    		s.append( "  " + (t.getAmount() < 0 ? "withdrawal" : "deposit") + " " + toDollars(t.getAmount()) + "\n");
    		total += t.getAmount();
    	}
    	
    	return total;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }

    private String toDollars(double d){
        return String.format("$%,.2f", abs(d));
    }
 
    
}
