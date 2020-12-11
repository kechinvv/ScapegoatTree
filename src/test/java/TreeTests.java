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
        System.out.println(set);
        while (iterator.hasNext()) {
            Integer a = iterator.next();
            Assert.assertTrue(tree.add(a));
            System.out.println(tree.find(a));
            Assert.assertTrue(tree.contains(a));
        }
        Iterator<Integer> treeit = tree.iterator();
        for (int i = 0; i < tree.size(); i++) {
            Integer j = treeit.next();
            System.out.println(tree.find(j));
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
        Set sub = tree.subSet(tree.first(),tree.last());
        Assert.assertEquals(sub.size(), tree.size()-1);

    }

    @Test
    public void remove() {
        for (Integer i : set) {
            Assert.assertTrue(tree.remove(i));
        }
        Assert.assertTrue(tree.isEmpty());
        this.create();
        for (Integer e: forR) {
            System.out.println(e);
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
    }
}
