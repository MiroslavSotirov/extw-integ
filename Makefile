MVN=./mvnw


.PHONY: build test

all: test build

test:
	$(MVN) test

build:
	$(MVN) compile quarkus:dev