package checkers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.Timer;

public class Checkers implements ActionListener, MouseListener
{

	public static Checkers checkers;

	private RenderPanel pane;

	private int turn, boardSize = 8;

	private static final int WIDTH = 806, HEIGHT = 833, HUDWIDTH = 200;

	private ArrayList<Piece> pieces;

	private ArrayList<Point> pos;

	private int redRemaining, grayRemaining, moves, winningTeam = -1;

	private Piece selected;

	public Checkers()
	{
		JFrame jframe = new JFrame("Checkers");
		Timer timer = new Timer(20, this);

		pane = new RenderPanel();
		pieces = new ArrayList<Piece>();
		pos = new ArrayList<Point>();

		jframe.setVisible(true);
		jframe.setSize(WIDTH + HUDWIDTH + 4, HEIGHT);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setResizable(false);
		jframe.addMouseListener(this);
		jframe.add(pane);

		start();

		timer.start();
	}

	public void render(Graphics2D g)
	{
		if (winningTeam != -1)
		{
			g.setFont(new Font("Arial", 1, 100));

			if (winningTeam == 0)
			{
				g.setColor(Color.RED);
				g.fillRect(0, 0, WIDTH + HUDWIDTH, HEIGHT);
				g.setColor(Color.black);
				g.drawString("RED WINS", 255, 300);
			}
			else
			{
				g.setColor(Color.lightGray);
				g.fillRect(0, 0, WIDTH + HUDWIDTH, HEIGHT);
				g.setColor(Color.black);
				g.drawString("GRAY WINS", 235, 300);
			}

			g.setFont(new Font("Arial", 1, 25));
			g.drawString("Click to restart", 425, 600);

			return;
		}

		int iterations = 0;
		int width = WIDTH / boardSize;

		g.setStroke(new BasicStroke(4f));

		for (int i = 0; i < boardSize; i++)
		{
			for (int j = 0; j < boardSize; j++)
			{
				if (iterations % 2 == 0)
				{
					g.setColor(Color.red);
				}
				else
				{
					g.setColor(Color.black);
				}

				g.fillRect(HUDWIDTH + i * width, j * width, width, width);

				iterations++;
			}

			iterations++;
		}

		for (Piece piece : pieces)
		{
			if (piece.team == 0)
			{
				g.setColor(Color.red);
			}
			else
			{
				g.setColor(Color.lightGray);
			}

			g.fillOval(HUDWIDTH + 7 + piece.x * width, 7 + piece.y * width, width - 10, width - 10);

			if (piece.crowned)
			{
				g.setColor(Color.yellow);
				g.fillOval(HUDWIDTH + 31 + piece.x * width, 31 + piece.y * width, width / 2 - 9, width / 2 - 9);
			}

			if (piece.targeting)
			{
				g.setColor(Color.orange);
				g.drawOval(HUDWIDTH + 7 + piece.x * width, 7 + piece.y * width, width - 10, width - 10);
			}
		}

		g.setColor(Color.white);

		for (Point point : pos)
		{
			g.drawRect(HUDWIDTH + 4 + point.x * width, 4 + point.y * width, width - 4, width - 4);
		}

		if (selected != null)
		{
			g.drawOval(HUDWIDTH + 7 + selected.x * width, 7 + selected.y * width, width - 10, width - 10);
		}

		g.setColor(Color.gray);

		for (int i = 0; i < boardSize; i++)
		{
			g.drawLine(HUDWIDTH + i * width + 2, 0, HUDWIDTH + i * width + 2, HEIGHT);
			g.drawLine(HUDWIDTH, i * width + 2, HUDWIDTH + WIDTH, i * width + 2);
		}

		g.drawLine(WIDTH + HUDWIDTH - 4, 0, WIDTH + HUDWIDTH - 4, HEIGHT);
		g.drawLine(HUDWIDTH, HEIGHT - 31, WIDTH + HUDWIDTH, HEIGHT - 31);
		g.fillRect(0, 0, HUDWIDTH, HEIGHT);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.black);
		g.drawLine(10, 10, HUDWIDTH - 10, 10);
		g.drawLine(10, 50, HUDWIDTH - 10, 50);
		g.setFont(new Font("Arial", 1, 30));
		g.drawString("TURN", 55, 43);

