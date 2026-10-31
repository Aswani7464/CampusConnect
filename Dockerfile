FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY . .

WORKDIR /app/backend
RUN javac -encoding UTF-8 -d . model/*.java Server.java

EXPOSE 8080
CMD ["java", "Server"]
