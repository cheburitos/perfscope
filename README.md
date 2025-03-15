(Work in progress, POC) A simple viewer for raw ptrace data stored in SQLite database.

### How to use

Only Linux and Intel CPU is supported. Record you program:

    sudo perf record --kcore -e intel_pt/cyc/ ./lsm-tree

Then dump all data to SQLite using [export-to-sqlite.py](https://github.com/torvalds/linux/blob/master/tools/perf/scripts/python/export-to-sqlite.py) script:

    sudo perf script --itrace=bep -s ~/export-to-sqlite.py sqlite branches calls

Open database in perfscope:

![perfscope](https://github.com/0xaa4eb/perfscope/blob/master/docs/images/example.png)

Perfscope is similar to the script [exported-sql-viewer.py](https://github.com/torvalds/linux/blob/master/tools/perf/scripts/python/exported-sql-viewer.py) which
supports building call tree. Implementing additional staff is in progress.


### Build

Generate JOOQ pojos `./gradlew generateJooq`
Build `./gradlew clean generateJooq build`

### How to use

TBD