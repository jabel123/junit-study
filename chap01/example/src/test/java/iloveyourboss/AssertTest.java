package iloveyourboss;

import iloveyouboss.Account;
import iloveyouboss.InsufficientFundsException;
import org.hamcrest.core.IsSame;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AssertTest
{
    private Account account;

    @Before
    public void createAccount()
    {
        account = new Account("an account name");
        account.deposit(100);
    }

    @Test
    public void hasPositiveBalance()
    {
        account.deposit(50);
        assertTrue(account.hasPositiveBalance());
    }

    @Test
    public void depositIncreasesBalance()
    {
        int initialBalance = account.getBalance();
        account.deposit(100);
        assertTrue(account.getBalance() > initialBalance);
    }

    @Test
    public void assertThatTest()
    {
        // 책에서는 이 부분이 equalsTo로 나와있지만,, deprecated되었나..?? 비슷해 보이는 것으로 사용
        assertThat(account.getBalance(), IsSame.sameInstance(100));
        assertThat(account.getBalance() > 0, is(true));
        assertThat(account.getName(), is(not(nullValue())));
        assertThat(new String[]{"a","b","c"}, is(new String[]{"a","b","c"}));
        assertThat(Arrays.asList(new String[]{"a"}), equalTo(Arrays.asList(new String[]{"a"})));

        assertTrue(Math.abs((2.32 * 3) - 6.96) < 0.0005);

    }

    @Test
    public void testWithWorthlessAssertionComment()
    {
        account.deposit(50);
        assertThat("account balance is 100", account.getBalance(), equalTo(150));
    }

    @Test(expected = InsufficientFundsException.class)
    public void throwWhenWithdrawingTooMuch()
    {
        account.withdraw(100);
    }
    @Test
    public void throwWhenWithdrawingTooMuch2()
    {
        try
        {
            account.withdraw(100);
            fail(); // 예외가 발생안하면 강제로 fail호출하여 실패처리
        }
        catch (InsufficientFundsException e)
        {

        }
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void exceptionRule()
    {
        thrown.expect(InsufficientFundsException.class);
        thrown.expectMessage("balance only 0");

        account.withdraw(100);
    }
}
