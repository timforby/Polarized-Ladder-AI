package game;

public class Main {

	//if there 
	boolean ai;
	int[][] positions = new int[13][7];
	int lastmove;
	
	public Main(boolean sai){
		//initialize if ai
		ai = sai;
		
		//go through positions and 
		//and set all to -2
		for(int i =0; i<13; i++){
			for(int j = 0; j<7; j++){
				positions[i][j] = -2;
			}
			
		}
		//go through PLAYABLE positions and 
		//set to -1
		for(int i = 0; i < 49;i++){
			int pos = idToPos(i);
			positions[pos%13][pos/13]=-1;
		}
		
	}
	
	/**
	 * Main calling function that updates info
	 * AND moves ai if it is enabled
	 * @param pl Current player playing
	 * @param id move selected by player
	 * @return returns AImove if there is AI, -1 if ai win, -2 if draw
	 */
	public int update(int pl, int id){
		int aimove=0;
		
		if(id != -1){
			int pos = idToPos(id);
			lastmove = pos;
			positions[pos%13][pos/13] = pl;
			
			//if ai check if won now and return already taken move
			if(ai){
				pl = (++pl)%2;
				int winner = Valid.winner(lastmove, positions);
				if(winner!=-1){
					//winner could be -2 which is draw
					aimove = winner>-1?-1:-2;
				}else{
					aimove = AI.aiMove(pl, positions);
					lastmove= aimove;
					positions[aimove%13][aimove/13] = pl;
				}
			}
			
		}else{
			//if ai move first
			//pl = (++pl)%2;
			//generic ai first move
			aimove = 45;
			lastmove = aimove;
			positions[aimove%13][aimove/13] = pl;
			
		}

		return posToId(aimove);
	}
	

	/**
	 * function that returns a winner
	 * @return
	 */
	public int winner(){
		return Valid.winner(lastmove, positions);
		
	}
	
	/**
	 * Function that takes in id in array of GUI
	 * @param id 0-49 as there are 48 buttons
	 * @return returns a value that can be placed in the position array 0-(13*7)
	 * 
	 * 	 Example
	 *        ,15					21,22,23,24,25,26,27
	 *     ,12,13,14				14,15,16,17,18,19,20
	 *  ,7 ,8 ,9 ,10,11		 		7 ,8 ,9 ,10,11,12,13
	 *0 ,1 ,2 ,3 ,4 ,5 ,6  -->>		0 ,1 ,2 ,3 ,4, 5 ,6
	 */
	private int idToPos(int id){
		int height= 0;
		int i= 13;
		while((int)(id/i)>0){
			height++;
			i +=(13-(2*height));
		}
		
		return (int) (id+Math.pow(height,2));
	}
	
	/**
	 * Simple function takes a pos in square array to point in triangle array
	 * opposite of idToPos -> check documentation for idToPos for imagery.
	 * @param pos 0-13*7 
	 * @return returns id 0-49
	 */
	private int posToId(int pos){
		if(pos >0){
			int height = pos/13;		
			return (int) (pos-Math.pow(height, 2));
		}
		return pos;
	}
}
