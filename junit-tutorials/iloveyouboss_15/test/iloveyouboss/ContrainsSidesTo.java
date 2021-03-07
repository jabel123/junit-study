package iloveyouboss;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class ContrainsSidesTo extends TypeSafeMatcher<Bearing>
{
    private int length;
    public ContrainsSidesTo(int length)
    {
        this.length = length;
    }
    @Override
    public void describeTo (Description description)
    {
        description.appendText("both sides must be <= " + length);
    }

    @Override
    protected boolean matchesSafely (Bearing bearing)
    {
        return bearing.value() > length;
    }

    public static <T>Matcher<Bearing> myTest(int length) {
        return new ContrainsSidesTo(length);
    }
}
