INSERT INTO t_languages(language_name, compile_command, execute_command, time_limit_rate, memory_limit_rate,
                        source_filename, target_filename, enabled, created_at, updated_at)
VALUES ('Kotlin', 'kotlinc -d /w ./Main.kt', 'kotlin MainKt', 2, 2, 'Main.kt', 'MainKt.class', true, now(), now());
