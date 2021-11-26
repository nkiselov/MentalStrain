package QuestionGenerators;

import java.util.*;

public class MultiplicationQuestion implements QuestionGenerator{

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
            double plim = 0.8*(1-Math.exp(-difficulty*0.5));
            int l = Math.max((int)(plim*Math.sqrt(max)),1);
            for(int qn=0; qn<difficulties[difficulty]; qn++){
                int a;
                int b;
                do {
                    a = (int)(Math.random()*Math.sqrt(max));
                    b = (int)(Math.random()*max/a);
                    if(Math.random()>0.5){
                        int temp = a;
                        a = b;
                        b = temp;
                    }
                } while (a<=l || b<=l || a*b > max || problems.contains(new Tuple<>(a, b)) || problems.contains(new Tuple<>(b, a)));
                problems.add(new Tuple<>(a,b));
                int ans = a*b;
                HashSet<Integer> comb = new HashSet<>();
                List<Integer> wrongs1 = QuestionUtils.generateBaseWrongs(a,max/b,Math.max(l/b,1),Math.min(max/b-Math.max(l/b,1),wrong/2+1));
                for(Integer i:wrongs1){
                    comb.add(i*b);
                }
                List<Integer> wrongs2 = QuestionUtils.generateBaseWrongs(b,max/a,Math.max(l/a,1),Math.min(max/a-Math.max(l/a,1),wrong/2+1));
                for(Integer i:wrongs2){
                    comb.add(i*a);
                }
                while(comb.size()<wrong){
                    int k = (int)(Math.random()*(max-l))+l+1;
                    if(k != ans){
                        comb.add(k);
                    }
                }
                List<Integer> pick = new ArrayList<>(comb);
                while(pick.size()>wrong){
                    pick.remove((int)(Math.random()*pick.size()));
                }
                questions[q] = new Question(a+" * "+b+" = ?",String.valueOf(ans),pick.toArray(new Integer[0]),difficulty,name());;
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
        return "Multiplication";
    }
}
