package org.casbin.jcasbin.main.benchmark.enforcer;

import org.casbin.jcasbin.main.Enforcer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Threads(1)
@Fork(1)
public class BenchmarkKeyMatchModel {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private Enforcer e;

        @Setup(Level.Trial)
        public void setup() {
            e = new Enforcer("examples/keymatch_model.conf", "examples/keymatch_policy.csv", false);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkKeyMatchModel.class.getName())
                .exclude("Pref")
                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void benchmarkKeyMatchModel(BenchmarkState state) {
        state.e.enforce("alice", "/alice_data/resource1", "GET");
    }
}
