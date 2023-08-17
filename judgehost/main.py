import json

import os
import traceback

import requests
import subprocess
import time

import config
from compile import compile
from fetch import get_testcase
from judge import judge
from constant import *


def localtime():
    return time.strftime("%Y-%m-%d %H:%M:%S: ", time.localtime())


def get_update_params(submission_id, problem_id, result, stage, time, memory):
    return json.dumps(
        {
            "submissionId": submission_id,
            "problemId": problem_id,
            "result": result,
            "time": time,
            "memory": memory,
            "judgeStage": stage,
            "judgerId": config.JUDGER_ID,
        }
    )


connected = 0
while True:
    if connected == 0:
        print(localtime() + "正在尝试连接服务器......")
    LOOP = 1
    while LOOP == 1:
        try:
            p = subprocess.Popen(
                "top -bn 1 | grep -oE 'load average: (.*)' | awk -F': ' '{print $2}'",
                shell=True,
                stdout=subprocess.PIPE,
            )
            (load, err) = p.communicate()
            load = load.decode().split("\n")[0]
            res = requests.get(
                f"{config.serverhost}/judge_client/get_submission?load={load}",
                headers=config.requestsHeader,
            )
            if res.status_code >= 500:
                print(localtime() + "服务器错误！5秒后尝试重新连接......")
                time.sleep(5)
                continue

            if res.status_code == 204:
                # 没有新的待评测的提交
                time.sleep(1)
                continue

            if res.status_code == 401:
                print(localtime() + "该评测机未通过服务器鉴权，请确认评测机token与服务器配置一致。")
                os._exit(0)

            if connected == 0:
                connected = 1
                print(localtime() + "服务器连接成功！开始监听待评测提交......")
            LOOP = 0
        except Exception as ex:
            print(localtime() + "服务器连接失败！5秒后尝试重新连接......")
            traceback.print_exc()
            connected = 0
            time.sleep(5)
            continue
    data = res.json()["data"]
    print(localtime() + "提交编号：%s" % data["submissionId"])
    code = data["code"].replace("\r", "")
    lang = data["language"]
    print(data)
    try:
        ret, err = compile(code=code, language=lang)
    except:
        params = get_update_params(
            data["submissionId"], data["problemId"], COMPILE_ERROR, STAGE_FINISHED, 0, 0
        )
        print(localtime() + "评测完毕。结果：Compile Error。正在将结果发回服务器......")
        res = requests.patch(
            f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
            params,
            headers=config.requestsHeader,
        )
        continue
    err = err.replace("'", "''")[0:1024]
    if ret == -1:
        params = get_update_params(
            data["submissionId"], data["problemId"], COMPILE_ERROR, STAGE_FINISHED, 0, 0
        )
        print(localtime() + "评测完毕。结果：Compile Error。正在将结果发回服务器......")
        print(params)
        res = requests.patch(
            f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
            params,
            headers=config.requestsHeader,
        )
    elif ret == -2:
        params = get_update_params(
            data["submissionId"], data["problemId"], COMPILE_ERROR, STAGE_FINISHED, 0, 0
        )
        print(localtime() + "评测完毕。结果：System Error。正在将结果发回服务器......")
        res = requests.patch(
            f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
            params,
            headers=config.requestsHeader,
        )
    else:
        params = get_update_params(
            data["submissionId"], data["problemId"], STAGE_JUDGING, STAGE_JUDGING, 0, 0
        )
        res = requests.patch(
            f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
            params,
            headers=config.requestsHeader,
        )
        print(localtime() + "编译成功。正在从服务器拉取测试样例......")
        (total, msg) = get_testcase(
            data["testcase"]["fileId"], data["testcase"]["hash"]
        )
        if total == -1:
            print(localtime() + msg + "最终结果：System Error。正在将结果发回服务器......")
            params = get_update_params(
                data["submissionId"],
                data["problemId"],
                SYSTEM_ERROR,
                STAGE_FINISHED,
                0,
                0,
            )
            res = requests.patch(
                f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
                params,
                headers=config.requestsHeader,
            )
            print(localtime() + "开始从服务器上监听提交......")
            continue
        print(localtime() + msg)
        max_time_cost, max_mem_cost = 0, 0
        print('------------------')
        for i in range(1, total + 1):
            print(localtime() + "测试点%s: 等待结果......" % i)
            language = lang['name']
            if "C++" in lang['name']:
                language = "cpp"
            elif "C" in lang['name']:
                language = "c"
            elif "Java" in lang['name']:
                language = "java"
            elif "Python 3" in lang['name']:
                language = "py3"
            result, time_cost, mem_cost = judge(
                i, language, data["timeLimit"], data["memoryLimit"], data["testcase"]['fileId']
            )
            max_time_cost = max(int(max_time_cost), int(time_cost))
            max_mem_cost = max(int(max_mem_cost), int(mem_cost))
            print(
                localtime()
                + "测试点%s评测完毕。结果：%s 运行时间：%sms 运行空间：%sKB"
                % (i, result, time_cost, mem_cost)
            )
            if result != ACCEPTED:
                break
        print(
            localtime()
            + "评测结束。最终结果：%s 最大运行时间：%sms 最大运行空间：%sKB。正在将结果发回服务器......"
            % (result, max_time_cost, max_mem_cost)
        )
        params = get_update_params(
            data["submissionId"],
            data["problemId"],
            result,
            STAGE_FINISHED,
            max_time_cost,
            max_mem_cost,
        )
        res = requests.patch(
            f"{config.serverhost}/judge_client/update_submission/{data['submissionId']}",
            params,
            headers=config.requestsHeader,
        )
    print(localtime() + "正在监听待评测提交......")
