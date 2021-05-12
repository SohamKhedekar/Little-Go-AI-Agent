import java.util.*;
import java.io.*;

class my_player {
	static class GODetails {
		int c;
		int[][] prev = new int[5][5];
		int[][] curr = new int[5][5];
		int[] out = new int[2];
		double h = 0;
		int d = 3;
		boolean alternateHeuristic = false;
	}

	static boolean isValidMove(int[][] curr, int[][] prev, int c, int i, int j) {
		if (i < 0 || i >= curr.length || j < 0 || j >= curr[0].length || curr[i][j] != 0)
			return false;
		//liberty check
		if (libertyCheck(curr, prev, i, j, c))
			return true;
		//post-liberty death check
		int[][] new_curr = new int[curr.length][];
		for(int k = 0; k < curr.length; k++)
			new_curr[k] = curr[k].clone();
		new_curr[i][j] = c;
		boolean deathFlag = deathCheck(new_curr, prev, c);
		
		//liberty check again
		if (!libertyCheck(new_curr, prev, i, j, c))
			return false;
		else {
			if (deathFlag && compareBoards(new_curr, prev))
				return false;
		}
		return true;
	}

	static boolean libertyCheck(int[][] curr, int[][] prev, int i, int j, int type) {
		int[][] visited = new int[5][5];
		visited[i][j] = type;
		if(dfs(curr, prev, i+1, j, visited, type) || dfs(curr, prev, i-1, j, visited, type) || dfs(curr, prev, i, j+1, visited, type) || dfs(curr, prev, i, j-1, visited, type)) {
			return true;
		}
		return false;
	}

	static boolean dfs(int[][] curr, int[][] prev, int i, int j, int[][] visited, int type) {
		if (i < 0 || i >= curr.length || j < 0 || j >= curr[0].length || visited[i][j] == type || (curr[i][j] != type && curr[i][j] != 0))
			return false;
		if (curr[i][j] == 0)
			return true;
		visited[i][j] = type;
		return dfs(curr, prev, i+1, j, visited, type) || dfs(curr, prev, i-1, j, visited, type) || dfs(curr, prev, i, j+1, visited, type) || dfs(curr, prev, i, j-1, visited, type);
	}

	static boolean deathCheck(int[][] curr, int[][] prev, int c) {
		boolean deathFlag = false;
		List<List<Integer>> deathList = new ArrayList<>();
		for (int k = 0; k < curr.length; k++) {
			for (int l = 0; l < curr[0].length; l++) {
				if (curr[k][l] == 3 - c) {
					if (!libertyCheck(curr, prev, k, l, 3 - c)) {
						List<Integer> temp = new ArrayList<>();
						temp.add(k);
						temp.add(l);
						deathList.add(temp);	
						deathFlag = true;
					}
				}
			}
		}
		for (List<Integer> temp : deathList) {
			curr[temp.get(0)][temp.get(1)] = 0;
		}
		return deathFlag;
	}

	static boolean compareBoards(int[][] curr, int[][] prev) {
		for (int i = 0; i < curr.length; i++) {
			for (int j = 0; j < curr[0].length; j++) {
				if (curr[i][j] != prev[i][j])
					return false;
			}
		}
		return true;
	}

