package org.casbin.jcasbin.main.benchmark.enforcer;

import org.casbin.jcasbin.main.Enforcer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
@Threads(1)
@Fork(1)
public class BenchmarkRBACModelMixed {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        @Param({"small", "medium", "large"})
        public String size;

        private Enforcer e;
        private List<List<Object>> enforcements;
        private int index;

        @Setup(Level.Trial)
        public void setup() {
            int roles = 0;
            int resources = 0;
            int users = 0;

            switch (size) {
                case "small":
                    roles = 100;
                    resources = 10;
                    users = 1000;
                    break;
                case "medium":
                    roles = 1000;
                    resources = 100;
                    users = 10000;
                    break;
                case "large":
                    roles = 10000;
                    resources = 1000;
                    users = 100000;
                    break;
            }

            e = new Enforcer("examples/rbac_model.conf", "", false);
            e.enableAutoBuildRoleLinks(false);

            // Add Policies (p, group, data, read)
            for (int i = 0; i < roles; i++) {
                String sub = String.format("group-has-a-very-long-name-%d", i);
                String obj = String.format("data-has-a-very-long-name-%d", i % resources);
                e.addPolicy(sub, obj, "read");
            }

            // Add Grouping Policies (g, user, group)
            for (int i = 0; i < users; i++) {
                String sub = String.format("user-has-a-very-long-name-%d", i);
                String group = String.format("group-has-a-very-long-name-%d", i % roles);
                e.addGroupingPolicy(sub, group);
            }

            e.buildRoleLinks();

            // Prepare mixed workload (Allow/Deny)
            enforcements = new ArrayList<>();
            for (int i = 0; i < 17; i++) {
                int userNum = (users / 17) * i;
                int roleNum = userNum % roles;
                int resourceNum = roleNum % resources;
                if (i % 2 == 0) {
                    resourceNum += 1;
                    resourceNum %= resources;
                }

                String user = String.format("user-has-a-very-long-name-%d", userNum);
                String resource = String.format("data-has-a-very-long-name-%d", resourceNum);
                
                List<Object> params = new ArrayList<>();
                params.add(user);
                params.add(resource);
                params.add("read");
                enforcements.add(params);
            }
            
            index = 0;
        }
        
        public List<Object> getNextEnforcement() {
            List<Object> params = enforcements.get(index % enforcements.size());
            index++;
            return params;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkRBACModelMixed.class.getName())
                .exclude("Pref")
                .addProfiler(GCProfiler.class)
                .forks(1)
                .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void benchmarkRBACModelMixed(BenchmarkState state) {
        List<Object> params = state.getNextEnforcement();
        state.e.enforce(params.toArray());
    }
}
