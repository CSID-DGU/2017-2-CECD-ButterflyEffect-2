bool isColorNeeded(int bodyPart){
	//0 : nose, 1:neck, 2:leftS, 4:rightS
	int colorNeededSet[4] = {0,1,2,5}; 
	for(int i=0;i<4;i++){
		if(bodyPart == colorNeededSet[i]) return true;
	}
	return false;

};

