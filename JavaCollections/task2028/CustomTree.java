package com.javarush.task.task20.task2028;

import java.io.Serializable;
import java.util.*;

/* 
Построй дерево(1)

Добавлять в дерево элементы мы можем, теперь займись удалением:
Необходимо реализовать метод remove(Object o), который будет удалять элемент дерева имя которого было полученного в качестве параметра.
Если переданный объект не является строкой, метод должен бросить UnsupportedOperationException.
Если в дереве присутствует несколько элементов с переданным именем - можешь удалить только первый найденный.
Не забывай сверять поведение своего дерева с картинкой:

[Картинка]

Что будет если удалить из дерева элементы "3", "4", "5" и "6", а затем попытаемся добавить новый елемент?
В таком случае элементы "1" и "2" должны восстановить возможность иметь потомков (возможно придется внести изменения в метод add()).

Требования:
1. После удаления последнего добавленного элемента из дерева с помощью метода remove, метод size должен возвращать N-1.
2. После удаления второго элемента добавленного в дерево, метод size должен возвращать N/2 + 1 (для случаев где N > 2 и является степенью двойки), N - размер дерева до удаления.
3. Если переданный объект не является строкой, метод remove() должен бросить UnsupportedOperationException.
4. Если ни один элемент не способен иметь потомков, необходимо восстановить такую возможность.
*/
public class CustomTree extends AbstractList<String> implements Cloneable, Serializable {
    Entry<String> root;
    private int size = 0;

    public CustomTree() {
        this.root = new Entry("0");
    }

    @Override
    public String get(int index) {
        Entry<String> current = root;                // Starting from root node.

        while (current.index != index) {             // While we find it.

            if (index < current.index) {             // Left side?
                current = current.leftChild;
            } else {
                current = current.rightChild;        // Or right?
            }
            if (current == null) {                   // Haven`t child
                return null;                         // Don`t found node.
            }
        }
        return current.elementName;
    }

    public String getParent(String s) {
        Entry<String> current = root;
        String parent = null;
        Stack<Entry<String>> stack = new Stack<>();
        stack.add(current);

        if (root.elementName.equals(s)) {
            return null;
        }
        do {
            if (current != null & current.elementName.equals(s)) {
                parent = current.parent.elementName;
            }
            if (current.leftChild != null)
                stack.add(current.leftChild);
            if (current.rightChild != null)
                stack.add(current.rightChild);
            if (!stack.empty())
                current = stack.pop();
        } while (!stack.empty());

        return parent;
    }

