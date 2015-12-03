package com.njinfotech.algorithmvisualizer;

/**
 * Created by hkourtev on 11/30/15.
 */
public class Mistake {
    int id;
    int sessionId;
    String stepChosen;
    String stepCorrect;

    Mistake() {

    }

    Mistake(int i, int sId, String sChosen, String sCorrect) {
        id = i;
        sessionId = sId;
        stepChosen = sChosen;
        stepCorrect = sCorrect;
    }
}
