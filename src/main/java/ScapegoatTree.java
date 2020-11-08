import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class ScapegoatTree<T extends Comparable<T>> {
    Node root;
    int height = 0;
    List<Node<T>> list;
    Stack<Node<T>> stack;
    int maxSize;
    private int size = 0;
    private double a;
    double ha = Math.log(this.size) / Math.log(1.0 / a);

    ScapegoatTree(double alpha) {
        if (alpha < 0.5 || alpha >= 1) throw new IllegalArgumentException();
        a = alpha;
        root = null;
    }

    public void add(T t) {
        int height = addE(t).height;
        if (height > ha) {
            Node scapegoat = findScapegoat(find(t));
            rebuild(scapegoat);
        }
    }

    public void rebuild(Node scape) {
        list = new ArrayList<>();
        inOrder(scape);
        if (scape.parent == null) {
            root = build(0, list.size());
            root.parent=null;
        }
        if (scape.parent.right != null && scape.parent.right.value.equals(scape.value))
            scape.parent.right = build(0, list.size());
        else scape.parent.left = build(0, list.size());
    }

    public Node<T> build(int i, int lsize) {
        if (lsize == 0) return null;
        int m = lsize / 2;
        list.get(i + m).left = build(i, m);
        if (list.get(i + m).left != null) list.get(i + m).left.parent = list.get(i + m);
        list.get(i + m).right = build(i + m + 1, size - m - 1);
        if (list.get(i + m).right != null) list.get(i + m).right.parent = list.get(i + m);
        return list.get(i + m);
    }

    public int getSize(){

    }

    public Node addE(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return null;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
            closest.left.parent = closest;
            closest.left.sibling = closest.right;
            size++;
            return closest.left;
        } else {
            assert closest.right == null;
            closest.right = newNode;
            closest.right.parent = closest;
            closest.right.sibling = closest.left;
            size++;
            return closest.right;
        }
        return null;
    }

    public void inOrder(Node<T> start) {
        if (start == null) return;
        inOrder(start.left);
        list.add(start);
        inOrder(start.right);
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
        boolean del = removeE(o);
        if (del) {
            //     if (size > ha * maxSize) rebuild();
        }
    }

    public boolean removeE(Object o) {
        Node rem = find((T) o);
        boolean r = false;
        int comparison = rem == null ? -1 : ((T) o).compareTo((T) rem.value);
        if (comparison != 0) {
            return false;
        }
        if (rem.value.equals(root.value)) {
            r = true;
        }
        if (rem.right == null) {
            if (r) root = rem.left;
            if (rem.parent.right != null && rem.parent.right.value.equals(rem.value)) rem.parent.right = rem.left;
            else rem.parent.left = rem.left;
            rem.left.parent = rem.parent;
            size--;
        } else if (rem.left == null) {
            if (r) root = rem.right;
            if (rem.parent.right != null && rem.parent.right.value.equals(rem.value)) rem.parent.right = rem.right;
            else rem.parent.left = rem.right;
            rem.right.parent = rem.parent;
            size--;
        } else {
            if (rem.right.left == null) {
                rem.value = rem.right.value;
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
        return true;
    }


    public Node findScapegoat(Node<T> n) {
        int size = 1;
        int height = 0;
        while (n.parent != null) {
            height++;
            double totalSize = 1 + size + n.sibling.size;
            if (height > (Math.log(totalSize) / Math.log(1.0 / a))) return n.parent;
            n = n.parent;
            size = (int) totalSize;
        }
        return null;
    }


    private static class Node<T> {
        T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;
        int height;
        int size = 1;
        Node sibling;

        Node(T value) {
            this.value = value;
        }
    }
}
