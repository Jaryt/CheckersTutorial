package checkers;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class RenderPanel extends JPanel
{

	@Override
	protected void paintComponent(Graphics arg0)
	{
		super.paintComponent(arg0);

		Checkers.checkers.render((Graphics2D) arg0);
	}

}
