import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class ScapegoatTree<T extends Comparable<T>> {
    Node root;
    int height = 0;
    List<T> list = new ArrayList<T>();
    Stack<Node<T>> stack;
    private int size = 0;
    private double a;

    ScapegoatTree(double alpha) {
        if (alpha < 0.5 || alpha >= 1) throw new IllegalArgumentException();
        a = alpha;
        root = null;
    }

    private void fillStack(Node<T> start) {
        while (start != null) {
            stack.push(start);
            start = start.left;
        }
    }

    public boolean getList(Node root, Node head) {
        if (root != null) fillStack(root);
        while (true) {
            Node cur = stack.pop();
            list.add((T) cur.value);
            if (cur.right != null) fillStack(cur.right);
        }
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }


    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public void remove(Object o) {
        Node rem = find((T) o);
        if (rem.right == null) {
            if (rem.parent.right != null && rem.parent.right.value.equals(rem.value)) rem.parent.right = rem.left;
            else rem.parent.left = rem.left;
            rem.left.parent = rem.parent;
            size--;
        } else if (rem.left == null) {
            if (rem.parent.right != null && rem.parent.right.value.equals(rem.value)) rem.parent.right = rem.right;
            else rem.parent.left = rem.right;
            rem.right.parent = rem.parent;
            size--;
        } else {
            if (rem.right.left == null) {
                rem.value=rem.right.value;
                rem.right = rem.right.right;
                if (rem.parent.right != null && rem.parent.right.value.equals(rem.value)) rem.parent.right = rem.right;
                else rem.parent.left = rem.right;
                rem.right.parent = rem.parent;
            } else {
                Node<T> cur = rem.right;
                while (true) {
                    if (cur.left.left == null) break;
                    else cur = cur.left;
                }
                rem.value = cur.left.value;
                cur.left = null;
            }
        }
    }


    private static class Node<T> {
        T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;
        int size = 1;

        Node(T value) {
            this.value = value;
        }
    }
}
