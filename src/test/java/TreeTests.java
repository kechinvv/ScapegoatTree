import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TreeTests {
    public int createRandomInt(int mn, int mx) {
        int min = mn;
        int max = mx;
        Random random = new Random();
        int n = random.nextInt(max - min + 1);
        return n + min;
    }

    public ScapegoatTree create(int size) {
        ScapegoatTree<Integer> tree = new ScapegoatTree<Integer>();
        int min = -5;
        int max = 101;
        Random random = new Random();
        int a = random.nextInt(max - min + 1);
        for (int i = 0; i < size; i++) {

            while (tree.contains(a)) {
                a = random.nextInt(max - min + 1);
                a += min;
            }
            tree.add(a);
        }
        return tree;
    }

    @Test
    public void createTest() {
        int size = createRandomInt(11, 51);
        ScapegoatTree tree = create(size);
        Assert.assertNotNull(tree);
        Assert.assertEquals(size, tree.size());
    }

    @Test
    public void remANDadd() {
        int size = createRandomInt(11, 51);
        ScapegoatTree tree = create(size);
        Assert.assertNotNull(tree);
        Assert.assertEquals(size, tree.size());
        int rnd = createRandomInt(-100, 100);
        tree.add(rnd);
        Assert.assertTrue(tree.checkInvariant());
        Assert.assertTrue(tree.contains(rnd));
        tree.remove(rnd);
        rnd = createRandomInt(-100, 100);
        tree.add(rnd);
        Assert.assertTrue(tree.checkInvariant());
        Assert.assertTrue(tree.contains(rnd));
        tree.remove(rnd);
        Assert.assertFalse(tree.contains(rnd));
        Assert.assertFalse(tree.remove(rnd));
    }
}
