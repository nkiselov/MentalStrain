package Sheet;

public class ConstantChoiceSetGenerator implements ChoiceSetNameGenerator {
    private char[] choices;

    public ConstantChoiceSetGenerator(char[] choices) {
        this.choices = choices;
    }

    @Override
    public char[] generateSet(int q) {
        return choices;
    }

    @Override
    public String generateName(int q) {
        return String.valueOf(q+1);
    }

    @Override
    public int maxLength() {
        return choices.length;
    }
}
