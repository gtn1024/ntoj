import requests, os, shutil, hashlib
import config


def get_testcase(testcase_file_id: int, hash: str) -> (int, str):
    print(testcase_file_id, hash)
    try:
        if not os.path.exists("testcase"):
            os.mkdir("testcase")
        if not os.path.isfile(f"testcase/{testcase_file_id}.zip"):
            # download testcase
            download_testcase(testcase_file_id)
        with open(f"testcase/{testcase_file_id}.zip", "rb") as f:
            local_hash = hashlib.md5(f.read()).hexdigest()
            if local_hash.lower() != hash.lower():
                shutil.rmtree(f"testcase/{testcase_file_id}")
                os.remove(f"testcase/{testcase_file_id}.zip")
                os.mkdir(f"testcase/{testcase_file_id}")
                print("*****数据不一致***** 检测到本地缓存但校验失败。正在重新从服务器拉取测试样例......")
                download_testcase(testcase_file_id)
        with open(f"testcase/{testcase_file_id}.zip", "rb") as f:
            if os.path.exists(f"testcase/{testcase_file_id}"):
                shutil.rmtree(f"testcase/{testcase_file_id}")
            os.mkdir(f"testcase/{testcase_file_id}")
            shutil.unpack_archive(
                f"testcase/{testcase_file_id}.zip",
                f"testcase/{testcase_file_id}",
            )
            num = get_folder_file_number(f"testcase/{testcase_file_id}") // 2
            return (num, "检测到本地缓存并通过了完整性校验。")
    except:
        return (-1, "样例拉取失败，这可能是由于服务器样例丢失或内部错误导致的。")


def download_testcase(testcase_id: int):
    if not os.path.exists("testcase"):
        os.mkdir("testcase")
    res = requests.get(
        f"{config.serverhost}/judge_client/download_testcase/{testcase_id}"
    )
    with open(f"testcase/{testcase_id}.zip", "wb") as f:
        f.write(res.content)


def get_folder_file_number(path: str) -> int:
    return len(
        [
            lists
            for lists in os.listdir(path)
            if os.path.isfile(os.path.join(path, lists))
        ]
    )
