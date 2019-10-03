FROM openjdk:11-jdk
WORKDIR /app
COPY . /app
ENTRYPOINT ["sh", "./entrypoint.sh"]