ALTER TABLE t_self_test_submissions
    ADD lang VARCHAR(255);

UPDATE t_self_test_submissions
SET lang = 'java'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Java%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'cc17'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C++%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'py3'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Python%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'py3'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%PyPy%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'cs'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C#%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'rs'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Rust%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'kt'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Kotlin%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'pas'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Pascal%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'go'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Go%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'c'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C%')
  AND lang IS NULL;

UPDATE t_self_test_submissions
SET lang = 'cat'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%文本输出%')
  AND lang IS NULL;

ALTER TABLE t_self_test_submissions
    ALTER COLUMN lang SET NOT NULL;

ALTER TABLE t_submissions
    ADD lang VARCHAR(255);

UPDATE t_submissions
SET lang = 'java'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Java%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'cc17'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C++%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'py3'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Python%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'py3'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%PyPy%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'cs'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C#%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'rs'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Rust%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'kt'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Kotlin%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'pas'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Pascal%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'go'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%Go%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'c'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%C%')
  AND lang IS NULL;

UPDATE t_submissions
SET lang = 'cat'
WHERE language_id IN (SELECT t_languages.language_id FROM t_languages WHERE language_name LIKE '%文本输出%')
  AND lang IS NULL;

ALTER TABLE t_submissions
    ALTER COLUMN lang SET NOT NULL;
