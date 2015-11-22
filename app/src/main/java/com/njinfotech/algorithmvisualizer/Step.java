package com.njinfotech.algorithmvisualizer;

/**
 * Created by hkourtev on 11/22/15.
 */
public class Step {

    public String command;
    public String[] arguments;
    public String description;
    public Boolean pause;
    public Boolean paramOrder;
    public Boolean executed;

    public Step() {
    }

    public Step(String stepCommand, String[] args, String stepDescription, Boolean haltAfterStep,
                Boolean paramOrderMatters) {
        command = stepCommand;
        arguments = args;
        description = stepDescription;
        pause = haltAfterStep;
        paramOrder = paramOrderMatters;
        executed = false;
    }


}
