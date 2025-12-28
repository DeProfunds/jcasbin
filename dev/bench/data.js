window.BENCHMARK_DATA = {
  "lastUpdate": 1766956785899,
  "repoUrl": "https://github.com/DeProfunds/jcasbin",
  "entries": {
    "JCasbin Benchmark (Dev)": [
      {
        "commit": {
          "author": {
            "email": "50104361+AKonnyaku@users.noreply.github.com",
            "name": "Konnyaku",
            "username": "AKonnyaku"
          },
          "committer": {
            "email": "50104361+AKonnyaku@users.noreply.github.com",
            "name": "Konnyaku",
            "username": "AKonnyaku"
          },
          "distinct": true,
          "id": "d2ea47702b4e36593fee8d1be473951d0749d4ff",
          "message": "Fix: Configure JMH output path to avoid branch checkout conflict",
          "timestamp": "2025-12-29T05:07:39+08:00",
          "tree_id": "24165079035e11c4cbdd5a67f9677377630145be",
          "url": "https://github.com/DeProfunds/jcasbin/commit/d2ea47702b4e36593fee8d1be473951d0749d4ff"
        },
        "date": 1766956123264,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkEnforce.enforce ( {\"dataScale\":\"medium\",\"modelType\":\"rbac\",\"useCache\":\"false\"} )",
            "value": 2.431518535657396,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.addPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 1068.4379738126147,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.hasPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 6138.247042950156,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.removePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 1.0114642052084968,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.updatePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 5447.985487253239,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkMemory.measureMemory ( {\"scenario\":\"RBAC_Medium\"} )",
            "value": 383.208443,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      },
      {
        "commit": {
          "author": {
            "email": "50104361+AKonnyaku@users.noreply.github.com",
            "name": "Konnyaku",
            "username": "AKonnyaku"
          },
          "committer": {
            "email": "50104361+AKonnyaku@users.noreply.github.com",
            "name": "Konnyaku",
            "username": "AKonnyaku"
          },
          "distinct": true,
          "id": "aca42d09fcd2025ca52ab2a70d75c971daf696c5",
          "message": "Refactor: Use standard pom.xml for benchmark, remove benchmark_pom.xml",
          "timestamp": "2025-12-29T05:18:54+08:00",
          "tree_id": "ea8b975540332de59c5248aa5fe65ebee4cbddfe",
          "url": "https://github.com/DeProfunds/jcasbin/commit/aca42d09fcd2025ca52ab2a70d75c971daf696c5"
        },
        "date": 1766956785502,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkEnforce.enforce ( {\"dataScale\":\"medium\",\"modelType\":\"rbac\",\"useCache\":\"false\"} )",
            "value": 2.359636187741835,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.addPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 1073.176008809092,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.hasPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 6161.014988735816,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.removePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 1.0218771055757339,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.updatePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 5619.450526925612,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkMemory.measureMemory ( {\"scenario\":\"RBAC_Medium\"} )",
            "value": 381.4917706666667,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}