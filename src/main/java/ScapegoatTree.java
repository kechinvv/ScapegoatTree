import java.util.*;

public class ScapegoatTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {
    Node<T> root;
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

    public void rebuild(Node<T> scape) {
        list = new ArrayList<>();
        inOrder(scape);
        if (scape.parent == null) {
            root = build(0, list.size());
            root.parent = null;
        } else if (scape.parent.right != null && scape.parent.right.value.equals(scape.value))
            scape.parent.right = build(0, list.size());
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

    @Override
    public boolean add(T t) {
        Node<T> cur = addE((T) t);
        if (cur == null) return false;
        Node<T> goat = findScapegoat(cur);
        if (goat != null) {
            rebuild(goat);
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
        Node<T> rem = find((T) o);
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
            if (rem.left != null) rem.left.parent = rem.parent;
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

    @Override
    public boolean addAll(Collection c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean removeAll(Collection c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection c) {
        return false;
    }

    @Override
    public boolean containsAll(Collection c) {
        return false;
    }


    public Node findScapegoat(Node<T> n) {
        while (n.parent != null) {
            if (getSize(n.left) > getSize(n) * a || getSize(n.right) > getSize(n) * a) return n;
            n = n.parent;
        }
        return null;
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
    public Object[] toArray() {
        return new Object[0];
    }


    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
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

    private static class Node<T> {
        T value;
        Node<T> left = null;
        Node<T> right = null;
        Node<T> parent = null;

        Node(T value) {
            this.value = value;
        }
    }

    public class ScapegoatTreeIterator implements Iterator<T> {
        ArrayDeque<Node<T>> deq = new ArrayDeque<>();
        T cur = null;

        private ScapegoatTreeIterator() {
            if (root != null) fillStack(root);
        }

        public void fillStack(Node<T> cur) {
            if (cur.left != null) fillStack(cur.left);
            deq.push(cur);
            if (cur.right != null) fillStack(cur.right);
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
            cur = deq.removeLast().value;
            return cur;
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
