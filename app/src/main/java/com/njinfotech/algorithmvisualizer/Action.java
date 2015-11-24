package com.njinfotech.algorithmvisualizer;

/**
 * Created by hkourtev on 11/12/15.
 */
public class Action {
    String command;
    String parameters[];
    int numExpectParam;
    int numRcvdParam;

    public Action () {

    }

    public Action (String cmd, int numPar) {
        command = cmd;
        numExpectParam = numPar;
        parameters = new String[numPar];
        numRcvdParam = 0;
    }

    public void addParam(String par) {
        parameters[numRcvdParam] = par;
        numRcvdParam++;
    }
}
