package iloveyouboss;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static iloveyouboss.ContrainsSidesTo.*;

public class BearingTest
{

    @Test
    public void failOnNegativeNumber()
    {
        assertThat(new Bearing(10), myTest(100));
    }

    @Test(expected = BearingOutOfRangeException.class)
    public void throwsOnNegativeNumber()
    {
        new Bearing(-1);
    }

    @Test(expected = BearingOutOfRangeException.class)
    public void throwsWhenTooLarge()
    {
        new Bearing(Bearing.MAX + 1);

    }

    @Test
    public void answersValidBearing()
    {
        assertThat(new Bearing(15).angleBetween(new Bearing(12)), equalTo(3));
    }

    @Test
    public void angleBetweenIsNegativeWhenThisBearingSmaller() {
        assertThat(new Bearing(12).angleBetween(new Bearing(15)), equalTo(-3));
    }
}
