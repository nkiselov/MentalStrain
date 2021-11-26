package MultipleChoice.QuestionGenerators;

import java.util.Arrays;

public class Question {
    public String text;
    public String correct;
    public String[] wrong;
    public int difficulty;
    public String genName;

    public Question(String text, String correct, String[] wrong, int difficulty, String genName) {
        this.text = text;
        this.correct = correct;
        this.wrong = wrong;
        this.difficulty = difficulty;
        this.genName = genName;
    }

    public Question(String text, String correct, Integer[] wrong, int difficulty, String genName) {
        String[] wrongs = new String[wrong.length];
        for(int i=0; i<wrong.length; i++){
            wrongs[i] = String.valueOf(wrong[i]);
        }
        this.text = text;
        this.correct = correct;
        this.wrong = wrongs;
        this.difficulty = difficulty;
        this.genName = genName;
    }

    @Override
    public String toString() {
        return "Question{"+
                ", text='" + text + '\'' +
                ", correct='" + correct + '\'' +
                ", wrong=" + Arrays.toString(wrong) +
                ", difficulty=" + difficulty +
                ", genName='" + genName + '\'' +
                '}';
    }
}
