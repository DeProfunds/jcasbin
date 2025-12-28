package org.casbin.jcasbin.main.benchmark.cached;

import org.casbin.jcasbin.main.CachedEnforcer;
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
public class BenchmarkCachedRBACModelSmall {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private CachedEnforcer e;

        @Setup(Level.Trial)
        public void setup() {
            e = new CachedEnforcer("examples/rbac_model.conf", "", false);
            e.enableAutoBuildRoleLinks(false);
            for (int i = 0; i < 100; i++) {
                e.addPolicy(String.format("group%d", i), String.format("data%d", i / 10), "read");
            }
            for (int i = 0; i < 1000; i++) {
                e.addGroupingPolicy(String.format("user%d", i), String.format("group%d", i / 10));
            }
            e.buildRoleLinks();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkCachedRBACModelSmall.class.getName())
                .exclude("Pref")
                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void benchmarkCachedRBACModelSmall(BenchmarkState state) {
        state.e.enforce("user501", "data9", "read");
    }
}

