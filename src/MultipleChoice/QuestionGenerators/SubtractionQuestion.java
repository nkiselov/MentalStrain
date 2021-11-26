package MultipleChoice.QuestionGenerators;

import java.util.*;

public class SubtractionQuestion implements QuestionGenerator{

    @Override
    public Question[] generate(int[] difficulties, int wrong) {
        int qs = 0;
        for(int o:difficulties){
            qs+=o;
        }
        int q = 0;
        Set<Tuple<Integer,Integer>> problems = new HashSet<>();
        Question[] questions = new Question[qs];
        for(int difficulty=0; difficulty<difficulties.length; difficulty++){
            int max = (int)(4*Math.pow(5,difficulty+1));
            double plim = 0.3*(1-Math.exp(-difficulty*0.5));
            int l = Math.max((int)(plim*max),1);
            for(int qn=0; qn<difficulties[difficulty]; qn++){
                int a;
                int b;
                do {
                    a = (int) (Math.random() * (max-1)) + 2;
                    b = (int) (Math.random() * (a-1)) + 1;
                } while (a<=l || b<=l || a-b<=l || problems.contains(new Tuple<>(a, b)));
                problems.add(new Tuple<>(a,b));
                int ans = a-b;
                List<Integer> wrongs = QuestionUtils.generateBaseWrongs(ans,max,l,wrong);
                questions[q] = new Question(a+" - "+b+" = ?",String.valueOf(ans),wrongs.toArray(new Integer[0]),difficulty,name());;
                q+=1;
            }
        }
        return questions;
    }

    @Override
    public int maxWrong() {
        return 19;
    }

    @Override
    public String name() {
        return "Subtraction";
    }
}
