#include <errno.h>
#include <fcntl.h>
#include <seccomp.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/resource.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <time.h>
#include <unistd.h>

int fd[2];
char language[32];
char input[64], output[64];
struct timespec start, end;
struct rlimit timelimit, memlimit, outlimit;

/*
@params   argv[1]:language
          argv[2]:file_input
          argv[3]:file_output
          argv[4]:time_limit  unit: ms
          argv[5]:mem_limit  unit: MB
          argv[6]:testcase SHA1
@return   status, timeusage, memoryusage
*/
int main(int argc, char **argv) {
  sprintf(input, "testcase/%s/%s", argv[6], argv[2]);
  sprintf(output, "app/%s", argv[3]);
  strcpy(language, argv[1]);
  long tl = atoi(argv[4]); // ms
  long ml = atoi(argv[5]); // MB
  if (strcmp(language, "c") && strcmp(language, "cpp")) {
    tl *= 2;
    ml *= 2;
  }
  timelimit.rlim_cur = 1. * tl / 1000 * 2;
  timelimit.rlim_max = 1. * tl / 1000 * 3;
  memlimit.rlim_cur = ml * 1024 * 1024 * 2; // Bytes
  memlimit.rlim_max = ml * 1024 * 1024 * 3; // Bytes
  outlimit.rlim_cur = 100 * 1024 * 1024;    // Bytes, 100M
  outlimit.rlim_max = 100 * 1024 * 1024;    // Bytes, 100M
  setrlimit(RLIMIT_CPU, &timelimit);
  if (strcmp(language, "java")) {
    setrlimit(RLIMIT_AS, &memlimit);
  }
  setrlimit(RLIMIT_FSIZE, &outlimit);
  pid_t pid = fork();
  if (pid < 0) {
    printf("System Error");
    return 1;
  } else if (pid == 0) {
    dup2(open(input, O_RDONLY), STDIN_FILENO);
    dup2(open(output, O_WRONLY | O_CREAT | O_TRUNC, 0644), STDOUT_FILENO);
    if (strcmp(language, "py2") == 0) {
      char *argp[] = {"/usr/bin/python2", "app/main", (char *)NULL};
      char *envp[] = {"PATH=/usr/bin", "PYTHONIOENCODING=utf-8", NULL};
      execve("/usr/bin/python2", argp, envp);
    }
    if (strcmp(language, "py3") == 0) {
      char *argp[] = {"/usr/bin/python3", "app/main", (char *)NULL};
      char *envp[] = {"PATH=/usr/bin", "PYTHONIOENCODING=utf-8", NULL};
      execve("/usr/bin/python3", argp, envp);
    } else if (strcmp(language, "java") == 0) {
      char *argp[] = {"/usr/bin/java", "-cp", "app", "Main", (char *)NULL};
      char *envp[] = {"PATH=/usr/bin", NULL};
      execve("/usr/bin/java", argp, envp);
    }
    scmp_filter_ctx ctx;
    ctx = seccomp_init(SCMP_ACT_KILL);

    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(read), 1,
                     SCMP_A0(SCMP_CMP_LE, 2));
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(write), 1,
                     SCMP_A0(SCMP_CMP_LE, 2));
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(exit), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(exit_group), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(fstat), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(brk), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(access), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(mmap), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(munmap), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(mprotect), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(close), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(arch_prctl), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(lseek), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(uname), 0);
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(readlink), 0);
    if (strcmp(language, "pascal") == 0) {
      seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(ioctl), 0);
      seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(rt_sigaction), 0);
      seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(getrlimit), 0);
    }
    seccomp_rule_add(ctx, SCMP_ACT_ALLOW, SCMP_SYS(execve), 1,
                     SCMP_A0(SCMP_CMP_EQ, (long)"app/main"));

    seccomp_load(ctx);

    char *argp[] = {"app/main", (char *)NULL};
    execve("app/main", argp, NULL);
    printf("%d", errno);
  } else {
    int status;
    struct rusage res;
    wait4(pid, &status, 0, &res);
    long utime = res.ru_utime.tv_sec * 1000000LL + res.ru_utime.tv_usec;
    long stime = res.ru_stime.tv_sec * 1000000LL + res.ru_stime.tv_usec;
    long mu = res.ru_maxrss;               // KB
    long tu = 1. * (utime + stime) / 1000; // ms
    if (WIFEXITED(status) == 0) {
      switch (WTERMSIG(status)) {
      case SIGXCPU:
      case SIGKILL:
        tu = tl;
        putchar('t'); // Time Limit Exceeded
        break;
      case SIGSEGV:
      case SIGFPE:
      case SIGSYS:    // seccomp filter
        putchar('r'); // Runtime Error
        break;
      case SIGXFSZ:
        putchar('o'); // Output Limit
        break;
      default:
        printf("%d", WTERMSIG(status));
      }
    } else {
      if (WEXITSTATUS(status)) // 程序返回值非0视为异常退出
      {
        putchar('r'); // Runtime Error
      } else {
        if (mu > ml * 1024) {
          mu = ml * 1024;
          putchar('m'); // Memory Limit Exceeded
        } else if (tu > tl) {
          tu = tl;
          putchar('t'); // Time Limit Exceeded
        } else {
          putchar('0');
        }
      }
    }
    printf(" %ld %ld", tu, mu);
  }
  return 0;
}
