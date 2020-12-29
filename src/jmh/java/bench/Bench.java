package bench;

import org.junit.Before;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import tree.ScapegoatTree;
import treeBinary.BinarySearchTree;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static io.qala.datagen.RandomValue.between;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 0, jvmArgs = {"-Xms2G", "-Xmx2G"}) //2
public class Bench {


    @Param({"0", "100000", "200000", "300000", "400000"})
    public int listSize;
    public ScapegoatTree<Integer> tree = new ScapegoatTree<>();
    public ScapegoatTree<Integer> treeA = new ScapegoatTree<>(0.9);
    public BinarySearchTree<Integer> treeB = new BinarySearchTree<>();

    ArrayList<Integer> list = new ArrayList<>();

    @Setup
    public void SetList() {
        for (int i = 0; i < listSize; i++) list.add(between(-30000, 30000).integer());
    }


    @Benchmark
    public void setUp5(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            // tree.add(between(-30000, 30000).integer());
            tree.add(list.get(i));
        }
        black.consume(tree);
    }

    @Benchmark
    public void setUpBinary(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            // tree.add(between(-30000, 30000).integer());
            treeB.add(list.get(i));
        }
        black.consume(tree);
    }

    @Benchmark
    public void setUp9(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            // treeA.add(between(-30000, 30000).integer());
            treeA.add(list.get(i));
        }
        black.consume(treeA);
    }

    @Benchmark
    public void find5(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            // tree.add(between(-30000, 30000).integer());
            tree.contains(list.get(i));
        }
        black.consume(tree);
    }

    @Benchmark
    public void find9(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            treeA.contains(list.get(i));
        }
        black.consume(tree);
    }

    @Benchmark
    public void findB(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            // tree.add(between(-30000, 30000).integer());
            treeB.contains(list.get(i));
        }
        black.consume(tree);
    }
}
