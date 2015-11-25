package com.njinfotech.algorithmvisualizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hkourtev on 11/22/15.
 */
public class Step implements Cloneable {

    public String command;
    public String[] arguments;
    public String description;
    public Class[] parameters;
    public Boolean pause;
    public Boolean orderMatters;
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
        orderMatters = paramOrderMatters;
        executed = false;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        // in order to be able to copy object by value if needed
        Step cloned = (Step)super.clone();
        return cloned;
    }

    public Boolean compare(String cmd, String[] args, Boolean orderMatters) {
        if (command.equals(cmd) && compareArrays(arguments, args, orderMatters)) {
            return true;
        } else
            return false;
    }

    Boolean compareArrays(String[] arr1, String[] arr2, Boolean orderMatters) {
        if (arr1.length == arr2.length) {
            if (orderMatters) {
                // order matters - compare each entry 1 by one
                for (int t=0; t<arr1.length; t++) {
                    if (!arr1[t].equals(arr2[t])) {
                        return false;
                    }
                }
            } else {
                // order doesn't matter
                List<String> list1 = new ArrayList<String>();
                List<String> list2 = new ArrayList<String>();

                // store array values in list so we can search easily
                for (int t=0; t<arr1.length; t++) {
                    list1.add(arr1[t]);
                    list2.add(arr2[t]);
                }

                // loop over all the values and see if there is a value we can't find
                int valIndex;
                for (int t=list1.size()-1; t>=0; t--) {
                    valIndex = list2.indexOf(list1.get(t));
                    if (valIndex != -1) {
                        // found -- remove values from lists
                        list1.remove(t);
                        list2.remove(valIndex);
                    } else {
                        // not found - return false
                        return false;
                    }
                }
            }
        } else
            return false;

        return true;
    }
}
