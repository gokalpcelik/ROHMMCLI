all: clean java

java: 
	mvn package

clean: 
	rm -rf target
	mvn clean