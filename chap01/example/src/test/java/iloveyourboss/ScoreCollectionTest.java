package iloveyourboss;

import iloveyouboss.ScoreCollection;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class ScoreCollectionTest
{
    @Test
    public void test()
    {
//        fail("Not yet implemented");
    }

    @Test
    public void answerArithmeticMeanOfTwoNumbers()
    {
        // 준비
        ScoreCollection collection = new ScoreCollection();
        collection.add(() -> 5);
        collection.add(() -> 7);

        // 실행
        int actualResult = collection.arithmeticMean();

        // 단언
        assertThat(actualResult, equalTo(6));
    }
}
