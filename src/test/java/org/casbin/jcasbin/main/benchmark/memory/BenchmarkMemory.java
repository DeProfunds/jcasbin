package org.casbin.jcasbin.main.benchmark.memory;

import org.casbin.jcasbin.main.Enforcer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(1)
@Threads(1)
public class BenchmarkMemory {

    private static Enforcer e;

    private static final List<String> memoryStats = new ArrayList<>();

    public static void main(String[] args) throws RunnerException {
        if (args != null && args.length > 0 && "scenario".equals(args[0])) {
            System.out.println(runScenario(args));
            return;
        }
        Options opt = new OptionsBuilder()
                .include(BenchmarkMemory.class.getName())
                .exclude("Pref")
                .forks(1)
                .build();
        new Runner(opt).run();

        printMemoryStats();
    }
    
    @TearDown(Level.Trial)
    public static void printMemoryStats() {
        if (memoryStats.isEmpty()) return;
        
        System.out.println("\n\n==================== MEMORY BENCHMARK RESULTS ====================");
        System.out.printf("%-30s | %-40s | %s%n", "Test case", "Rule size", "Memory overhead (KB)");
        System.out.println("-------------------------------|------------------------------------------|---------------------");
        for (String stat : memoryStats) {
            System.out.println(stat);
        }
        System.out.println("==================================================================\n");
        memoryStats.clear();
    }

    private void registerStat(String name, String size, double meanKB, double stdDevKB, double variance) {
        String stat = String.format("%-30s | %-40s | Mean: %8.3f KB | StdDev: %6.3f KB | Var: %10.0f", 
            name, size, meanKB, stdDevKB, variance);
        memoryStats.add(stat);
        System.out.println(stat);
    }

