INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Kotlin', 'kotlinc -d /w ./Main.kt && jar cvf Main.jar *.class >/dev/null',
        'kotlin -cp Main.jar MainKt', 2, 2, 'Main.kt', 'Main.jar', true, now(), now());
