# Makefile for NileDotCom

JC=javac
JFLAGS=

NileDotCom:
	$(JC) $(JFLAGS) *.java

clean:
	rm *.class
