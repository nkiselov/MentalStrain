package QuestionGenerators;

public interface QuestionGenerator {
    public Question[] generate(int[] difficulties, int wrong);
    public int maxWrong();
    public String name();
}
