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
        Collections.sort(sets, Collections.reverseOrder());
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
        set matches[] = new set[2];
        int count[] = new int[2];
        int index = 0;
        Iterator I = sets.iterator();
        while(I.hasNext()){
            set temp = (set)I.next();
            if(temp.getLabel() == labelLeft || temp.hasChild(labelLeft) || temp.getLabel() == labelRight || temp.getLabel() == labelRight){
                matches[index] = temp;
                count[index] = temp.getWeight();
                index++;
            }
        }
        if(count[0] >= count[1]){
            matches[0].absorbSet(matches[1]);
            sets.remove(matches[1]);
        }
        else{
            matches[1].absorbSet(matches[0]);
            sets.remove(matches[0]);
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
