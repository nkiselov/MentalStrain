package Sheet;

public class MalformedScanException extends Exception {
    int marksFound;
    int marksExpected;

    public MalformedScanException(int marksFound, int marksExpected) {
        this.marksFound = marksFound;
        this.marksExpected = marksExpected;
    }
}
