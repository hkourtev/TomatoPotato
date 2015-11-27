package com.njinfotech.algorithmvisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * Created by aditya on 11/26/2015.
 */

public class SetPool implements Iterable{
    private List sets = new ArrayList<set>();

    public void makeSet(String label){
        sets.add(new set(label));
    }

    public void sortPool(){
        Collections.sort(sets);
    }

    public Iterator iterator(){
        return sets.iterator();
    }

    public int getTotalChildrenCount(){
        int count = 0;
        Iterator I = sets.iterator();
        while(I.hasNext()){
            count += ((set)I.next()).getWeight();
        }
        return count;
    }

    public int getTotalSetCount(){
        return sets.size();
    }

    public void unionSets(String labelLeft, String labelRight){
        set left = null, right = null;
        Iterator I = sets.iterator();
        while(I.hasNext()){
            set temp = (set)I.next();
            if(temp.getLabel() == labelLeft || temp.hasChild(labelLeft)){
                left = temp;
            }
            else if(temp.getLabel() == labelRight || temp.getLabel() == labelRight){
                if(left != null){
                    right = temp;
                }
                else{
                    left = temp;
                }
            }
        }
        if(left.getWeight() >= right.getWeight()){
            left.absorbSet(right);
            sets.remove(right);
        }
        else{
            right.absorbSet(left);
            sets.remove(left);
        }
    }

    class set implements Comparable, Iterable{
        String label;
        List children;

        public Boolean hasChild(String child){
            return children.contains(child);
        }

        public set(String label){
            this.label = label;
            children = new ArrayList<String>();
        }

        public String getLabel(){
            return label;
        }

        public void absorbSet(set S){
            children.add(S.label);
            Iterator I = S.children.iterator();
            while(I.hasNext()){
                children.add(I.next());
            }
        }

        public int getWeight(){
            return children.size();
        }

        public int compareTo(Object o1){
            int left = this.getWeight(), right = ((set)o1).getWeight();
            return left > right ? 1 : left == right ?  0 : -1;
        }

        public Iterator iterator(){
            return children.iterator();
        }
    }
}
