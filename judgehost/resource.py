import threading


class Resource:
    thread = ''
    utime_max = 0
    stime_max = 0

    def __init__(self, p):
        self.thread = threading.Thread(target=self.listener, args=[p])

    def listener(self, p):
        while p.poll() is None:
            try:
                with open('/proc/%d/stat' % p.pid, "r") as file:
                    stat = str(file.read()).split(' ')
                    utime = int(stat[13])  # 用户态时间，stat 第 14 位
                    stime = int(stat[14])  # 内核态时间，stat 第 15 位
                    self.utime_max = max(self.utime_max, utime)
                    self.stime_max = max(self.stime_max, stime)
            except:
                pass

    def start(self):
        self.thread.start()

    def getResource(self):
        return [self.utime_max, self.stime_max]
