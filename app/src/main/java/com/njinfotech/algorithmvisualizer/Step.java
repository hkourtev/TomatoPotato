package com.njinfotech.algorithmvisualizer;

/**
 * Created by hkourtev on 11/22/15.
 */
public class Step {

    public String command;
    public String[] arguments;
    public String description;
    public Class[] parameters;
    public Boolean pause;
    public Boolean paramOrder;
    public Boolean executed;

    public Step() {
    }

    public Step(String stepCommand, Class[] params, String[] args, String stepDescription, Boolean haltAfterStep,
                Boolean paramOrderMatters) {
        command = stepCommand;
        parameters = params;
        arguments = args;
        description = stepDescription;
        pause = haltAfterStep;
        paramOrder = paramOrderMatters;
        executed = false;
    }

    public Boolean compare(String cmd, String[] args) {
        if (generateCommand().equals(generateCommand(cmd, args)))
            return true;
        else
            return false;
    }

    public String generateCommand() {
        return generateCommand(command, arguments);
    }

    public String generateCommand(String cmd, String[] args) {
        cmd = cmd + "(";
        for (int h=0; h<args.length; h++) {
            if (h==args.length-1) {
                cmd = cmd + args[h];
            } else {
                cmd = cmd + args[h] + ",";
            }
        }
        cmd = cmd + ")";

        return cmd;
    }
}
