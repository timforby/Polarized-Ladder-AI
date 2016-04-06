package game;


//"static" class that just verifies parts of the game
public class Valid {

	
	/**
	 * function that goes through the last move and checks if that player is a winner
	 * @return returns the player id that won, -1 if noone wins -2 if draw
	 */
	public static int winner(int lastmove,final int[][] positions){
		final int pos = lastmove;
		final int posx = pos%13;
		final int posy = pos/13;
		final int pl = positions[posx][posy];
		final int otherpl = (pl+1)%2;
		
		//calculating scores
		int[][] scores = new int[2][4];	
		scores = countScore(posx,posy,pl,positions);
		
		//alternate positions for calculations
		int cposx = posx;
		int cposy = posy;
				
		//go through all double L with score greater than 3;
		//for all directions
		for(int i = 0; i < 4; i++){			
			//1 in scores: vertical points away from lastmove
			int pointVert = scores[1][i];
			//0 in scores: horizontal points away from lastmove
			int pointHorz = scores[0][3-i];
			
			//score greater than 3
			if (pointVert+pointHorz > 3){
				
				//if total points is odd do not count score
				if((pointVert+pointHorz)%2!=0){
					continue;
				}

				//find direction to move x value left or right
				int dirx = (i == 3 || i == 0)?-1:1;
				
				//if current point is at concave part set to convex part
				//this is to simplify the search process
				if(pointVert%2==1){
					cposx += dirx;
					int temp;
					temp = pointHorz-1;
					pointHorz = pointVert+1;
					pointVert = temp;
				}
				int tempx = cposx;
				int tempy = cposy;
							
				//for direction up and down
				for(int j = 0; j < 2; j++){	
					//j==0 going down, j==1 going up
					int diry = j==0?-1:1;
									
					//--o       o--
					//  |   or  |
					// vert is always down horz is up from current point when convex
					//if down pointvert if up pointhorz since convex
					int point = j==0?pointVert:pointHorz;
					int pointOther = j==1?pointVert:pointHorz;
					
					cposx = tempx;
					cposy = tempy;
						
					while(point>0){
						
						//make sure not edge point so pointother must not be 0
						if(pointOther!=0 && checknullify(cposx,cposy,otherpl,i,positions)){
							return pl;
						}
						//increase or decrease x and y
						cposy += diry;
						cposx += dirx*(diry*-1);
						
						//increase or decrease points as we move along L
						point -=2;
						pointOther += 2;
					}
				}				
			}
		}
		
		int draw = draw(positions);
		
		return draw;

	}
	
	/**
	 * Function that returns the heuristic value based on a last move
	 * @param player the player that played the last move
	 * @param lastmove last move played
	 * @param positions current situation of the board
	 * @param depth depth to give a weight to a win if there is one
	 * @return returns a value based on the number of adjacent points relative to the last move
	 */
	public static int heuristicScore(int player, int lastmove, final int[][] positions, int depth){
		
		int winnerResult = -1;
		//get heuristic count for move
		int h = heuristicScoreCount(lastmove, player, positions);
		if(h>3){//chance that there is winner
			winnerResult = winner(lastmove, positions);
			// ^^^ in order to avoid always checking if winner
		}	
		if(winnerResult ==player){	//this plyer wins4
			//by multiplying by depth allows AI to determine Urgerncy
			return 100*depth;
		}else if(winnerResult == (player+1)%2){//OTHER PLAYER WINS- should never happen as we are basing of last move
			return -100*depth;//WILL NEVER HAPPEN
			
		}else if(winnerResult == -1){//NO WIN			
			return h*h;//h squared for more impact relative to win
		}else{//DRAW
			return 0;
		}
		
	}
	
	
	/**
	 * Function that calculates if there is a draw
	 * @param positions
	 * @return -1 if no draw, -2 if draw
	 */
	private static int draw(final int[][]positions){
		for(int x =0; x < positions.length; x++){
			for(int y =0; y < positions[x].length; y++){
				if(positions[x][y]==-1){
					return -1;
				}
			}
		}
		return -2;
	}
	
	/**
	 * Simple heuristic that returns a value based on the number of points that will be adjacent on a specific move
	 * @param lastmove last move that was played by the player pl
	 * @param pl player to play last move
	 * @param positions board after said move
	 * @return returns a simple heuristic based on the number of adjacent points
	 */
	private static int heuristicScoreCount(int lastmove, int pl, final int[][] positions){
		final int posx = lastmove%13;
		final int posy = lastmove/13;
		
		int maxScore=0;
		for(int i = 0; i<4; i++){
			//for each different direction (2 in total but 4 due to which direction next point could be (vertical or horziz)
			//adds the opposite directions together simplify
			int score = Math.max(checkAdjacency(posx,posy,pl,i,true,positions,false),0)+Math.max(checkAdjacency(posx,posy,pl,3-i,false,positions,false),0);
			maxScore = (maxScore<score)?score:maxScore;
		}
		return maxScore;
	}
	