    @Override
    public String set(int index, String element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof String) {
            String element = (String)o;
            Stack<Entry<String>> stack = new Stack<>();

            Entry<String> current = root;                // Starting from root node.
            Entry<String> parent = root;

            stack.add(current);
            // Looking up our node in the tree
            do {
                if (current != null) {
                    if (current.elementName.equals(element)) {
                        parent = current.parent;
                        break;
                    }
                    if (current.leftChild != null)
                        stack.add(current.leftChild);
                    if (current.rightChild != null)
                        stack.add(current.rightChild);
                }
                if (!stack.empty()) {
                    current = stack.pop();
                }
            } while (!stack.empty());

            // Looking up all the nodes from "current" node. It`s doing for to clarify the size.
            stack.clear();
            stack.add(current);
            int stackSize = 0;
            do {
                if (current != null) {
                    if (current.leftChild != null)
                        stack.add(current.leftChild);
                    if (current.rightChild != null)
                        stack.add(current.rightChild);
                }
                if (!stack.empty()) {
                    current = stack.pop();
                    stackSize++;
                }
            } while (!stack.empty());

            // Delete parent link on the current and current link on the parent.
            // Destroying the connection between parent and child.
            // After GC doing his work.
            if (parent.leftChild == null || parent.leftChild.equals(current)) {
                parent.leftChild = null;
            } else {
                parent.rightChild = null;
            }

            if (!parent.availableToAddLeftChildren) {
                parent.availableToAddLeftChildren = true;
            } else {
                parent.availableToAddRightChildren = true;
            }
            size = size - stackSize;
            current.parent = null;
        } else {
            throw new UnsupportedOperationException();
        }
        return true;

/////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////

//        //This block is a classical realization of remove method from book Lafore Robert Data Structures and Algorithms in Java
//        //Maybe code is bad because it`s if...else blocks and who-is-who for nodes is confuses ^_^
//        if (o instanceof String) {
//            String element = (String)o;
//            Stack<Entry<String>> stack = new Stack<>();
//
//            Entry<String> current = root;                // Starting from root node.
//            Entry<String> parent = root;
//
//            stack.add(current);
//
//            //Looking up node
//            do {
//                if (current != null) {
//                    if (current.elementName.equals(element)) {
//                        parent = current.parent;
//                        break;
//                    }
//                    if (current.leftChild != null)
//                        stack.add(current.leftChild);
//                    if (current.rightChild != null)
//                        stack.add(current.rightChild);
//                }
//                if (!stack.empty()) {
//                    current = stack.pop();
//                }
//            } while (!stack.empty());
//
//            //node is leaf
//            if (current.rightChild == null && current.leftChild == null) {
//                if (current.equals(root)) {
//                    root = null;
//                } else {
//                    current.parent = null;
//
//                    if (parent.leftChild != null && parent.leftChild.equals(current)) //isLeftChild
//                        parent.leftChild = null;
//                    else
//                        parent.rightChild = null;
//                }
//            } else  //Deleting node have one child node
//                if (current.rightChild == null) {                       // Left child
//                    if (current == root) {
//                        root = current.leftChild;
//                        root.parent = null;
//                    } else if (parent.leftChild.equals(current)) {
//                        parent.leftChild = current.leftChild;
//                        current.leftChild.parent = parent;
//                    } else {
//                        parent.rightChild = current.rightChild;
//                    }
//                } else if (current.leftChild == null) {                 // Right child
//                    if (current == root) {
//                        root = current.rightChild;
//                        root.parent = null;
//                    } else if (parent.rightChild.equals(current)) {
//                        parent.rightChild = current.rightChild;
//                        current.rightChild.parent = parent;
//                    } else {
//                        parent.leftChild = current.leftChild;
//                    }
//                } else { //Deleting node have two child nodes
//                    Entry<String> successor = getSuccessor(current);
//
//                    if (successor.equals(current.rightChild)) {
//                        if (current == root) {
//                            root = successor;
//                            successor.parent = null;
//                        } else if (parent.leftChild != null & parent.leftChild.equals(current)) {
//                            parent.leftChild = successor;
//                            successor.parent = parent;
//                            successor.leftChild = current.leftChild;
//                            current.leftChild.parent = successor;
//                            //Clear current connections
//                            current.leftChild = null;
//                            current.rightChild = null;
//                            current.parent = null;
//
//                        } else {
//                            parent.rightChild = successor;
//                            successor.parent = parent;
//                            successor.leftChild = current.leftChild;
//                            current.leftChild.parent = successor;
//
//                            current.leftChild = null;
//                            current.rightChild = null;
//                            current.parent = null;
//                        }
//                    } else {// Successor right child have left child
//                        //maybe this block need move to getSuccessor() method
//                        successor.parent.leftChild = successor.rightChild;
//                        successor.rightChild.parent = successor.parent;
//
//                        successor.rightChild = current.rightChild;
//                        successor.rightChild.parent = successor;
//
//                        parent.rightChild = successor;
//                        successor.parent = parent;
//
//                        successor.leftChild = current.leftChild;
//                        successor.leftChild.parent = successor;
//
//                        current.leftChild = null;
//                        current.rightChild = null;
//                        current.parent = null;
//                    }// For tests
//                    try { System.out.println("successor " + successor.elementName);
//                    } catch (NullPointerException e) { System.out.println("successor null");
//                    }
//                    try { System.out.println("successor.leftChild " + successor.leftChild.elementName);
//                    } catch (NullPointerException e) {System.out.println("successor.leftChild null");
//                    }
//                    try { System.out.println("successor.rightChild " + successor.rightChild.elementName);
//                    } catch (NullPointerException e) {System.out.println("successor.rightChild null");
//                    }
//                    try { System.out.println("successor.parent " + successor.parent.elementName);
//                    } catch (NullPointerException e) {System.out.println("successor.parent null");
//                    }
//                }
//        }
//        return true;
    }

    private Entry<String> getSuccessor(Entry<String> delNode){
        Entry<String> successorParent = delNode;
        Entry<String> successor = delNode;
        Entry<String> current = delNode.rightChild;     // Go to the right child

        while(current != null)     {
            successorParent = successor;
            successor = current;
            current = current.leftChild;                // go to the lest child
        }
        if(successor != delNode.rightChild) {           // If successor isn`t right child make connections between nodes
            successorParent.leftChild = successor.rightChild;
            successor.rightChild = delNode.rightChild;
        }
        return successor;
    }

    @Override
    public boolean addAll(int index, Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(String element) {
        Entry<String> top = root;
        Entry<String> current = new Entry<>(element);
        Queue<Entry<String>> queue = new LinkedList<>();

        do {
            if (top.leftChild != null) {            // If in the left side is node - adding it in the queue.
                queue.add(top.leftChild);
            } else {
                top.leftChild = current;            // Create new node in the left subtree.
                top.cantMakeChildren();             // Parent cant have child on this direction.
                current.parent = top;               // Set parent.
                size++;
                return true;
            }
            if (top.rightChild != null) {           // Similar as for left subtree.
                queue.add(top.rightChild);
            } else {
                top.rightChild = current;
                top.cantMakeChildren();
                current.parent = top;
                size++;
                return true;
            }
            if (!queue.isEmpty()) {
                top = queue.poll();                 // Take node from the queue head and delete his from queue.
            }
        } while (!queue.isEmpty());
        queue.clear();
        return false;
    }

    @Override
    public int size() {
        if  (root == null){
            return 0;
        }
        return size;
    }

    static class Entry<T> implements Serializable {
        String elementName;
        boolean availableToAddLeftChildren, availableToAddRightChildren;
        Entry<T> parent, leftChild, rightChild;
        int index;

        public Entry(String elementName) {
            this.elementName = elementName;
            this.availableToAddLeftChildren = true;
            this.availableToAddRightChildren = true;
        }

        private void cantMakeChildren() {
            if (leftChild != null) availableToAddLeftChildren = false;
            if (rightChild != null) availableToAddRightChildren = false;
        }

        // Don`t know why we create this method ^_^
        public boolean isAvailableToAddChildren() {
            return availableToAddLeftChildren || availableToAddRightChildren;
        }
    }
}
