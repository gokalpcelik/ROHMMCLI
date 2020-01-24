all: clean java

java8: clean mvn8

mvn8:
	mvn package -P Java8

java: 
	mvn package -P Java11

clean: 
	rm -rf target
	mvn clean