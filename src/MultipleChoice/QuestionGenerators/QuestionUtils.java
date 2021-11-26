package MultipleChoice.QuestionGenerators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionUtils {

    public static List<Integer> generateBaseWrongs(int ans, int max, int min, int wrong){
        int v = 1;
        HashSet<Integer> wrongs = new HashSet<>();
        int lim = (int)Math.log10(ans);
        HashSet<Integer> potentialWrongsBiggerSet = new HashSet<>();
        HashSet<Integer> potentialWrongsSmallerSet = new HashSet<>();
        for(int i=0; i<lim; i++){
            List<Integer> off = new ArrayList<>();
            while(off.size()<wrong/(i+1)+1){
                int of = (int)Math.round(gaussian(0,2));
                if (!off.contains(of) && of != 0) {
                    off.add(of);
                }
            }
            while(off.size()>0){
                int base;
                if(Math.random()<0.1+0.5*i/(double)(lim) || (potentialWrongsBiggerSet.size() == 0 && potentialWrongsSmallerSet.size() == 0)){
                    base = ans;
                }else{
                    if((Math.random()>0.5 && potentialWrongsBiggerSet.size() != 0) || potentialWrongsSmallerSet.size() == 0){
                        base = pickRandom(potentialWrongsBiggerSet);
                    }else{
                        base = pickRandom(potentialWrongsSmallerSet);
                    }
                }
                int w = v*off.remove((int)(Math.random()*off.size()))+base;
                if(w>=min && w <= max && w != ans){
                    if(w>ans) {
                        potentialWrongsBiggerSet.add(w);
                    }else{
                        potentialWrongsSmallerSet.add(w);
                    }
                }
            }
            v*=10;
        }
        List<Integer> potentialWrongsSmaller = new ArrayList<>(potentialWrongsSmallerSet);
        List<Integer> potentialWrongsBigger = new ArrayList<>(potentialWrongsBiggerSet);
        int minLess = Math.max(0,wrong-(max-ans));
        int maxLess = Math.min(wrong,ans-min);
        if(minLess>maxLess){
            throw new RuntimeException();
        }
        int less =(int)(Math.random()*(maxLess-minLess+1))+minLess;
        while(wrongs.size()<less){
            if(potentialWrongsSmaller.size()>0){
                wrongs.add(potentialWrongsSmaller.remove((int)(Math.random()*potentialWrongsSmaller.size())));
            }else {
                int w = (int) (Math.random() * (max-min+1)) + min;
                if(w>=ans){
                    continue;
                }
                wrongs.add(w);
            }
        }
        while(wrongs.size()<wrong){
            if(potentialWrongsBigger.size()>0){
                wrongs.add(potentialWrongsBigger.remove((int)(Math.random()*potentialWrongsBigger.size())));
            }else {
                int w = (int) (Math.random() * (max-min+1)) + min;
                if(w<=ans){
                    continue;
                }
                wrongs.add(w);
            }
        }
        return new ArrayList<>(wrongs);
    }

    private static <T> T pickRandom(Set<T> set){
        int i = 0;
        int g = (int)(Math.random()*set.size());
        for(T t:set){
            if(i==g){
                return t;
            }
            i++;
        }
        return null;
    }

    private static double gaussian(double mean, double deviation){
        double theta = Math.random()*Math.PI*2;
        double R = Math.sqrt(-2*Math.log(Math.random()));
        return R*Math.cos(theta);
    }
}