	/**
	 * Function that counts the scores of adjacent points
	 * @param posx takes in x value of point
	 * @param posy takes in y value of point
	 * @param pl takes in current player to verify adjacent points are of the player
	 * @param positions contains board with player moves
	 * @return returns a 2dimensional array with int[0][...] the points horizontally adjacent and int[1][...] with points vertically adjacent
	 */
	private static int[][] countScore(int posx, int posy, int pl, final int[][] positions){
		int[][] scores = new int[2][4];
		
		for(int i = 0; i<4; i++){
			//1-d array pos is vertical previous/next vertical(false) or horiz(true)
			//2-d array pos is previous/next pos. 0-downleft(next) 1-downright(next), 2upleft(prev) 2upright(prev)
			scores[0][i] = Math.max(checkAdjacency(posx,posy,pl,i,true,positions,true),0);
			scores[1][i] = Math.max(checkAdjacency(posx,posy,pl,i,false,positions,true),0);
		}
		return scores;
	}
	
	/**
	 * Function that checks if the current part in convex of L is nullified
	 * @param cposx takes in posx within the L -> should not be an edge point of the L
	 * @param cposy takes in posy within the L -> should not be an edge point of the L
	 * @param otherpl other player to check nullification
	 * @param direction direction of the L (0-3) (0 and 3 are same, 1 and 2 are same)
	 * @return returns true if NOT nullified by other player
	 */
	private static boolean checknullify(int cposx, int cposy, int otherpl, int direction, final int[][] positions){
		/*
		 *dir
		 *
		 *2<  >3
		 *^    ^
		 *v    v
		 *0<  >1 
		 */
		 
		try{
		
			if (cposy == 1)return true;
			
			//1 or 2 means top left to bottom right
			if(direction == 1 || direction == 2){
				//check if points on side of ladder are other players to nullify
				if(positions[cposx+1][cposy+1] != otherpl || positions[cposx-1][cposy-1] != otherpl){
					return true;
				}
			//3 or 0 thus top right to bottom left
			}else{
				if(positions[cposx-1][cposy+1] != otherpl || positions[cposx+1][cposy-1] != otherpl){
					return true;
				}
			}
		}catch(IndexOutOfBoundsException e){
		}
		return false;
		
	}
	

	/**
	 * Recursive function that checks for adjacent points that satisfy game rules
	 * @param posx X position to check from
	 * @param posy Y poistion to check from
	 * @param pl current player
	 * @param dir Direction must be 0-3 as defined below
	 * @param horz whether to check horizontally(true) or vertically(false)
	 * @param remove the score of a badly adjacent point
	 * @return returns the score for the amount of adjacent points satisfying game conditions
	 *dir
	 *
	 *2<  >3
	 *^    ^
	 *v    v
	 *0<  >1 
	 */
	private static int checkAdjacency(int posx, int posy, int pl,final int dir, boolean horz, final int[][] positions, boolean removeBadAdjacency){
		
		//based on direction AND horz(bool) pick whether to go <horizontally and left or right> or 
		//<vertically and left or right>
		
		switch(dir){
		case 0:
			posx -= horz?1:0;
			posy -= horz?0:1;
			break;
		case 1:
			posx += horz?1:0;
			posy -= horz?0:1;
			break;
		case 2:
			posx -= horz?1:0;
			posy += horz?0:1;
			break;
		case 3:
			posx += horz?1:0;
			posy += horz?0:1;
			break;
		default:break;
		}
		int plpoint =-1;
		try{
			plpoint = positions[posx][posy];
		}catch(ArrayIndexOutOfBoundsException e){
			//if point is out of bounds
			return 0;
		}
		//if point is players point then good!
		if(plpoint== pl){
			return 1+checkAdjacency(posx,posy,pl, dir, !horz, positions, removeBadAdjacency);
		}else{
			int val = 0;
			
			//VERY IMPORTANT STEP!!
			//will return -1 to REMOVE an unnescessary count of point
			//on double L that does not contribute
			/*
			 * o o				x o
			 *   o o    --->>	  o o
			 *     o o			    o o
			 */
			if(removeBadAdjacency && ((dir>1 && !horz) ||(dir<2 && horz))){
				val = -1;
			}
			
			
			return val;
		}
	}
	
}
