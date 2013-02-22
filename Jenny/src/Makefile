JC = javac

SRC_DIR = src/
CONST_SRC = usedConsts/
BIN_DIR = bin2/

#JFLAGS = -cp $(BIN_DIR)$(CONST_SRC)

Jenny:
	mkdir $(BIN_DIR)
	$(JC) -d $(BIN_DIR) $(JFLAGS) $(SRC_DIR)*.java $(SRC_DIR)$(CONST_SRC)*.java
	cp -rf $(BIN_Dir)*
	
clean:
	rm -rf $(BIN_DIR)
	rm *.class
	rm -rf $(CONST_SRC)

#Constans:
#	$(JC) -d $(BIN_DIR) $(SRC_DIR)$(CONST_SRC)*.java