		if (turn == 0)
		{
			g.setColor(Color.red);
		}
		else
		{
			g.setColor(Color.lightGray);
		}

		g.fillOval(55, 75, 100, 100);
		g.setColor(Color.black);
		g.drawLine(10, 200, HUDWIDTH - 10, 200);
		g.drawLine(10, 325, HUDWIDTH - 10, 325);
		g.drawString("RED: " + redRemaining, 50, 250);
		g.drawString("GRAY: " + grayRemaining, 40, 300);
		g.drawLine(10, 390, HUDWIDTH - 10, 390);
		g.drawString("MOVES: " + moves, 35, 370);
	}

	public static void main(String[] args)
	{
		checkers = new Checkers();
	}

	@Override
	public void actionPerformed(ActionEvent arg0)
	{
		pane.repaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		if (winningTeam != -1)
		{
			start();
		}

		int x = (arg0.getX() - HUDWIDTH) / (WIDTH / boardSize);
		int y = arg0.getY() / (HEIGHT / boardSize);

		if (arg0.getX() > HUDWIDTH && x % 2 != y % 2)
		{
			Piece pieceAtPos = getPieceAt(x, y);

			if (pieceAtPos != null)
			{
				if ((pieceAtPos.targeting || pos.isEmpty()) && pieceAtPos.team == turn)
				{
					int belowAjacent = 0;
					int aboveAjacent = 0;

					for (int i = -1; i < 2; i += 2)
					{
						for (int j = -1; j < 2; j += 2)
						{
							Point point = new Point(x + i, y + j);

							if (point.x < 0 || point.y < 0 || point.x >= boardSize || point.y >= boardSize)
							{
								if (j < 0)
								{
									aboveAjacent++;
								}
								else
								{
									belowAjacent++;
								}
							}

							Piece ajacentPiece = getPieceAt(point.x, point.y);

							if (ajacentPiece != null && ajacentPiece.team == pieceAtPos.team)
							{
								if (j < 0)
								{
									aboveAjacent++;
								}
								else
								{
									belowAjacent++;
								}
							}
							else if (ajacentPiece != null)
							{
								int dx = ajacentPiece.x - pieceAtPos.x;
								int dy = ajacentPiece.y - pieceAtPos.y;
								Point checkSpace = new Point(ajacentPiece.x + dx, ajacentPiece.y + dy);

								if (checkSpace.x < 0 || checkSpace.y < 0 || checkSpace.x >= boardSize || checkSpace.y >= boardSize)
								{
									if (j < 0)
									{
										aboveAjacent++;
									}
									else
									{
										belowAjacent++;
									}
								}
								else if (getPieceAt(checkSpace.x, checkSpace.y) != null)
								{
									if (j < 0)
									{
										aboveAjacent++;
									}
									else
									{
										belowAjacent++;
									}
								}
							}
						}
					}

					if (pieceAtPos.team == 0)
					{
						if (belowAjacent < 2 || (pieceAtPos.crowned && aboveAjacent < 2))
						{
							selected = pieceAtPos;
						}
					}
					else if (pieceAtPos.team == 1)
					{
						if (aboveAjacent < 2 || (pieceAtPos.crowned && belowAjacent < 2))
						{
							selected = pieceAtPos;
						}
					}
				}
			}
			else if (selected != null)
			{
				int dx = x - selected.x;
				int dy = y - selected.y;
				double d = Math.sqrt(dx * dx + dy * dy);

				Piece jumped = getPieceAt(selected.x + dx / 2, selected.y + dy / 2);

				if (pos.isEmpty())
				{
					if (((turn == 0 && dy < 0) || (turn == 1 && dy > 0)) && !selected.crowned)
					{
						return;
					}

					if (d < 1.5d)
					{
						selected.x = x;
						selected.y = y;
						moves++;

						if (y == (boardSize - 1) * ((turn + 1) % 2))
						{
							selected.crowned = true;
						}

						turn = (turn + 1) % 2;
						selected = null;

						calculatePossibleJumps(true);
					}
					else if (d < 3 && jumped != null && jumped.team != selected.team)
					{
						pieces.remove(jumped);

						if (jumped.team == 0)
						{
							redRemaining--;
						}
						else
						{
							grayRemaining--;
						}

						selected.x = x;
						selected.y = y;
						moves++;

						calculatePossibleJumps(false);

						if (y == (boardSize - 1) * ((turn + 1) % 2))
						{
							selected.crowned = true;
							turn = (turn + 1) % 2;
							selected = null;

							return;
						}

						if (pos.isEmpty())
						{
							turn = (turn + 1) % 2;
							selected = null;

							calculatePossibleJumps(true);
						}
					}
				}
				else
				{
					boolean moved = false;

					for (Point point : pos)
					{
						if (point.x == x && point.y == y && d > 1.5 && d < 3)
						{
							moved = true;
						}
					}

					if (moved && jumped.team != selected.team)
					{
						pieces.remove(jumped);

						if (jumped.team == 0)
						{
							redRemaining--;
						}
						else
						{
							grayRemaining--;
						}

						selected.x = x;
						selected.y = y;
						moves++;

						calculatePossibleJumps(false);

						if (y == (boardSize - 1) * ((turn + 1) % 2))
						{
							selected.crowned = true;
							turn = (turn + 1) % 2;
							selected = null;

							return;
						}

						if (pos.isEmpty())
						{
							turn = (turn + 1) % 2;
							selected = null;

							calculatePossibleJumps(true);
						}
					}
				}
			}
		}
	}

	public void calculatePossibleJumps(boolean all)
	{
		ArrayList<Piece> ajacent = new ArrayList<Piece>();

		pos.clear();

		if (grayRemaining <= 0)
		{
			winningTeam = 0;
		}

		if (redRemaining <= 0)
		{
			winningTeam = 1;
		}

		for (Piece piece : pieces)
		{
			piece.targeting = false;
		}

		if (all)
		{
			for (Piece piece : pieces)
			{
				if (piece.team == turn)
				{
					ajacent.clear();

					for (int i = -1; i < 2; i += 2)
					{
						for (int j = -1; j < 2; j += 2)
						{
							Piece jump = getPieceAt(piece.x + i, piece.y + j);

							if (!(jump == null || (((turn == 0 && j < 0) || (turn == 1 && j > 0)) && !piece.crowned)) && jump.team != piece.team)
							{
								ajacent.add(jump);
							}
						}
					}

					for (Piece piece1 : ajacent)
					{
						int dx = piece1.x - piece.x;
						int dy = piece1.y - piece.y;
						Piece jumped = getPieceAt(piece1.x + dx, piece1.y + dy);

						if (piece1.x + dx >= 0 && piece1.y + dy >= 0 && piece1.x + dx < boardSize && piece1.y + dy < boardSize && jumped == null)
						{
							piece.targeting = true;
							pos.add(new Point(piece1.x + dx, piece1.y + dy));
						}
					}
				}
			}
		}
		else
		{
			for (int i = -1; i < 2; i += 2)
			{
				for (int j = -1; j < 2; j += 2)
				{
					Piece jump = getPieceAt(selected.x + i, selected.y + j);

					if (!(jump == null || (((turn == 0 && j < 0) || (turn == 1 && j > 0)) && !selected.crowned)) && jump.team != selected.team)
					{
						ajacent.add(jump);
					}
				}
			}

			for (Piece piece : ajacent)
			{
				int dx = piece.x - selected.x;
				int dy = piece.y - selected.y;
				Piece jumped = getPieceAt(piece.x + dx, piece.y + dy);

				if (piece.x + dx >= 0 && piece.y + dy >= 0 && piece.x + dx < boardSize && piece.y + dy < boardSize && jumped == null)
				{
					pos.add(new Point(piece.x + dx, piece.y + dy));
					piece.targeting = true;
				}
			}
		}
	}

	public void start()
	{
		pieces.clear();
		pos.clear();
		turn = 0;
		moves = 0;
		winningTeam = -1;
		grayRemaining = (boardSize / 2) * 3;
		redRemaining = (boardSize / 2) * 3;

		for (int j = 0; j < boardSize / 2; j++)
		{
			for (int i = 0; i < 3; i++)
			{
				pieces.add(new Piece(j * 2 - (i % 2) + (i == 1 ? 2 : 0), i + boardSize - 3, 1));
				pieces.add(new Piece(j * 2 - (i % 2) + 1, i, 0));
			}
		}
	}

	public Piece getPieceAt(int x, int y)
	{
		for (Piece piece : pieces)
		{
			if (piece.x == x && piece.y == y)
			{
				return piece;
			}
		}

		return null;
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

}
