window.BENCHMARK_DATA = {
  "lastUpdate": 1766958926750,
  "repoUrl": "https://github.com/DeProfunds/jcasbin",
  "entries": {
    "JCasbin Benchmark": [
      {
        "commit": {
          "author": {
            "name": "DeProfunds",
            "username": "DeProfunds"
          },
          "committer": {
            "name": "DeProfunds",
            "username": "DeProfunds"
          },
          "id": "907ae352d8265816581f7a6e6155373a2c83f01d",
          "message": "Benchmark/pr10 graph test",
          "timestamp": "2025-12-28T20:08:58Z",
          "url": "https://github.com/DeProfunds/jcasbin/pull/13/commits/907ae352d8265816581f7a6e6155373a2c83f01d"
        },
        "date": 1766958925947,
        "tool": "jmh",
        "benches": [
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkEnforce.enforce ( {\"dataScale\":\"medium\",\"modelType\":\"rbac\",\"useCache\":\"false\"} )",
            "value": 2.2063446109112075,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.addPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 875.0887560738743,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.hasPolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 6144.898428678942,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.removePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 0.9621066440910032,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkManagement.updatePolicy ( {\"currentRuleSize\":\"10000\"} )",
            "value": 5707.916715020915,
            "unit": "ops/ms",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          },
          {
            "name": "org.casbin.jcasbin.main.benchmark.BenchmarkMemory.measureMemory ( {\"scenario\":\"RBAC_Medium\"} )",
            "value": 395.71605033333327,
            "unit": "ms/op",
            "extra": "iterations: 3\nforks: 1\nthreads: 1"
          }
        ]
      }
    ]
  }
}