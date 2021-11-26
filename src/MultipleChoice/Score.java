package MultipleChoice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Score {
    String recipient;
    Map<String,CategoryScore> scores;
    CategoryScore total;
    CompositeScorer sc;

    public Score(String recipient, Map<String, CategoryScore> scores, CategoryScore total, CompositeScorer sc) {
        this.recipient = recipient;
        this.scores = scores;
        this.total = total;
        this.sc = sc;
    }

    public static void saveToFile(Score s, File f) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        ArrayList<String> types = new ArrayList<>(s.scores.keySet());
        Collections.sort(types);
        for(String t:types){
            pw.println(t+": "+s.scores.get(t).toString());
        }
        pw.println("Total: "+s.total);
        pw.println("Composite: "+s.sc.composite(s));
        pw.flush();
        pw.close();
    }
}
