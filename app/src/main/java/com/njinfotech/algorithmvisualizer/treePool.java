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

    public void makeTree(String label){
        Tree newTree = new Tree(label);
        trees.add(newTree);
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
                reAssignParents(child, this);
            }

            public void reAssignParents(Node current, Node newParent){
                Node parent = current.parent;
                current.parent = newParent;
                if(parent != null) {
                    current.addChild(parent);
                    reAssignParents(parent, current);
                }
            }

            public String getLabel(){
                return label;
            }

            public Node findNode(String label){
                if(this.label == label){
                    return this;
                }
                else{
                    Iterator I = children.iterator();
                    while(I.hasNext()){
                        Node child = (Node)I.next();
                        Node result = child.findNode(label);
                        if(result != null)return result;
                    }
                }
                return null;
            }

            public Node findRoot(){
                if(this.parent == null){
                    return this;
                }
                else{
                    return this.parent.findRoot();
                }
            }
        }

        public Tree(String label){
            root = new Node(label, null, 0);
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
                    if(!Q.isEmpty()){
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
            String lot = levelOrderTraversal();
            lot.replace("!", "");
            lot.replace("*", "");
            return lot.length();
        }
        public int compareTo(Object O){
            int left = ((Tree)O).countNodes(), right = this.countNodes();
            return left > right ? 1 : left == right ?  0 : -1;
        }

    }

    public void mergeTrees(String left, String right){
        Iterator I = trees.iterator();
        int count[] = new int[2];
        treePool.Tree.Node matches[] = new treePool.Tree.Node[2];
        int index = 0;

        while(I.hasNext()){
            Tree current = (Tree)I.next();
            if(current.root.findNode(left)!= null){
                matches[index] = current.root.findNode(left);
                count[index] = current.countNodes();
                index++;
            }
            else if(current.root.findNode(right) != null){
                matches[index] = current.root.findNode(right);
                count[index] = current.countNodes();
                index++;
            }
        }

        if(count[0] > count[1]){
            matches[0].connect(matches[1], 0);
            trees.remove(findTree(matches[1]));

        }
        else{
            matches[1].connect(matches[0], 0);
            trees.remove(findTree(matches[0]));
        }
    }

    private Tree findTree(Tree.Node no){
        Tree.Node root = no.findRoot();
        Iterator I = trees.iterator();
        while(I.hasNext()){
            Tree t = (Tree)I.next();
            if(t.root == root){
                return t;
            }
        }
        return null;
    }
}
