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

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // in order to be able to copy object by value if needed
        Action cloned = (Action)super.clone();
        return cloned;
    }

    public void addParam(String par) {
        parameters[numRcvdParam] = par;
        numRcvdParam++;
    }
}
