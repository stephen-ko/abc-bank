package com.abc;


import java.util.Date;

public class Transaction {
    private  double amount = 0.0;

    private Date transactionDate = null;

    public Transaction(double amount) {
        this.amount = amount;
        this.transactionDate = DateProvider.getInstance().now();
    }
    
    // so somebody else look at it
    public Date getTransactionDate()
    {
    	return transactionDate;
    }
    
    public double getAmount() 
    {
    	return amount;
    }

}
