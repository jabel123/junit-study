package iloveyouboss;

import java.util.concurrent.atomic.AtomicInteger;

public class Account
{
    private final String accountName;
    private AtomicInteger money = new AtomicInteger(0);

    public Account (String accountName)
    {
        this.accountName = accountName;
    }

    public int deposit (int accMoney)
    {
        return money.addAndGet(accMoney);
    }

    public boolean hasPositiveBalance ()
    {
        return money.get() >= 0;
    }

    public int getBalance ()
    {
        return money.get();
    }

    public String getName ()
    {
        return accountName;
    }

    public void withdraw(int i) {
        throw new InsufficientFundsException("balance only 0");
    }
}
