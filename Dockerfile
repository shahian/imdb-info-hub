FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY target/*.jar app.jar

# Install CA certificates
RUN apt-get update && apt-get install -y ca-certificates && update-ca-certificates

# Create directory for H2 database
RUN mkdir -p /app/h2db && \
    chown -R 1000:1000 /app

# Create non-root user
RUN useradd -m -u 1000 -s /bin/bash appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

# Memory settings for 5GB data import (~6GB heap for processing)
ENV JAVA_OPTS="-Xms6144m -Xmx6144m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -server"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]