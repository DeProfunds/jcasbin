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
public class BenchmarkCachedABACModel {
    public static class TestResource {
        private String Name;
        private String Owner;

        public TestResource(String name, String owner) {
            this.Name = name;
            this.Owner = owner;
        }

        public String getName() {
            return Name;
        }

        public String getOwner() {
            return Owner;
        }
    }

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private CachedEnforcer e;
        private TestResource data1;

        @Setup(Level.Trial)
        public void setup() {
            e = new CachedEnforcer("examples/abac_model.conf", "", false);
            data1 = new TestResource("data1", "alice");
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkCachedABACModel.class.getName())
                .exclude("Pref")
                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void benchmarkCachedABACModel(BenchmarkState state) {
        state.e.enforce("alice", state.data1, "read");
    }
}

