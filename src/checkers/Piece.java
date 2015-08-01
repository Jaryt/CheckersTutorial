package checkers;

public class Piece
{

	public boolean crowned, targeting;
	
	public int x, y, team; 
	
	public Piece(int x, int y, int team)
	{
		this.x = x;
		this.y = y;
		this.team = team;
	}
	
}
