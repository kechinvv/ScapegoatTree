import java.util.*;

public class ScapegoatTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {
    Node<T> root;
    List<Node<T>> list;
    private int size = 0;
    private double a = 0.5;

    ScapegoatTree() {
        root = null;
    }


    ScapegoatTree(double alpha) {
        if (alpha < 0.5 || alpha >= 1) throw new IllegalArgumentException();
        a = alpha;
        root = null;
    }

    public void rebuild(Node<T> scape) throws CloneNotSupportedException {
        list = new ArrayList<>();
        inOrder(scape);
        if (scape.parent == null) {
            root = build(0, list.size());
            root.parent = null;
        } else {
            if (scape.parent.right != null && scape.parent.right.value.equals(scape.value)) {
                scape.parent.right = build(0, list.size());
                scape.parent.right.parent = scape.parent;
            } else {
                scape.parent.left = build(0, list.size());
                scape.parent.left.parent = scape.parent;
            }

        }
    }

    public Node<T> build(int i, int lsize) {
        if (lsize == 0) return null;
        int m = lsize / 2;
        list.get(i + m).left = build(i, m);
        if (list.get(i + m).left != null) {
            list.get(i + m).left.parent = list.get(i + m);
        }
        list.get(i + m).right = build(i + m + 1, lsize - m - 1);
        if (list.get(i + m).right != null) {
            list.get(i + m).right.parent = list.get(i + m);
        }

        return list.get(i + m);
    }

    public Node<T> findScapegoat(Node<T> n) {
        Node s = null;
        if (n == null) return null;
        while (n != null) {
            if (getSize(n.left) > getSize(n) * a || getSize(n.right) > getSize(n) * a) s = n;
            n = n.parent;

        }
        return s;
    }

    public int getSize(Node<T> node) {
        if (node == null) return 0;
        return 1 + getSize(node.left) + getSize(node.right);
    }

