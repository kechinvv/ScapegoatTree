import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TreeTests {
    public int createSize() {
        int min = 11;
        int max = 51;
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
    public void test() {
        int size = createSize();
        ScapegoatTree tree = create(size);
        Assert.assertNotNull(tree);
        Assert.assertEquals(size, tree.size());
    }
}
