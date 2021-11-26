import QuestionGenerators.Question;
import QuestionGenerators.QuestionGenerator;
import Sheet.AnswerSheet;
import Sheet.AnswerSheetContext;
import Sheet.AnswerSheetGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.ToIntFunction;

public class Test {
    public static void generateTest(QuestionPreset qp, DesignPreset dp, File dir) throws IOException, InterruptedException {
        for (QuestionGenerator qg : qp.generators) {
            if (qg.maxWrong() < qp.choices - 1) {
                throw new IllegalArgumentException("Invalid generator: " + qg.getClass() + " can only generate " + qg.maxWrong() + " wrong choices, need " + (qp.choices - 1));
            }
        }
        int qs = 0;
        int[] qsp = new int[qp.questionsByDifficultyByType.length];
        for (int i = 0; i < qp.questionsByDifficultyByType.length; i++) {
            int[] q = qp.questionsByDifficultyByType[i];
            int qi = 0;
            for (int k : q) {
                qi += k;
            }
            qsp[i] = qi;
            qs+=qi;
        }
        Map<String,Integer> genNamesOrder = new HashMap<>();
        for(int i=0; i<qp.generators.length; i++){
            genNamesOrder.put(qp.generators[i].name(),i);
        }
        Question[] questions = new Question[qs];
        int q = 0;
        for (int i = 0; i < qp.questionsByDifficultyByType.length; i++) {
            Question[] questionT = qp.generators[i].generate(qp.questionsByDifficultyByType[i],qp.choices-1);
            for(int j=0; j<qsp[i]; j++){
                questions[q] = questionT[j];
                q+=1;
            }
        }
        for(Question qu:questions){
            for(String w:qu.wrong){
                if(w.equals(qu.correct)){
                    throw new RuntimeException("duplicate: "+qu);
                }
            }
        }
        shuffleArray(questions);
        switch (qp.arrangement) {
            case DIFFICULTY:
                Arrays.sort(questions, Comparator.comparingInt(o -> o.difficulty));
                break;
            case TYPE:
                Arrays.sort(questions, Comparator.comparingInt(o -> genNamesOrder.get(o.genName)));
                break;
            case DIFFICULTY_TYPE:
                Arrays.sort(questions, Comparator.comparingInt((ToIntFunction<Question>) value -> value.difficulty).thenComparingInt(o -> genNamesOrder.get(o.genName)));
                break;
            case TYPE_DIFFICULTY:
                Arrays.sort(questions, Comparator.comparingInt((ToIntFunction<Question>) value -> genNamesOrder.get(value.genName)).thenComparingInt(o -> o.difficulty));
                break;
        }
        dir.mkdir();
        AnswerSheet sh = AnswerSheetGenerator.generateSheet(qs,dp.choices,new Font("Helvetica", Font.PLAIN, 10),dp.sheetColor);
        AnswerSheetContext.writeToFile(sh.ctx,new File(dir.getPath()+"/context.txt"));
        ImageIO.write(sh.img,"png",new File(dir.getPath()+"/sheet.png"));
        int[] correct = new int[qs];
        PrintWriter pw = new PrintWriter(new FileWriter(new File(dir.getPath()+"/test_tex.txt")));
        pw.println("\\documentclass{article}");
        pw.println("\\usepackage{multicol}");
        pw.println("\\usepackage[margin="+dp.margin+"in]{geometry}");
        pw.println("\\begin{document}");
        pw.println("\\begin{multicols*}{"+dp.questionColumns+"}");
        for(int i=0; i<questions.length; i++){
            String[] choices = new String[qp.choices];
            int corr = (int)(Math.random()*qp.choices);
            char[] choiceSet = dp.choices.generateSet(i);
            correct[i] = corr;
            choices[corr] = questions[i].correct;
            List<Integer> open = new ArrayList<>();
            for(int k=0; k<qp.choices; k++){
                if(k!= corr){
                    open.add(k);
                }
            }
            for(String w:questions[i].wrong){
                choices[open.remove((int)(Math.random()*open.size()))] = w;
            }
            StringBuilder text = new StringBuilder("\t\\noindent\n\\begin{minipage}{\\textwidth}\n" +
                    "\t\\setlength{\\parindent}{1.5em}\n" +
                    "\\noindent "+ dp.choices.generateName(i)+". " + questions[i].text + "\n" +
                    "\\newline");
            for(int c = 0; c<qp.choices; c++){
                text.append("\\indent ").append(choiceSet[c]).append(") ").append(choices[c]).append("\n\\newline");
            }
            text.append("\\newline").append("\\end{minipage}");
            pw.println(text);
        }
        pw.println("\\end{multicols*}");
        pw.println("\\end{document}");
        pw.flush();
        pw.close();
        PrintWriter pw2 = new PrintWriter(new FileWriter(new File(dir.getPath()+"/answers.txt")));
        for(int i = 0; i<correct.length; i++){
            pw2.println(dp.choices.generateName(i)+". "+dp.choices.generateSet(i)[correct[i]]+" ("+correct[i]+")");
        }
        pw2.flush();
        pw2.close();
        ProcessBuilder pb = new ProcessBuilder("/bin/bash","-c","pdflatex test_tex.txt");
        pb.directory(dir);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process p = pb.start();
        p.waitFor();
    }

    static <T> void shuffleArray (T[] ar)
    {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            T a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }
}
