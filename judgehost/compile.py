import os
import re
import shutil
import subprocess


def compile(code, language):
    if language['compileCommand'] == "":
        return [-2, "系统未正确配置该语言"]
    if os.path.exists('app'):
        shutil.rmtree('app')
    os.mkdir('app')
    if language['type'] == 'CPP':
        file = open("app/main.cpp", "w")
    elif language['type'] == 'C':
        file = open("app/main.c", "w")
    elif language['type'] == 'JAVA':
        file = open("app/Main.java", "w")
    elif language['type'] == 'PYTHON':
        file = open("app/main", "w")
    else:
        return [-1, "Language not support"]
    file.write(code)
    file.close()

    try:
        p = subprocess.Popen(language['compileCommand'], shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        p.wait(timeout=30)
        out, err = p.communicate()
    except subprocess.TimeoutExpired as e:
        p.kill()
        return [-1, 'Compile TimeoutExpired']
    if "Python" in language:
        result = out.decode()
        if "E" in result:
            return [-1, result]
        else:
            return [0, 'success']
    else:
        if err:
            return [-1, err.decode()]
        return [p.returncode, err.decode()]