    @Override
    public boolean add(T t) {
        Node<T> cur = addE((T) t);
        if (cur == null) return false;
        Node<T> goat = findScapegoat(cur);

        if (goat != null) {
            try {
                rebuild(goat);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public Node<T> addE(T t) {
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

    Node<T> find(T value) {
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

    @Override
    public Comparator comparator() {
        return null;
    }

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (fromElement == null || toElement == null) throw new NullPointerException();
        TreeSet<T> set = new TreeSet<T>() {
            ScapegoatTree<T> b = ScapegoatTree.this;

            @Override
            public boolean add(T t) {
                if (fromElement.compareTo(t) > 0 || toElement.compareTo(t) <= 0) throw new IllegalArgumentException();
                b.add(t);
                return super.add(t);
            }

            @Override
            public boolean remove(Object o) {
                if (fromElement.compareTo((T) o) > 0 || toElement.compareTo((T) o) <= 0)
                    throw new IllegalArgumentException();
                b.remove(o);
                return super.remove(o);
            }

            @Override
            public Iterator<T> iterator() {
                b.adder(root, this, fromElement, toElement);
                return super.iterator();
            }

            @Override
            public boolean contains(Object o) {
                b.adder(root, this, fromElement, toElement);
                return super.contains(o);
            }

            @Override
            public int size() {
                b.adder(root, this, fromElement, toElement);
                return super.size();
            }
        };
        if (fromElement.compareTo(toElement) == 0) return set;
        this.adder(root, set, fromElement, toElement);
        return set;
    }

    @Override
    public SortedSet<T> headSet(T toElement) {
        return this.subSet(first(), toElement);
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return this.subSet(fromElement, last());
    }

    void adder(Node<T> n, TreeSet<T> set, T fromElement, T toElement) {
        if (n != null && ((T) n.value).compareTo(fromElement) >= 0 && ((T) n.value).compareTo(toElement) < 0) {
            set.add((T) n.value);
            this.adder(n.left, set, fromElement, toElement);
            this.adder(n.right, set, fromElement, toElement);
        } else if (n != null && ((T) n.value).compareTo(fromElement) < 0)
            this.adder(n.right, set, fromElement, toElement);
        else if (n != null && ((T) n.value).compareTo(toElement) >= 0) this.adder(n.left, set, fromElement, toElement);
    }


    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    public Node firstN() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    @Override
    public boolean remove(Object o) {
        Node<T> del = this.find((T) o);
        if (del == null || del.value.compareTo((T) o) != 0) return false;
        Node<T> pr = del.parent;
        if (del.left == null && del.right == null) {
            if (del.value.equals(root.value)) root = null;
            else if (pr.left == del) pr.left = null;
            else pr.right = null;
        } else if (del.left == null || del.right == null) {
            if (del.left == null) {
                if (del.value.equals(root.value)) {
                    root = del.right;
                    if (root.right != null) root.right.parent = root;
                } else if (pr.left == del) {
                    pr.left = del.right;
                    pr.left.parent = del.parent;
                } else {
                    pr.right = del.right;
                    pr.right.parent = del.parent;
                }
            } else {
                if (del.value.equals(root.value)) {
                    root = del.left;
                    if (root.left != null) root.left.parent = root;
                } else if (pr.left == del) {
                    pr.left = del.left;
                    pr.left.parent = del.parent;
                } else {
                    pr.right = del.left;
                    pr.right.parent = del.parent;
                }
            }
        } else {
            Node<T> cur = this.next(del);

            del.value = cur.value;
            if (cur.parent.left == cur) {
                cur.parent.left = cur.right;
                if (cur.right != null) cur.right.parent = cur.parent;
            } else {
                cur.parent.right = cur.left;
                if (cur.left != null) cur.right.parent = cur.parent;
            }
        }
        size--;
        try {
            if (root != null)
                this.rebuild(root);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Node<T> next(Node<T> node) {
        if (node.right != null) return minimum(node.right);
        Node y = node.parent;
        while (y != null && node == y.right) {
            node = y;
            y = y.parent;
        }
        return y;
    }

    public Node<T> minimum(Node min) {
        if (min.left == null) return min;
        return minimum(min.left);
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    @Override
    public Iterator iterator() {
        return new ScapegoatTreeIterator();
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    public static class Node<T> implements Cloneable {
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;
        T value;

        Node(T value) {
            this.value = value;
        }

        public Node<T> clone() throws CloneNotSupportedException {
            Node<T> clone = (Node<T>) super.clone();
            return clone;
        }

        public String toString() {
            String v;
            String p;
            String l;
            String r;
            if (left == null) l = "null";
            else l = left.value.toString();
            if (right == null) r = "null";
            else r = right.value.toString();
            if (parent == null) p = "null";
            else p = parent.value.toString();
            v = value.toString();
            return "Value " + v + " left " + l + " right " + r + " par " + p;
        }
    }

    public class ScapegoatTreeIterator implements Iterator<T> {
        ArrayDeque<Node<T>> deq = new ArrayDeque<>();
        Node<T> cur = null;

        private ScapegoatTreeIterator() {
            fillStack(root);
        }

        public void fillStack(Node<T> cur) {
            while (cur != null) {
                deq.push(cur);
                cur = cur.left;
            }
        }

        /**
         * Проверка наличия следующего элемента
         * <p>
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         * <p>
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         * <p>
         * Средняя
         */
        @Override
        public boolean hasNext() {
            return !deq.isEmpty();
        }

        /**
         * Получение следующего элемента
         * <p>
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         * <p>
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         * <p>
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         * <p>
         * Средняя
         */


        @Override
        public T next() {
            cur = deq.pop();
            if (cur.right != null) fillStack(cur.right);
            return cur.value;
        }

        /**
         * Удаление предыдущего элемента
         * <p>
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         * <p>
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         * <p>
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         * <p>
         * Сложная
         */
        @Override
        public void remove() {
            if (cur != null) {
                ScapegoatTree.this.remove(cur);
                cur = null;
            } else throw new IllegalStateException();
        }
    }
}
