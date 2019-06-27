FROM gcr.io/google-appengine/openjdk
COPY ./build/libs /usr/local/miskwallet
WORKDIR /usr/local/miskwallet

RUN groupadd -g 999 appuser && \
    useradd -r -u 999 -g appuser appuser
USER 999

CMD ["java", "-jar", "/usr/local/miskwallet/miskwallet-0.7.0-SNAPSHOT.jar"]
