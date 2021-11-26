package MultipleChoice;

public class CategoryScore {
    public int total;
    public int correct;
    public int wrong;
    public int unanswered;
    public int multiple;

    public CategoryScore(int total, int correct, int wrong, int unanswered, int multiple) {
        this.total = total;
        this.correct = correct;
        this.wrong = wrong;
        this.unanswered = unanswered;
        this.multiple = multiple;
    }

    @Override
    public String toString() {
        return "{" +
                "total=" + total +
                ", correct=" + correct +
                ", wrong=" + wrong +
                ", unanswered=" + unanswered +
                ", multiple=" + multiple +
                '}';
    }
}