package MultipleChoice;

import QuestionGenerators.QuestionGenerator;

public class QuestionPreset {
    public QuestionGenerator[] generators;
    public int choices;
    public int[][] questionsByDifficultyByType;
    public QuestionArrangement arrangement;

    public QuestionPreset(QuestionGenerator[] generators, int choices, int[][] questionsByDifficultyByType, QuestionArrangement arrangement) {
        this.generators = generators;
        this.choices = choices;
        this.questionsByDifficultyByType = questionsByDifficultyByType;
        this.arrangement = arrangement;
    }
}
