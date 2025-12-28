// Copyright 2020 The casbin Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.casbin.jcasbin.main.benchmark.enforcer;

import org.casbin.jcasbin.main.Enforcer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark for RBAC model with resource roles.
 * Data scale: 6 rules (2 users, 2 roles).
 */
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
public class BenchmarkRBACModelWithResourceRoles {
    @State(Scope.Benchmark)
    public static class BenchmarkState {
        private Enforcer e;

        @Setup(Level.Trial)
        public void setup() {
            e = new Enforcer(
                "examples/rbac_with_resource_roles_model.conf",
                "examples/rbac_with_resource_roles_policy.csv",
                false
            );
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BenchmarkRBACModelWithResourceRoles.class.getName())
                .exclude("Pref")
                .warmupIterations(3)
                .measurementIterations(5)
                .addProfiler(GCProfiler.class)
                .forks(2)
                .build();
        new Runner(opt).run();
    }

    @Threads(1)
    @Benchmark
    public void benchmarkRBACModelWithResourceRoles(BenchmarkState state) {
        state.e.enforce("alice", "data1", "read");
    }
    
    
}
