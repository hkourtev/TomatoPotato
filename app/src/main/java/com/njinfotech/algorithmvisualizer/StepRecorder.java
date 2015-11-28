package com.njinfotech.algorithmvisualizer;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by aditya on 11/28/2015.
 */
public class StepRecorder {
    private int currentStep;
    private SetPool sp;
    private treePool tp;
    private List steps;
    private Graph graph;

    class step{
        private String operation, description;
        private String[] arguments;

        public step(String operation, String description, String[] arguments){
            this.operation = operation;
            this.description = description;
            this.arguments = arguments;
        }

        public String getOperation(){
            return operation;
        }

        public String[] getArguments(){
            return arguments;
        }

        public String getDescription(){
            return description;
        }

    }

    public StepRecorder(SetPool sp, treePool tp, Graph G){
        this.sp = sp;
        this.tp = tp;
        this.graph = G;
        steps = new ArrayList<Step>();
        currentStep = 0;
    }

    public void addStep(String operation, String description, String[] arguments){
        steps.add(new step(operation, description, arguments));
    }

    public Boolean hasNext(){
        return currentStep + 1 != steps.size();
    }

    public String nextStep(){
        step next;

        if(hasNext()){

            next = (step)steps.get(currentStep);
            currentStep++;
            executeStep(next);
            return next.getDescription();
        }
        else{
            return "";
        }
    }

    private void executeStep(step next){
        switch (next.getOperation()) {
            case "_makeSet":
                sp.makeSet(next.getArguments()[0]);
                tp.makeTree(next.getArguments()[0]);
            break;

            case "_union":
                sp.unionSets(next.getArguments()[0], next.getArguments()[1]);
                tp.mergeTrees(next.getArguments()[0], next.getArguments()[1]);
            break;
        }
    }
}
