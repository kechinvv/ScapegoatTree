import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static io.qala.datagen.RandomValue.between;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TreeTests {
    ScapegoatTree<Integer> tree;
    Set<Integer> set;

    @Before
    public void create() {
        set = new HashSet();
        for (int i = 0; i < 30; i++) {
            set.add(between(0, 100).integer());
        }
        tree = new ScapegoatTree<>();
        Iterator<Integer> iterator = set.iterator();

        for (int i = 0; i < set.size(); i++) {
            tree.add(iterator.next());
        }

    }

    @Test
    public void size() {
        Assert.assertTrue(set.size() == tree.size());
    }

    @Test
    public void remove() {
        Integer rem = set.iterator().next();
        Assert.assertTrue(tree.remove(rem));
        set.remove(rem);
        Assert.assertFalse("Tree contain removed element",tree.contains(rem));
        Assert.assertFalse("Element must be already removed",tree.remove(rem));
        Assert.assertTrue(set.size()==tree.size());
        set.remove(tree.root.value);
        Assert.assertTrue(tree.remove(tree.root.value));
        Assert.assertTrue(set.size()==tree.size());
    }

    @Test
    public void iterator(){
        tree=new ScapegoatTree<>();
        Assert.assertFalse(tree.iterator().hasNext());
        Throwable thrown = catchThrowable(() -> { tree.iterator().next(); });
        assertThat(thrown);
    }
}