    @Benchmark
    public void benchmarkACL() {
        measure("ACL", "2 rules (2 users)", () -> {
            e = new Enforcer("examples/basic_model.conf", "examples/basic_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkRBAC() {
        measure("RBAC", "5 rules (2 users, 1 role)", () -> {
            e = new Enforcer("examples/rbac_model.conf", "examples/rbac_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkRBACSmall() {
        measure("RBAC (small)", "1100 rules (1000 users, 100 roles)", () -> {
            e = new Enforcer("examples/rbac_model.conf", "", false);
            e.enableAutoBuildRoleLinks(false);
            for (int i = 0; i < 100; i++) {
                e.addPolicy(String.format("group%d", i), String.format("data%d", i / 10), "read");
            }
            for (int i = 0; i < 1000; i++) {
                e.addGroupingPolicy(String.format("user%d", i), String.format("group%d", i / 10));
            }
            e.buildRoleLinks();
        });
    }

    @Benchmark
    public void benchmarkRBACMedium() {
        measure("RBAC (medium)", "11000 rules (10000 users, 1000 roles)", () -> {
            e = new Enforcer("examples/rbac_model.conf", "", false);
            e.enableAutoBuildRoleLinks(false);
            List<List<String>> pRules = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                List<String> line = new ArrayList<>();
                line.add(String.format("group%d", i));
                line.add(String.format("data%d", i / 10));
                line.add("read");
                pRules.add(line);
            }
            e.addPolicies(pRules);

            List<List<String>> gRules = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                List<String> line = new ArrayList<>();
                line.add(String.format("user%d", i));
                line.add(String.format("group%d", i / 10));
                gRules.add(line);
            }
            e.addGroupingPolicies(gRules);
            e.buildRoleLinks();
        });
    }

    @Benchmark
    public void benchmarkRBACLarge() {
        measure("RBAC (large)", "110000 rules (100000 users, 10000 roles)", () -> {
            e = new Enforcer("examples/rbac_model.conf", "", false);
            e.enableAutoBuildRoleLinks(false);
            List<List<String>> pRules = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                List<String> line = new ArrayList<>();
                line.add(String.format("group%d", i));
                line.add(String.format("data%d", i / 10));
                line.add("read");
                pRules.add(line);
            }
            e.addPolicies(pRules);

            List<List<String>> gRules = new ArrayList<>();
            for (int i = 0; i < 100000; i++) {
                List<String> line = new ArrayList<>();
                line.add(String.format("user%d", i));
                line.add(String.format("group%d", i / 10));
                gRules.add(line);
            }
            e.addGroupingPolicies(gRules);
            e.buildRoleLinks();
        });
    }

    @Benchmark
    public void benchmarkRBACWithResourceRoles() {
        measure("RBAC with resource roles", "6 rules (2 users, 2 roles)", () -> {
            e = new Enforcer("examples/rbac_with_resource_roles_model.conf", "examples/rbac_with_resource_roles_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkRBACWithDomains() {
        measure("RBAC with domains/tenants", "6 rules (2 users, 1 role, 2 domains)", () -> {
            e = new Enforcer("examples/rbac_with_domains_model.conf", "examples/rbac_with_domains_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkABAC() {
        measure("ABAC", "0 rule (0 user)", () -> {
            e = new Enforcer("examples/abac_model.conf", "");
        });
    }

    @Benchmark
    public void benchmarkABACLargeSimple() {
        measure("ABAC Rule (large/simple)", "10000 rules", () -> {
            e = newABACRuleEnforcer(10000, "simple");
        });
    }

    @Benchmark
    public void benchmarkABACLargeMedium() {
        measure("ABAC Rule (large/medium)", "10000 rules", () -> {
            e = newABACRuleEnforcer(10000, "medium");
        });
    }

    @Benchmark
    public void benchmarkABACLargeComplex() {
        measure("ABAC Rule (large/complex)", "10000 rules", () -> {
            e = newABACRuleEnforcer(10000, "complex");
        });
    }

    @Benchmark
    public void benchmarkRESTful() {
        measure("RESTful", "5 rules (3 users)", () -> {
            e = new Enforcer("examples/keymatch_model.conf", "examples/keymatch_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkDenyOverride() {
        measure("Deny-override", "6 rules (2 users, 1 role)", () -> {
            e = new Enforcer("examples/rbac_with_deny_model.conf", "examples/rbac_with_deny_policy.csv");
        });
    }

    @Benchmark
    public void benchmarkPriority() {
        measure("Priority", "9 rules (2 users, 2 roles)", () -> {
            e = new Enforcer("examples/priority_model.conf", "examples/priority_policy.csv");
        });
    }

    // The measurement logic remains the same, just adapted to be called by @Benchmark methods
    private void measure(String name, String size, Runnable setup) {
        int iterations = 5; // Reduced iterations for JMH context as we use SingleShotTime
        List<Long> results = new ArrayList<>();
        
        try {
            e = null;
            forceGC();
            setup.run();
            e = null;
            forceGC();
        } catch (Exception ex) {
        }

        for (int i = 0; i < iterations; i++) {
            e = null;
            forceGC();
            long start = getUsedMemory();
            
            setup.run();
            
            forceGC();
            long end = getUsedMemory();
            long diff = end - start;
            
            if (diff > 0) {
                results.add(diff);
            } else {
                i--;
                if (i < -5) break;
            }
        }

        if (results.isEmpty()) return;

        double sum = 0;
        for (long val : results) {
            sum += val;
        }
        double mean = sum / results.size();

        double sumSqDiff = 0;
        for (long val : results) {
            sumSqDiff += Math.pow(val - mean, 2);
        }
        double variance = sumSqDiff / results.size();
        double stdDev = Math.sqrt(variance);
        
        registerStat(name, size, mean / 1024.0, stdDev / 1024.0, variance);
    }

    private static long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void forceGC() {
        for (int i = 0; i < 3; i++) {
            System.gc();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
    }

    private static class ScenarioSampleStats {
        private long count;
        private long sum;
        private long max;

        void add(long value) {
            count++;
            sum += value;
            if (value > max) {
                max = value;
            }
        }

        long getCount() {
            return count;
        }

        long getAvg() {
            return count == 0 ? 0 : sum / count;
        }

        long getMax() {
            return max;
        }
    }

    private static class ScenarioGcStats {
        private final long count;
        private final long timeMs;

        ScenarioGcStats(long count, long timeMs) {
            this.count = count;
            this.timeMs = timeMs;
        }
    }

    private static ScenarioGcStats readGcStats() {
        long count = 0;
        long timeMs = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            long c = gc.getCollectionCount();
            long t = gc.getCollectionTime();
            if (c > 0) {
                count += c;
            }
            if (t > 0) {
                timeMs += t;
            }
        }
        return new ScenarioGcStats(count, timeMs);
    }

    private static String runScenario(String[] args) {
        String scenario = args.length > 1 ? args[1] : "concurrency";
        int durationMs = args.length > 2 ? Integer.parseInt(args[2]) : 15000;
        int threads = args.length > 3 ? Integer.parseInt(args[3]) : 32;
        int services = args.length > 4 ? Integer.parseInt(args[4]) : 20;
        int iteration = args.length > 5 ? Integer.parseInt(args[5]) : 1;
        String complexity = args.length > 6 ? args[6] : "simple";
        int policies = args.length > 7 ? Integer.parseInt(args[7]) : 10000;

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ScenarioGcStats gcBefore = readGcStats();
        MemoryUsage heapBefore = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapBefore = memoryBean.getNonHeapMemoryUsage();

        ScenarioSampleStats heapStats = new ScenarioSampleStats();
        ScenarioSampleStats nonHeapStats = new ScenarioSampleStats();

        ScheduledExecutorService sampler = Executors.newSingleThreadScheduledExecutor();
        long startNs = System.nanoTime();
        sampler.scheduleAtFixedRate(() -> {
            MemoryUsage heap = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
            heapStats.add(heap.getUsed());
            nonHeapStats.add(nonHeap.getUsed());
        }, 0, 200, TimeUnit.MILLISECONDS);

        AtomicLong ops = new AtomicLong();
        try {
            if ("concurrency".equalsIgnoreCase(scenario)) {
                Enforcer enforcer = newRBACEnforcerMedium();
                long endNs = startNs + TimeUnit.MILLISECONDS.toNanos(durationMs);
                ExecutorService pool = Executors.newFixedThreadPool(threads);
                List<Future<?>> futures = new ArrayList<>();
                for (int i = 0; i < threads; i++) {
                    futures.add(pool.submit(() -> {
                        while (System.nanoTime() < endNs) {
                            enforcer.enforce("user5001", "data99", "read");
                            ops.incrementAndGet();
                        }
                    }));
                }
                for (Future<?> f : futures) {
                    try {
                        f.get();
                    } catch (Exception ex) {
                    }
                }
                pool.shutdownNow();
            } else if ("bigdata".equalsIgnoreCase(scenario)) {
                Enforcer enforcer = newRBACEnforcerLarge();
                long endNs = startNs + TimeUnit.MILLISECONDS.toNanos(durationMs);
                while (System.nanoTime() < endNs) {
                    enforcer.enforce("user50001", "data999", "read");
                    ops.incrementAndGet();
                }
            } else if ("longrun".equalsIgnoreCase(scenario)) {
                Enforcer enforcer = newRBACEnforcerMedium();
                long endNs = startNs + TimeUnit.MILLISECONDS.toNanos(durationMs);
                while (System.nanoTime() < endNs) {
                    enforcer.enforce("user5001", "data99", "read");
                    ops.incrementAndGet();
                }
            } else if ("microservices".equalsIgnoreCase(scenario)) {
                List<Enforcer> enforcers = new ArrayList<>();
                for (int i = 0; i < services; i++) {
                    enforcers.add(newRBACEnforcerSmall());
                }
                long endNs = startNs + TimeUnit.MILLISECONDS.toNanos(durationMs);
                int idx = 0;
                while (System.nanoTime() < endNs) {
                    Enforcer enforcer = enforcers.get(idx++ % enforcers.size());
                    enforcer.enforce("user501", "data9", "read");
                    ops.incrementAndGet();
                }
            } else if ("abaclarge".equalsIgnoreCase(scenario)) {
                Enforcer enforcer = newABACRuleEnforcer(policies, complexity);
                TestEvalRule sub = new TestEvalRule("alice", 30);
                long endNs = startNs + TimeUnit.MILLISECONDS.toNanos(durationMs);
                while (System.nanoTime() < endNs) {
                    enforcer.enforce(sub, "/data5000", "read");
                    ops.incrementAndGet();
                }
            } else {
                return "CASBIN_MEM_JSON={\"lang\":\"java\",\"scenario\":\"" + scenario + "\",\"error\":\"unknown scenario\"}";
            }
        } finally {
            sampler.shutdownNow();
        }

        long durationActualMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        ScenarioGcStats gcAfter = readGcStats();
        MemoryUsage heapAfter = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapAfter = memoryBean.getNonHeapMemoryUsage();

        long gcCount = Math.max(0, gcAfter.count - gcBefore.count);
        long gcTimeMs = Math.max(0, gcAfter.timeMs - gcBefore.timeMs);

        StringBuilder sb = new StringBuilder();
        sb.append("CASBIN_MEM_JSON={");
        sb.append("\"lang\":\"java\"");
        sb.append(",\"scenario\":\"").append(scenario).append("\"");
        sb.append(",\"iteration\":").append(iteration);
        sb.append(",\"duration_ms\":").append(durationActualMs);
        sb.append(",\"threads\":").append(threads);
        sb.append(",\"services\":").append(services);
        sb.append(",\"policies\":").append(policies);
        sb.append(",\"complexity\":\"").append(complexity).append("\"");
        sb.append(",\"ops\":").append(ops.get());
        sb.append(",\"heap_used_start\":").append(heapBefore.getUsed());
        sb.append(",\"heap_used_end\":").append(heapAfter.getUsed());
        sb.append(",\"heap_used_avg\":").append(heapStats.getAvg());
        sb.append(",\"heap_used_peak\":").append(heapStats.getMax());
        sb.append(",\"heap_samples\":").append(heapStats.getCount());
        sb.append(",\"nonheap_used_start\":").append(nonHeapBefore.getUsed());
        sb.append(",\"nonheap_used_end\":").append(nonHeapAfter.getUsed());
        sb.append(",\"nonheap_used_avg\":").append(nonHeapStats.getAvg());
        sb.append(",\"nonheap_used_peak\":").append(nonHeapStats.getMax());
        sb.append(",\"nonheap_samples\":").append(nonHeapStats.getCount());
        sb.append(",\"gc_count\":").append(gcCount);
        sb.append(",\"gc_time_ms\":").append(gcTimeMs);
        sb.append("}");
        return sb.toString();
    }

    private static Enforcer newRBACEnforcerSmall() {
        Enforcer enforcer = new Enforcer("examples/rbac_model.conf", "", false);
        enforcer.enableAutoBuildRoleLinks(false);
        for (int i = 0; i < 100; i++) {
            enforcer.addPolicy(String.format("group%d", i), String.format("data%d", i / 10), "read");
        }
        for (int i = 0; i < 1000; i++) {
            enforcer.addGroupingPolicy(String.format("user%d", i), String.format("group%d", i / 10));
        }
        enforcer.buildRoleLinks();
        return enforcer;
    }

    private static Enforcer newRBACEnforcerMedium() {
        Enforcer enforcer = new Enforcer("examples/rbac_model.conf", "", false);
        enforcer.enableAutoBuildRoleLinks(false);
        List<List<String>> pRules = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            List<String> line = new ArrayList<>();
            line.add(String.format("group%d", i));
            line.add(String.format("data%d", i / 10));
            line.add("read");
            pRules.add(line);
        }
        enforcer.addPolicies(pRules);

        List<List<String>> gRules = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            List<String> line = new ArrayList<>();
            line.add(String.format("user%d", i));
            line.add(String.format("group%d", i / 10));
            gRules.add(line);
        }
        enforcer.addGroupingPolicies(gRules);
        enforcer.buildRoleLinks();
        return enforcer;
    }

    private static Enforcer newRBACEnforcerLarge() {
        Enforcer enforcer = new Enforcer("examples/rbac_model.conf", "", false);
        enforcer.enableAutoBuildRoleLinks(false);
        List<List<String>> pRules = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            List<String> line = new ArrayList<>();
            line.add(String.format("group%d", i));
            line.add(String.format("data%d", i / 10));
            line.add("read");
            pRules.add(line);
        }
        enforcer.addPolicies(pRules);

        List<List<String>> gRules = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            List<String> line = new ArrayList<>();
            line.add(String.format("user%d", i));
            line.add(String.format("group%d", i / 10));
            gRules.add(line);
        }
        enforcer.addGroupingPolicies(gRules);
        enforcer.buildRoleLinks();
        return enforcer;
    }

    private static Enforcer newABACRuleEnforcer(int policyCount, String complexity) {
        Enforcer enforcer = new Enforcer("examples/abac_rule_model.conf", "", false);
        List<List<String>> pRules = new ArrayList<>();
        for (int i = 0; i < policyCount; i++) {
            List<String> line = new ArrayList<>();
            line.add(makeSubRule(i, complexity));
            line.add("/data" + i);
            line.add("read");
            pRules.add(line);
        }
        enforcer.addPolicies(pRules);
        return enforcer;
    }

    private static String makeSubRule(int i, String complexity) {
        if ("complex".equalsIgnoreCase(complexity)) {
            return "r.sub.Age > 18 && r.sub.Age < 60 && r.sub.Name != \"blocked\" && (" + (i % 2 == 0 ? "true" : "false") + " || r.sub.Name == \"alice\")";
        }
        if ("medium".equalsIgnoreCase(complexity)) {
            return "r.sub.Age > 18 && r.sub.Age < 60";
        }
        return "r.sub.Age > 18";
    }

    public static class TestEvalRule {
        private String name;
        private int age;

        TestEvalRule(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }
}
