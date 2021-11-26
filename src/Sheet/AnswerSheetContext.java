package Sheet;

import java.io.*;
import java.util.Scanner;

public class AnswerSheetContext {
    int questions;
    int choices;
    int n;
    int m;
    double whiteX;
    double whiteY;

    public AnswerSheetContext(int questions, int choices, int n, int m, double whiteX, double whiteY) {
        this.questions = questions;
        this.choices = choices;
        this.n = n;
        this.m = m;
        this.whiteX = whiteX;
        this.whiteY = whiteY;
    }

    public static void writeToFile(AnswerSheetContext ctx, File f) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        pw.println(ctx.questions);
        pw.println(ctx.choices);
        pw.println(ctx.n);
        pw.println(ctx.m);
        pw.println(ctx.whiteX);
        pw.println(ctx.whiteY);
        pw.flush();
        pw.close();
    }

    public static AnswerSheetContext readFromFile(File f) throws FileNotFoundException {
        Scanner scn = new Scanner(new FileInputStream(f));
        int questions = Integer.parseInt(scn.nextLine());
        int choices = Integer.parseInt(scn.nextLine());
        int n = Integer.parseInt(scn.nextLine());
        int m = Integer.parseInt(scn.nextLine());
        double whiteX = Double.parseDouble(scn.nextLine());
        double whiteY = Double.parseDouble(scn.nextLine());
        scn.close();
        return new AnswerSheetContext(questions, choices, n, m, whiteX, whiteY);
    }

    public int getQuestions() {
        return questions;
    }
}
