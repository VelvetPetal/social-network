# Сборка образов для Docker

1. Создаем в корне проекта необходимый Dockerfile для вашего сервиса. Создавать нужно именно в корне, чтобы у докера был
   контекст всего приложения, а не только вашего сервиса. Имя файла должно быть Dockerfile-servName.
2. В Dockerfile прописываем шаги для сборки образа вашего сервиса. Должно быть 2 контейнера:
    1. Установка зависимостей для компиляции проекта и сама компиляция.
    2. Образ для запуска, который желательно делать без jdk, только jre.
3. Добавляем мавен плагин, чтобы задать желаемое имя джарника
   ```xml
   <plugin>
       <groupId>org.apache.maven.plugins</groupId>
       <artifactId>maven-jar-plugin</artifactId>
       <version>3.2.0</version>
       <configuration>
           <finalName>jarName</finalName>
       </configuration>
   </plugin>
   ```
4. Компилируем командой

```shell
docker buildx build --build-arg SN_DB_HOST=${CONTAINER_IP} --build-arg SN_DB_PORT=${PORT} --build-arg SN_DB_NAME=${DB_NAME} --build-arg SN_DB_USER=${USER} --build-arg SN_DB_PASSWORD=${PASSWORD} --platform linux/amd64 -f Dockerfile-servName --tag intouchgroup/imageName:latest . --load 2>&1 | tee build.log
```

где

- `-f Dockerfile-notifications` - аргумент, указывающий Dockerfile вашего сервиса
- `imageName` - желаемое имя образа
- `2>&1 | tee build.log` вывод логов в файл, чтобы можно было разобраться в случае чего в чем проблема с созданием
  образа.  
  **Аргументы из команды вам скорее всего не понадобятся. Они нужны только если во время сборки вам обязательно
  подключение к БД.**

# Запуск проекта

Проект запускается при помощи `docker compose`.

1. Перед запуском убедитесь, что в вашей системе существуют все необходимые переменные среды:

   ```
   SN_DB_PASSWORD
   SN_DB_NAME
   SN_DB_USER
   SN_DB_PORT
   SN_NOTIFICATIONS_PORT
   SN_AGGREGATOR_PORT=8080
   SN_NOTIFICATIONS_PORT=8888
   ```
   Везде где указаны выше указаны значения, это рекомендуемые по умолчанию значения.  
   Переменная `SN_NOTIFICATIONS_HOST` - это хост для подключения к микросервису уведомлений внутри контейнера. При
   подключении своего микросервиса необходимо добавить хост своего микросервиса аналогичным образом. По умолчанию
   значение переменной совпадает с именем контейнера и указывается как переменная контейнеров микросервиса и агрегатора.
2. Запуск происходит командой
    ```shell
    docker compose up -d
    ```
