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
public class BenchmarkCachedRBACModelWithDomains {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private CachedEnforcer e;

        @Setup(Level.Trial)
        public void setup() {
            e = new CachedEnforcer("examples/rbac_with_domains_model.conf", "examples/rbac_with_domains_policy.csv", false);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkCachedRBACModelWithDomains.class.getName())
                .exclude("Pref")
                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void benchmarkCachedRBACModelWithDomains(BenchmarkState state) {
        state.e.enforce("alice", "domain1", "data1", "read");
    }
}

