all:
	clean mktargetdir java

java: 
	mvn package

mktargetdir:
	mkdir target

clean: 
	rm -rf build
	mvn clean