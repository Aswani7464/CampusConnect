FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mkdir -p out && find . -name "*.java" -print0 | xargs -0 javac -d out

EXPOSE 8080

CMD ["sh", "-c", "java -cp out Server"]
