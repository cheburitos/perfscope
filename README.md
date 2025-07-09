(Work in progress, POC) A simple viewer for raw Intel PTrace data stored in SQLite database. Might be helpful to debug 
small programs written on compiler languages like Rust, C++, Go.

### How to use

Only Linux and Intel CPU are supported. Record you program:

    sudo perf record --kcore -e intel_pt/cyc/ ./lsm-tree

Then dump all data to SQLite using [export-to-sqlite.py](https://github.com/torvalds/linux/blob/master/tools/perf/scripts/python/export-to-sqlite.py) script:

    sudo perf script --itrace=bep -s ~/export-to-sqlite.py sqlite branches calls

Open the database in perfscope:

![perfscope](https://github.com/0xaa4eb/perfscope/blob/main/docs/images/example.png)

Perfscope is similar (and can be considered as fork of) to the script [exported-sql-viewer.py](https://github.com/torvalds/linux/blob/master/tools/perf/scripts/python/exported-sql-viewer.py) which
supports building a call tree. Implementing additional staff is in progress.

### Build

* Generate JOOQ pojos `./gradlew generateJooq`
* Build `./gradlew clean generateJooq build`

### See also 
* [perf ptrace example](https://perfwiki.github.io/main/perf-tools-support-for-intel-processor-trace/)
* [magic-trace](https://github.com/janestreet/magic-trace)