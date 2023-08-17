import re
import shlex
import subprocess
from constant import *


def judge(num: int, language: str, time_limit: int, mem_limit: int, testcase: int):
    try:
        cmd = f"./sandbox.exe {language} {num}.in user.out {time_limit} {mem_limit} {testcase}"
        print(cmd)
        p = subprocess.Popen(
            shlex.split(cmd),
            shell=False,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
        )
        p.wait(timeout=time_limit / 1000 * 4)
        out, err = p.communicate()
        out = out.decode(encoding="utf-8").split(" ")
    except subprocess.TimeoutExpired as e:
        pid = p.pid
        with open("/proc/%d/status" % pid, "r") as f:
            status = f.read()
            m = re.search("VmPeak:(.*)kB", status)
        p.kill()
        memory = 0
        if m is not None:
            memory = m.group(1).strip()
        return [TIME_LIMIT_EXCEEDED, time_limit, memory]
    print(out)
    if out[0] == "t":
        return [TIME_LIMIT_EXCEEDED, out[1], out[2]]
    elif out[0] == "r":
        return [RUNTIME_ERROR, out[1], out[2]]
    elif out[0] == "m":
        return [MEMORY_LIMIT_EXCEEDED, out[1], out[2]]
    elif out[0] == "o":
        return [OUTPUT_LIMIT_EXCEEDED, out[1], out[2]]
    with open(
        "testcase/%s/%s.out" % (testcase, num), "r", encoding="utf-8", errors="ignore"
    ) as f:
        std = f.read().replace("\r", "").rstrip()  # 删除\r,删除行末的空格和换行
    with open("app/user.out", "r", encoding="utf-8", errors="ignore") as f:
        user = f.read().replace("\r", "").rstrip()
    if std == user:
        return [ACCEPTED, out[1], out[2]]
    else:
        return [WRONG_ANSWER, out[1], out[2]]
