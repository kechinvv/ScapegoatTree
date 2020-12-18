package bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import tree.ScapegoatTree;

import java.util.concurrent.TimeUnit;

import static io.qala.datagen.RandomValue.between;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class Bench {


    @Param({"0", "100000", "200000", "300000", "400000", "500000",
            "600000", "700000", "800000"})
    public int listSize;
    public ScapegoatTree<Integer> tree = new ScapegoatTree<>();
    public ScapegoatTree<Integer> treeA = new ScapegoatTree<>(0.9);

    /*
   public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Bench.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

*/

    @Benchmark
    public void setUpJ(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            tree.add(between(-30000, 30000).integer());
        }
        black.consume(tree);
    }

    @Benchmark
    public void setUpA(Blackhole black) {
        for (int i = 0; i < listSize; i++) {
            treeA.add(between(-30000, 30000).integer());
        }
        black.consume(treeA);
    }
}
