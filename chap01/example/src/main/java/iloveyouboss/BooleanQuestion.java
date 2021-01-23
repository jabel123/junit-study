package iloveyouboss;


public class BooleanQuestion extends Question {
    public BooleanQuestion(int id, String text) {
        super(id, text, new String[] { "No", "Yes" });
    }

    public BooleanQuestion(String text) {
        super(text);
    }

    @Override
    public boolean match(int expected, int actual) {
        return expected == actual;
    }
}
