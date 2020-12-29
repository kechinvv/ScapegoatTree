package treeBinary;

import java.util.*;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements SortedSet<T> {

    private Node<T> root = null;
    private int size = 0;

    @Override
    public int size() {
        return size;
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


    private List<Node<T>> findwithParent(Node<T> start, T value, Node<T> parrent) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return List.of(start, parrent);
        } else if (comparison < 0) {
            if (start.left == null) return List.of(start, parrent);
            return findwithParent(start.left, value, start);
        } else {
            if (start.right == null) return List.of(start, parrent);
            return findwithParent(start.right, value, start);
        }
    }
    // трудоемкость: O(log n)  ресурсоемкость: O(m)


    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     * <p>
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * <p>
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     * <p>
     * Пример
     */
    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    /**
     * Удаление элемента из дерева
     * <p>
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     * <p>
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     * <p>
     * Средняя
     */
    @Override
    public boolean remove(Object o) {
        boolean r = false;
        List<Node<T>> list = this.findwithParent(root, (T) o, root);
        Node<T> node = list.get(0);
        Node<T> par = list.get(1);
        int comparison = node == null ? -1 : ((T) o).compareTo(node.value);
        if (comparison != 0) {
            return false;
        }
        if (node.value.equals(root.value)) {
            r = true;
        }
        if (node.right == null) {
            if (r) root = node.left;
            else if (par.right != null && par.right.value.equals(node.value)) par.right = node.left;
            else par.left = node.left;
            size--;
        } else if (node.left == null) {
            if (r) root = node.right;
            else if (par.right != null && par.right.value.equals(node.value)) par.right = node.right;
            else par.left = node.right;
            size--;
        } else {
            Node<T> current;
            if (node.right.left != null) {
                current = node.right;
                while (true) {
                    if (current.left.left == null) break;
                    current = current.left;
                }
                Node<T> newnode = new Node(current.left.value);
                newnode.left = node.left;
                newnode.right = node.right;
                this.remove(current.left.value);
                if (r) root = newnode;
                else if (par.right != null && par.right.value.equals(node.value)) par.right = newnode;
                else par.left = newnode;
            } else {
                Node<T> newnode = new Node(node.right.value);
                newnode.left = node.left;
                newnode.right = node.right.right;
                node.right = null;
                if (r) root = newnode;
                else if (par.right != null && par.right.value.equals(node.value)) par.right = newnode;
                else par.left = newnode;
                size--;
            }
        }
        return true;
    }

    // трудоемкость: O(log n)  ресурсоемкость: O(6)


    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
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
    // трудоемкость: O(2^log n) ресурсоемкость: O(m*2^log n)

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */

    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        if (fromElement == null || toElement == null) throw new NullPointerException();
        TreeSet<T> set = new TreeSet<T>() {
            BinarySearchTree<T> b = BinarySearchTree.this;

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
// трудоемкость: O(2^log n) ресурсоемкость: O(m*2^log n)

    /**
     * Подмножество всех элементов строго меньше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */

    @Override
    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        return null;
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

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
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
        final T value;
        Node<T> left = null;
        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    public class BinarySearchTreeIterator implements Iterator<T> {
        ArrayDeque<Node<T>> deq = new ArrayDeque<>();
        T cur = null;

        private BinarySearchTreeIterator() {
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
        // Трудоёмкость - O(1); Ресурсоёмкость - O(1)

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
        // Трудоёмкость - O(1); Ресурсоёмкость - O(1)

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
                BinarySearchTree.this.remove(cur);
                cur = null;
            }
            else throw new IllegalStateException();
        }
        // трудоемкость: O(log n)  ресурсоемкость: O(6)
    }
}