INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('文本输出', 'echo ''DO NOT NEED TO COMPILE''', 'cat a', 1, 1, 'a', 'a', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('C11', 'gcc -DONLINE_JUDGE -fno-tree-ch -O2 -Wall -std=c11 -pipe a.c -lm -o a', '/w/a', 1, 1, 'a.c', 'a', true,
        now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('C++17', 'g++ -DONLINE_JUDGE -fno-tree-ch -O2 -Wall -std=c++17 -pipe a.cc -lm -o a', '/w/a', 1, 1, 'a.cc', 'a',
        true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Java 17', 'javac -d /w -encoding utf8 ./Main.java', '/usr/bin/java Main', 2, 2, 'Main.java', 'Main.class',
        true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Python 3', '/usr/bin/python3 -c "import py_compile; py_compile.compile(''main.py'', ''main'', doraise=True)"',
        '/usr/bin/python3 main', 1, 1, 'main.py', 'main', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('PyPy 3',
        '/usr/bin/pypy3 -c "import py_compile; py_compile.compile(''/w/main.py'', ''/w/main'', doraise=True)" && mv main.py main',
        'mv main main.py && /usr/bin/pypy3 -B main.py', 1, 1, 'main.py', 'main', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('C#', '/usr/bin/mcs -optimize+ -out:/w/foo /w/foo.cs',
        '/usr/bin/mono foo', 1, 1, 'foo.cs', 'foo', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Rust', '/usr/bin/rustc -O -o /w/foo /w/foo.rs',
        '/w/foo', 1, 1, 'foo.rs', 'foo', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Pascal', '/usr/bin/fpc -O2 -o/w/foo foo.pas',
        '/w/foo', 1, 1, 'foo.pas', 'foo', true, now(), now());

INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Go', 'env GOPATH=/w GOCACHE=/tmp/ /usr/bin/go build -o foo foo.go',
        '/w/foo', 1, 1, 'foo.go', 'foo', true, now(), now());
