package game;


public class AI {

	//essentially accessor for the ai move -> minimax algorithm.
	public static int aiMove(int pl, final int[][] positions){
		return miniMax(0,pl,positions,pl, 0, -1000,1000);
	}
	
	/**
	 * Function that calculates the best move for an AI to take.
	 * @param depth On orignal call should be 0, is the depth of recursive function
	 * @param player Current player in depth
	 * @param positions Array of array holding all positions within game
	 * @param origPlayer First player in original call
	 * @param prevHeuristic allows for the calculation of the total of heuristics once depth has been reached
	 * @param alpha value for the cutoff
	 * @param beta value for the cutoff
	 * @return For original call returns BEST move. For inner calls returns BEST value due to move
	 */
	private static int miniMax(int depth, int player, int[][] positions, final int origPlayer, int prevHeuristic, int alpha, int beta){
		final int DEPTH = 5;
		final int WINDOW = 3;
		
		//record of visited nodes
		int[][] posLocal = new int[13][7];
		for(int i =0; i<13; i++){
			for(int j = 0; j<7; j++){
				posLocal[i][j] = positions[i][j];
			}	
		}
		
		//value holds the HIGHEST(MAX) or LOWEST(MIN) value of the move	
		int valMove = (player==origPlayer)?-1000:1000;

		//holds actual move that results in highest or lowest value
		//lowest value not necessary as basic move only required for depth 0
		//where MAX thus only highest move.
		int basicMove = -1;

		
		//DEPTH FIRST SEARCH
		//for all possible moves
		//miniMax(that move in positions, player+1, depth +1)
		for(int x = 0; x < positions.length; x++){
			for(int y = 0; y < positions[x].length; y++){
				//only points that have a move in them
				if(positions[x][y] > -1){
					//create a window that goes around a move window of 3x3
					for(int i =0; i<WINDOW;i++){
						for(int j = 0; j<WINDOW;j++){
							//setting the positions relative to graph
							int xpos = (x-(WINDOW/2))+i;
							int ypos = (y-(WINDOW/2))+j;
							//only if positions is within playable game space
							if(xpos >= 0 && ypos >= 0 && ypos < 7 && xpos < 13){
								int lastmove =  xpos+(ypos*13);
								//point must be free
								if(positions[xpos][ypos]==-1 && posLocal[xpos][ypos]==-1){
									
									//set point as player MAX or MIN
									positions[xpos][ypos] = player;
									posLocal[xpos][ypos] = player;
									
									
									//Heuristic
									int heuristic = Valid.heuristicScore(player, lastmove, positions, (DEPTH-depth)+1);
									//apply min or max modifier
									//move value is the heuristic for the move + the heuristics of all father node
									int move = heuristic*(player==origPlayer?1:-1)+prevHeuristic;
							
									
									
									///////ONLY RECURSE NOT DEPTH REACHED//////////
									if(depth!=DEPTH){																		
										////RECURSIVE CALL//////////
										//calculate recursive minmax
										move = miniMax(depth+1, (player+1)%2, positions, origPlayer,move, alpha, beta);
										///////////////////////////										
									}									
									//alphabeta pruning
									if(player==origPlayer){
										//max of all possible node moves (in min)
										if(move>valMove){
											valMove = move;
											basicMove = lastmove;
										}
										//reset alpha to best move possible for max
										alpha = (alpha>valMove)?alpha:valMove;
									}else{
										//min of all possible node moves (in min)
										if(move<valMove){
											valMove = move;
											basicMove = lastmove;
										}
										//reset beta to best move possible for min
										beta = (beta<valMove)?beta:valMove;
									}
									//cutoff -- if beta is less than alpha cutoff/ end the recursive alg
									if(beta<= alpha){
										positions[xpos][ypos] = -1;	
										return endAlg(valMove, lastmove, depth);
									}
									///////////////////////////////////////////

									//CLOSING POS////////////////////
									//reset position to -1
									positions[xpos][ypos] = -1;	

									//if no winning value found
									if(basicMove == -1){
										basicMove = lastmove;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return endAlg(valMove, basicMove, depth);

	}
	
	/**
	 * Function that works with MINMAX algorithm. Will return the value of nodes if 
	 * we want node value -> depth > 0. Will return MOVE if depth is 0, to ORIGINAL calling
	 * function, in this case aiMove();
	 * @param valMove VALUE of the node with respect to the move played
	 * @param basicMove ACTUAL move to take 
	 * @param depth Depth of the minimax algorithm.
	 * @return Depending on depth returns VALUE of nodes (depth!=0) or ACTUAL move (depth == 0)
	 */
	private static int endAlg(int valMove, int basicMove, int depth){
		if(depth!=0){
			return valMove;//player == origPlayer);
		}else{
			return basicMove;
		}
	}
	
}
