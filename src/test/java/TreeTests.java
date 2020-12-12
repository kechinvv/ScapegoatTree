import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static io.qala.datagen.RandomValue.between;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TreeTests {
    ScapegoatTree<Integer> tree;
    Set<Integer> set;
    List<Integer> forR;

    @Before
    public void create() {
        set = new HashSet<Integer>();
        forR=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Integer rand = between(0, 100).integer();
            if (between(0,2).integer()==0 && !set.contains(rand)) forR.add(rand);
            set.add(rand);

        }
        tree = new ScapegoatTree<>();
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            Integer a = iterator.next();
            Assert.assertTrue(tree.add(a));
            Assert.assertTrue(tree.contains(a));
        }

    }

    @Test
    public void size() {
        Assert.assertEquals(set.size(), tree.size());
    }

    @Test
    public void order() {
        Assert.assertTrue(tree.checkInvariant());
    }

    @Test
    public void add(){
        Assert.assertFalse(tree.add(tree.first()));
    }
    @Test
    public void subSet(){
        SortedSet<Integer> sub = tree.subSet(tree.first(),tree.last());
        Assert.assertEquals(sub.size(), tree.size()-1);
        tree.add(tree.first()+1);
        Assert.assertTrue(sub.contains(tree.first()+1));
        tree.remove(tree.first()+1);
        Assert.assertFalse(sub.contains(tree.first()+1));
        sub.add(tree.first()+2);
        Assert.assertTrue(tree.contains(tree.first()+2));
        sub.remove(tree.first()+2);
        Assert.assertFalse(tree.contains(tree.first()+2));
    }

    @Test
    public void remove() {
        for (Integer i : set) {
            Assert.assertTrue(tree.remove(i));
        }
        Assert.assertTrue(tree.isEmpty());
        this.create();
        for (Integer e: forR) {
            Assert.assertTrue(tree.remove(e));
        }
        Assert.assertEquals(tree.size(), set.size() - forR.size());

    }

    @Test
    public void iterator() {
        tree = new ScapegoatTree<>();
        Assert.assertFalse(tree.iterator().hasNext());
        Throwable thrown = catchThrowable(() -> {
            tree.iterator().next();
        });
        assertThat(thrown);
        create();
        Iterator it = tree.iterator();
        Integer i = (Integer) it.next();
        it.remove();
        Assert.assertFalse(tree.contains(i));
    }

    @Test
    public void treeAlpha(){
        set = new HashSet<Integer>();
        forR=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Integer rand = between(0, 100).integer();
            if (between(0,2).integer()==0 && !set.contains(rand)) forR.add(rand);
            set.add(rand);

        }
        tree=new ScapegoatTree<>(0.9);
        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            Integer a = iterator.next();
            Assert.assertTrue(tree.add(a));
            Assert.assertTrue(tree.contains(a));
        }
        Assert.assertTrue(tree.getSize(tree.root.left)<=tree.size()*0.9 && tree.getSize(tree.root.left)<=tree.size()*0.9);
        Assert.assertFalse(tree.getSize(tree.root.left)>tree.size()*0.9 || tree.getSize(tree.root.left)>tree.size()*0.9);
    }
}
