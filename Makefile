all: clean java

java8: clean mvn8

mvn8:
	mvn -f pom8.xml package

java: 
	mvn package

clean: 
	rm -rf target
	mvn clean