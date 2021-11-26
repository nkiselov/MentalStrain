package MultipleChoice;

import Sheet.ChoiceSetNameGenerator;

import java.awt.*;

public class DesignPreset {
    int questionColumns;
    double margin;
    ChoiceSetNameGenerator choices;
    Color sheetColor;

    public DesignPreset(int questionColumns, double margin, ChoiceSetNameGenerator choices, Color sheetColor) {
        this.questionColumns = questionColumns;
        this.margin = margin;
        this.choices = choices;
        this.sheetColor = sheetColor;
    }
}
