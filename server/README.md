```shell
docker run -d \
   --name db-for-ntoj \
   -e POSTGRES_USER=ntoj \
   -e POSTGRES_PASSWORD=123456 \
   -e POSTGRES_DB=ntoj \
   -e PGDATA=/var/lib/postgresql/data/pgdata \
   -e TZ=Asia/Shanghai \
   -e PGTZ=Asia/Shanghai \
   -e LANG=en_US.UTF-8 \
   -p 15432:5432 \
   postgres:15
```
