import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ScapegoatTree<T extends Comparable<T>> {
    Node root;
    List<Node<T>> list;
    private int size = 0;
    private double a = 0.5;


    ScapegoatTree(double alpha) {
        if (alpha < 0.5 || alpha >= 1) throw new IllegalArgumentException();
        a = alpha;
        root = null;
    }

    ScapegoatTree() {
        root = null;
    }

    public void rebuild(Node scape) {
        list = new ArrayList<>();
        inOrder(scape);
        if (scape.parent == null) {
            root = build(0, list.size());
            root.parent = null;
        } else if (scape.parent.right != null && scape.parent.right.value.equals(scape.value)) scape.parent.right = build(0, list.size());
        else scape.parent.left = build(0, list.size());
    }

    public Node<T> build(int i, int lsize) {
        if (lsize == 0) return null;
        int m = lsize / 2;
        list.get(i + m).left = build(i, m);
        if (list.get(i + m).left != null) list.get(i + m).left.parent = list.get(i + m);
        list.get(i + m).right = build(i + m + 1, lsize - m - 1);
        if (list.get(i + m).right != null) list.get(i + m).right.parent = list.get(i + m);
        return list.get(i + m);
    }

    public int getSize(Node<T> node) {
        if (node == null) return 0;
        else {
            return (getSize(node.left) + 1 + getSize(node.right));
        }
    }

    public void add(T t) {
        Node cur = addE(t);
        if (cur == null) return;
        Node goat = findScapegoat(cur);
        if (goat != null) {
            rebuild(goat);
        }
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
            size++;
            return root;
        } else if (comparison < 0) {
            assert closest.left == null;
            newNode.parent = closest;
            closest.left = newNode;
            size++;
            return closest.left;
        } else {
            assert closest.right == null;
            newNode.parent = closest;
            closest.right = newNode;
            size++;
            return closest.right;
        }
    }

    public void inOrder(Node<T> start) {
        if (start == null) return;
        inOrder(start.left);
        list.add(new Node<T>(start.value));
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


    public boolean remove(Object o) {
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
        while (n.parent != null) {
            if (getSize(n.left) > getSize(n) * a || getSize(n.right) > getSize(n) * a) return n;
            n = n.parent;
        }
        return null;
    }

    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    public int size() {
        return size;
    }


    private static class Node<T> {
        T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }
    }
}
