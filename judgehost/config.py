import os

SERVER_URL = os.environ.get("SERVER_URL", "http://127.0.0.1:18080")
serverhost = f"{SERVER_URL}"

JUDGER_ID = os.environ.get("JUDGER_ID", None)
if JUDGER_ID is None:
    raise Exception("JUDGER_ID is not set")

JUDGE_TOKEN = os.environ.get("JUDGE_TOKEN", None)
if JUDGE_TOKEN is None:
    raise Exception("JUDGE_TOKEN is not set")

NAME = os.environ.get("NAME", "Jury")

requestsHeader = {
    'X-Judge-Server-Token': JUDGE_TOKEN,
    'Content-type': 'application/json',
}
