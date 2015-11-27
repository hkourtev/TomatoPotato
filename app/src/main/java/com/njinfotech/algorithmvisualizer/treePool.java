package com.njinfotech.algorithmvisualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by aditya on 11/27/2015.
 */
public class treePool implements Iterable{
    private List trees = new ArrayList<Tree>();

    public Iterator iterator(){
        return trees.iterator();
    }

    public void sortPool(){
        Collections.sort(trees);
    }

    public int getMaxWidth(){
        Iterator I = trees.iterator();
        int i = 0;
        while(I.hasNext()){
            i += ((Tree)I.next()).getMaxWidth();
        }
        return i;
    }

    public int getMaxHeight(){
        Iterator I = trees.iterator();
        int i = 0;
        while(I.hasNext()){
            int j = ((Tree)I.next()).getHeight();
            if(j > i){
                j = i;
            }
        }
        return i;
    }

    class Tree implements Comparable{
        private Node root;
        class Node implements Iterable{
            private String label;
            private Node parent;
            private int edgeWeight;
            private List children = new  ArrayList<Node>();

            public void addChild(Node child){
                children.add(child);
            }

            public Node(String label, Node parent, int edgeWeight){
                this.label = label;
                this.parent = parent;
                this.edgeWeight = edgeWeight;
            }

            public Iterator iterator(){
                return children.iterator();
            }

            public void connect(Node child, int edgeWeight){
                this.children.add(child);
                child.parent = this;
                child.edgeWeight = edgeWeight;
            }

            public String getLabel(){
                return label;
            }

        }

        public int getHeight(){
            return getHeightHelper(root);
        }

        public int getHeightHelper(Node root){
            Iterator I = root.iterator();
            int i = 0;
            while(I.hasNext()){
                Node child = (Node)I.next();
                int heightOfChild = getHeightHelper(child);
                if(heightOfChild > i){
                    i = heightOfChild;
                }
            }
            return i + 1;
        }

        public String levelOrderTraversal(){
            Queue Q = new LinkedList();
            StringBuilder result = new StringBuilder();
            Q.offer(root);
            Q.offer(new Node("*", null, 0));
            result.append(root.getLabel());
            result.append("*");
            while(!Q.isEmpty()){
                Node top = (Node)Q.poll();
                result.append(top.getLabel());
                if(top.getLabel() != "*") {
                    Iterator I = top.iterator();
                    while (I.hasNext()) {
                        Node temp = (Node)I.next();
                        Q.offer(temp);
                        result.append(temp.getLabel());
                    }
                    result.append("!");
                }
                else{
                    if(result.charAt(result.length() - 1) == '*'){
                        break;
                    }
                    else {
                        Q.offer(new Node("*", null, 0));
                        result.append("*");
                    }
                }
            }
            return result.toString();
        }

        public int getMaxWidth(){
            String lot = levelOrderTraversal();
            String lot2 = lot.replace("!", "");
            String levels[] = lot2.split("\\*");
            int max = 0;
            for(String level : levels){
                if(level.length() > max){
                    max = level.length();
                }
            }
            return max;
        }

        public int countNodes(){
            return countNodesHelper(root);
        }
        public int countNodesHelper(Node root){
            Iterator I = root.iterator();
            int i = 1;
            while(I.hasNext()){
                i += countNodesHelper((Node) I.next());
            }
            return i;
        }

        public int compareTo(Object O){
            int left = ((Tree)O).countNodes(), right = this.countNodes();
            return left > right ? 1 : left == right ?  0 : -1;
        }
    }

}