	public static void main (String args[]) {
		File file = new File("input.txt");
		GODetails go = new GODetails();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			go.c = Integer.parseInt(br.readLine());
			int i = 0;
			while (i < go.prev.length) {
				String row = br.readLine();
				go.prev[i][0] = Character.getNumericValue(row.charAt(0));
				go.prev[i][1] = Character.getNumericValue(row.charAt(1));
				go.prev[i][2] = Character.getNumericValue(row.charAt(2));
				go.prev[i][3] = Character.getNumericValue(row.charAt(3));
				go.prev[i][4] = Character.getNumericValue(row.charAt(4));
				i++;
			}
			i = 0;
			while (i < go.curr.length) {
				String row = br.readLine();
				go.curr[i][0] = Character.getNumericValue(row.charAt(0));
				go.curr[i][1] = Character.getNumericValue(row.charAt(1));
				go.curr[i][2] = Character.getNumericValue(row.charAt(2));
				go.curr[i][3] = Character.getNumericValue(row.charAt(3));
				go.curr[i][4] = Character.getNumericValue(row.charAt(4));
				i++;
			}
			//go.h = random_player(go);
			go.h = minimax_player(go, go.curr, go.prev, go.c, go.d, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
			getOutput(go);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void getOutput (GODetails go) {
		File file = new File("output.txt");
		try {
			PrintWriter writer = new PrintWriter("output.txt");
			if (go.h != -10000) {
				writer.print(go.out[0]);
				writer.print(",");
				writer.print(go.out[1]);
			}
			else {
				writer.println("PASS");
			}
			writer.close();
			file = new File("helper.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));
			int count = Integer.parseInt(br.readLine());
			if (go.c == 2 && count == 0)
				count++;
			writer = new PrintWriter("helper.txt");
			if (count+2 < 24) {
				writer.print((count+2));
			}
			else {
				writer.print("0");
			}
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	static double random_player(GODetails go) {
		List<List<Integer>> possible_placements = new ArrayList<>();
		for (int i = 0; i < go.curr.length; i++) {
			for (int j = 0; j < go.curr[0].length; j++) {
				if (isValidMove(go.curr, go.prev, go.c, i, j)) {
					List<Integer> temp = new ArrayList<>();
					temp.add(i);
					temp.add(j);
					possible_placements.add(temp);
				}
			}
		}
		if (possible_placements.size() != 0) {
			Random rand = new Random();
			int index = rand.nextInt(possible_placements.size());
			go.out[0] = possible_placements.get(index).get(0);
			go.out[1] = possible_placements.get(index).get(1);
			return 1;
		}
		return -10000;
	}

	static void dfsForLiberties(int[][] curr, int i, int j, int[][] visited, int type) {
		if (i < 0 || i >= curr.length || j < 0 || j >= curr[0].length || visited[i][j] == type || curr[i][j] != type)
			return;
		visited[i][j] = type;
		dfsForLiberties(curr, i+1, j, visited, type);
		dfsForLiberties(curr, i-1, j, visited, type);
		dfsForLiberties(curr, i, j+1, visited, type);
		dfsForLiberties(curr, i, j-1, visited, type);
	}

	static double countLiberties (int[][] curr, int c, int k, int l) {
		double count = 0;
		int[][] visited = new int[5][5];
		dfsForLiberties(curr, k, l, visited, c);
		for (int i = 0; i < curr.length; i++) {
			for (int j = 0; j < curr[0].length; j++) {
				if (visited[i][j] == c) {
					if ((i-1 >= 0 && curr[i-1][j] == 0 && visited[i-1][j] != -1)) {
						count++;
						visited[i-1][j] = -1;
					}
					if ((i+1 < curr.length && curr[i+1][j] == 0 && visited[i+1][j] != -1)) {
						count++;
						visited[i+1][j] = -1;
					}
					if ((j-1 >= 0 && curr[i][j-1] == 0 && visited[i][j-1] != -1)) {
						count++;
						visited[i][j-1] = -1;
					}
					if ((j+1 < curr[0].length && curr[i][j+1] == 0 && visited[i][j+1] != -1)) {
						count++;
						visited[i][j+1] = -1;
					}
				}
			}
		}
		return count;
	}

	// static double getEulerNumber(int[][] curr, int i, int j, int c) {
	// 	int count = 0;
	// 	if (curr[i][j] != c) count++;
	// 	if (curr[i][j+1] != c) count++;
	// 	if (curr[i+1][j] != c) count++;
	// 	if (curr[i+1][j+1] != c) count++;
	// 	if (count == 3)
	// 		return 1;
	// 	if (count == 1)
	// 		return -1;
	// 	if (count == 2 && ((curr[i][j] == c && curr[i+1][j+1] == c) || (curr[i][j+1] == c && curr[i+1][j] == c)))
	// 		return 2;
	// 	return 0;
	// }

	static double minimax_player(GODetails go, int[][] curr, int[][] prev, int c, int depth, boolean isMax, double alpha, double beta) {
		if (depth == 0) {
			double heuristicValue = 0;
			double libertyCount = 0;
			//double edgeCount = 0;
			//double eulerNumber = 0;
			if (go.c == 2) 
				heuristicValue = 2.5;
			else 
				heuristicValue = -2.5;
			for (int i = 0; i < curr.length; i++) {
				for (int j = 0; j < curr[0].length; j++) {
					if (curr[i][j] == go.c) {
						heuristicValue++;
						// if (i == 0 || i == curr.length-1 || j == 0 || j == curr[0].length-1)
						// 	edgeCount--;
					}
					else if (curr[i][j] == 3-go.c) {
						heuristicValue--;
						// if (i == 0 || i == curr.length-1 || j == 0 || j == curr[0].length-1)
						// 	edgeCount++;
					}
					else if (!go.alternateHeuristic) {
						curr[i][j] = go.c;
						libertyCount += countLiberties(curr, go.c, i, j);
						curr[i][j] = 3-go.c;
						libertyCount -= countLiberties(curr, 3-go.c, i, j);
						curr[i][j] = 0;
						// if ((i-1 >= 0 && curr[i-1][j] == go.c) || (i+1 < curr.length && curr[i+1][j] == go.c) || (j-1 >= 0 && curr[i][j-1] == go.c) || (j+1 < curr.length && curr[i][j+1] == go.c))
						// 	libertyCount++;
						// if ((i-1 >= 0 && curr[i-1][j] == 3-go.c) || (i+1 < curr.length && curr[i+1][j] == 3-go.c) || (j-1 >= 0 && curr[i][j-1] == 3-go.c) || (j+1 < curr.length && curr[i][j+1] == 3-go.c))
						// 	libertyCount--;
					}
					// if (!go.alternateHeuristic && i < curr.length-1 && j < curr.length-1) {
					// 	eulerNumber += getEulerNumber(curr, i, j, go.c);
					// }
				}
			}
			if (!go.alternateHeuristic) {
                heuristicValue *= 5; 
                //heuristicValue += libertyCount + edgeCount - eulerNumber;
                heuristicValue += libertyCount;
			}
			return heuristicValue;
		}
		int count = 0;
		if (depth == go.d) {
			try {
				File file = new File("helper.txt");
				BufferedReader br = new BufferedReader(new FileReader(file));
				count = Integer.parseInt(br.readLine());
				if (count == 0 && go.c == 2)
					count++;
				if ((go.c == 1 && count == 0) || (go.c == 2 && count == 1 && curr[2][2] == 0)) {
					go.out[0] = 2;
					go.out[1] = 2;
					return 1;
				}
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<List<Integer>> possible_placements = new ArrayList<>();
		for (int i = 0; i < curr.length; i++) {
			for (int j = 0; j < curr[0].length; j++) {
				if (curr[i][j] == 0) {
					//clone curr and prev
					int[][] new_curr = new int[curr.length][];
					for(int k = 0; k < curr.length; k++)
					    new_curr[k] = curr[k].clone();
					int[][] new_prev = new int[prev.length][];
					for(int k = 0; k < prev.length; k++)
					    new_prev[k] = prev[k].clone();
					//check validity
					if (isValidMove(new_curr, new_prev, c, i, j)) {
						List<Integer> temp = new ArrayList<>();
						temp.add(i);
						temp.add(j);
						possible_placements.add(temp);
					}
				}
			}
		}
		if (possible_placements.size() == 0)
			return -10000;
		double max = Integer.MIN_VALUE;
		double min = Integer.MAX_VALUE;
		List<List<Integer>> best_placements = new ArrayList<>();
		List<List<Integer>> best_placements_middle = new ArrayList<>();
		for (List<Integer> temp : possible_placements) {
			//clone curr
			int [][] next_curr = new int[curr.length][];
			for(int i = 0; i < curr.length; i++)
			    next_curr[i] = curr[i].clone();
			next_curr[temp.get(0)][temp.get(1)] = c;
			boolean deathFlag = deathCheck(next_curr, prev, c);
			double val = 0;
			if (depth == go.d) {
				int nextDepth = (count + go.d < 24)? depth-1:23-count;
				if (count + go.d >= 24) go.alternateHeuristic = true;
				val = minimax_player(go, next_curr, curr, 3-c, nextDepth, !isMax, alpha, beta);
			}
			else
				val = minimax_player(go, next_curr, curr, 3-c, depth-1, !isMax, alpha, beta);
			if (isMax) {
				if (val > max) {
					max = val;
					if (depth == go.d) {
						best_placements = new ArrayList<>();
						best_placements_middle = new ArrayList<>();
						best_placements.add(temp);
						if (temp.get(0) != 0 && temp.get(0) != curr.length-1 && temp.get(1) != 0 && temp.get(1) != curr[0].length-1)
							best_placements_middle.add(temp);
					}
					alpha = Math.max(alpha, max);
				}
				else if (val == max && depth == go.d) {
					best_placements.add(temp);
					if (temp.get(0) != 0 && temp.get(0) != curr.length-1 && temp.get(1) != 0 && temp.get(1) != curr[0].length-1)
						best_placements_middle.add(temp);
				}
				if (alpha >= beta) {
					break;
				} 
			}
			else {
				if (val < min) {
					min = val;
					beta = Math.min(beta, min);
				}
				if (alpha >= beta) {
					break;
				} 
			}
		}
		if (depth == go.d) {
			Random rand = new Random();
			int index = 0;
			if (best_placements_middle.size() == 0) {
				if (best_placements.size() > 1)
					index = rand.nextInt(best_placements.size());
				go.out[0] = best_placements.get(index).get(0);
				go.out[1] = best_placements.get(index).get(1);
			}
			else {
				if (best_placements_middle.size() > 1)
					index = rand.nextInt(best_placements_middle.size());
				go.out[0] = best_placements_middle.get(index).get(0);
				go.out[1] = best_placements_middle.get(index).get(1);
			}
		}
		return (isMax)?max:min;
	}
}


