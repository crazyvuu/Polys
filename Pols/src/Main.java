import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Main extends JComponent implements MouseListener {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static JTextArea textArea;
	private int posX = 0;
	private int posY = 0;
	private int clickCount = 0;
	private Polygon poly = new Polygon();
	private ArrayList<int[]> bluePlayer = new ArrayList<int[]>();
	private ArrayList<int[]> redPlayer = new ArrayList<int[]>();
	private ArrayList<int[]> blueAreas = new ArrayList<int[]>();
	private ArrayList<int[]> redAreas = new ArrayList<int[]>();
	private boolean blueHave = false, redHave = false, blueExist = false,
			redExist = false;
	private int[] ba;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new JFrame();
					frame.setBounds(100, 100, 630, 450);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.getContentPane().add(new Main());

					textArea = new JTextArea();
					frame.getContentPane().add(textArea, BorderLayout.SOUTH);
					textArea.setRows(2);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		frame.addMouseListener(this);
	}

	public void paint(Graphics g2) {
		double t = 20;
		double r = t * Math.sqrt(3) / 2;
		int l = 0;
		Graphics2D g = (Graphics2D) g2;
		int[] x = { 20, (int) (30), 20, 40, (int) (30 + t), 40 };
		int[] z = { 20, (int) (30 - t), 20, 40, (int) (30 + t), 40 };
		int[] y = { 20, (int) (20 + r), (int) (20 + 2 * r), (int) (20 + 2 * r),
				(int) (20 + r), 20 };
		for (int j = 0; j < 20; j++) {
			poly.reset();
			if (j % 2 == 0) {
				while (l < 6) {
					x[l] = z[l];
					poly.addPoint(x[l], y[l]);
					l++;
				}
				l = 0;
			} else {
				while (l < 6) {
					x[l] = (int) (z[l] + 1.5 * t);
					poly.addPoint(x[l], y[l]);
					l++;
				}
				l = 0;
			}
			for (int i = 0; i < 10; i++) {
				checkArrays(x, y);
				if (blueHave) {
					g.setColor(Color.BLUE);
					g.fillPolygon(poly);
					blueHave = false;
				} else {
					if (redHave) {
						g.setColor(Color.RED);
						g.fillPolygon(poly);
						redHave = false;
					} else {
						if (poly.contains(posX, posY)) {
							if (clickCount % 2 == 0) {
								g.setColor(Color.RED);
								redPlayer.add(x.clone());
								redPlayer.add(y.clone());
							} else {
								g.setColor(Color.BLUE);
								bluePlayer.add(x.clone());
								bluePlayer.add(y.clone());
							}
							g.fillPolygon(poly);
						} else
							g.drawPolygon(poly);
					}
				}
				g.setColor(Color.black);
				poly.reset();
				while (l < 6) {
					x[l] += 3 * t;
					poly.addPoint(x[l], y[l]);
					l++;
				}
				l = 0;
			}
			while (l < 6) {
				y[l] += r;
				l++;
			}
			l = 0;
		}
		checkArreas();
		if (!blueAreas.isEmpty()) {
			for (int i = 0; i < blueAreas.size(); i++) {
				g.setColor(Color.green);
				g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.drawLine(blueAreas.get(i)[0], blueAreas.get(i)[1],
						blueAreas.get(i)[2], blueAreas.get(i)[3]);
			}
		}
		if (!redAreas.isEmpty()) {
			for (int i = 0; i < redAreas.size(); i++) {
				g.setColor(Color.yellow);
				g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				g.drawLine(redAreas.get(i)[0], redAreas.get(i)[1],
						redAreas.get(i)[2], redAreas.get(i)[3]);
			}
		}
	}

	public void checkArrays(int[] x, int[] y) {
		for (int a = 0, b = 1; a < bluePlayer.size(); b += 2, a += 2) {
			if (Arrays.equals(x, bluePlayer.get(a))
					&& Arrays.equals(y, bluePlayer.get(b)))
				blueHave = true;
		}
		for (int a = 0, b = 1; a < redPlayer.size(); b += 2, a += 2) {
			if (Arrays.equals(x, redPlayer.get(a))
					&& Arrays.equals(y, redPlayer.get(b)))
				redHave = true;
		}
	}

	public void checkArreas() {
		int l = 0, sumXd = 0, sumYe = 0, sumXa = 0, sumYb = 0;
		for (int d = 0, e = 1; d < bluePlayer.size(); d += 2, e += 2) {
			for (int a = 0, b = 1; a < bluePlayer.size(); b += 2, a += 2) {
				if ((bluePlayer.get(d)[0] == bluePlayer.get(a)[4]
						&& bluePlayer.get(d)[1] == bluePlayer.get(a)[3]
						&& bluePlayer.get(e)[0] == bluePlayer.get(b)[4] && bluePlayer
						.get(e)[1] == bluePlayer.get(b)[3])
						|| (bluePlayer.get(d)[1] == bluePlayer.get(a)[5]
								&& bluePlayer.get(d)[2] == bluePlayer.get(a)[4]
								&& bluePlayer.get(e)[1] == bluePlayer.get(b)[5] && bluePlayer
								.get(e)[2] == bluePlayer.get(b)[4])
						|| (bluePlayer.get(d)[2] == bluePlayer.get(a)[0]
								&& bluePlayer.get(d)[3] == bluePlayer.get(a)[5]
								&& bluePlayer.get(e)[0] == bluePlayer.get(b)[0] && bluePlayer
								.get(e)[1] == bluePlayer.get(b)[5])
						|| (bluePlayer.get(d)[3] == bluePlayer.get(a)[1]
								&& bluePlayer.get(d)[4] == bluePlayer.get(a)[0]
								&& bluePlayer.get(e)[3] == bluePlayer.get(b)[1] && bluePlayer
								.get(e)[4] == bluePlayer.get(b)[0])
						|| (bluePlayer.get(d)[4] == bluePlayer.get(a)[2]
								&& bluePlayer.get(d)[5] == bluePlayer.get(a)[1]
								&& bluePlayer.get(e)[4] == bluePlayer.get(b)[2] && bluePlayer
								.get(e)[5] == bluePlayer.get(b)[1])
						|| (bluePlayer.get(d)[5] == bluePlayer.get(a)[3]
								&& bluePlayer.get(d)[0] == bluePlayer.get(a)[2]
								&& bluePlayer.get(e)[5] == bluePlayer.get(b)[3] && bluePlayer
								.get(e)[0] == bluePlayer.get(b)[2])) {
					while (l < 6) {
						sumXd += bluePlayer.get(d)[l];
						sumYe += bluePlayer.get(e)[l];
						sumXa += bluePlayer.get(a)[l];
						sumYb += bluePlayer.get(b)[l];
						l++;
					}
					ba = new int[] { sumXa / 6, sumYb / 6, sumXd / 6, sumYe / 6 };
					for (int i = 0; i < blueAreas.size(); i++) {
						if (Arrays.equals(blueAreas.get(i), ba))
							blueExist = true;
					}
					if (!blueExist)
						blueAreas.add(ba);
					else
						blueExist = false;
					l = 0;
					sumXd = 0;
					sumXa = 0;
					sumYe = 0;
					sumYb = 0;
				}
			}
		}
		for (int d = 0, e = 1; d < redPlayer.size(); d += 2, e += 2) {
			for (int a = 0, b = 1; a < redPlayer.size(); b += 2, a += 2) {
				if ((redPlayer.get(d)[0] == redPlayer.get(a)[4]
						&& redPlayer.get(d)[1] == redPlayer.get(a)[3]
						&& redPlayer.get(e)[0] == redPlayer.get(b)[4] && redPlayer
						.get(e)[1] == redPlayer.get(b)[3])
						|| (redPlayer.get(d)[1] == redPlayer.get(a)[5]
								&& redPlayer.get(d)[2] == redPlayer.get(a)[4]
								&& redPlayer.get(e)[1] == redPlayer.get(b)[5] && redPlayer
								.get(e)[2] == redPlayer.get(b)[4])
						|| (redPlayer.get(d)[2] == redPlayer.get(a)[0]
								&& redPlayer.get(d)[3] == redPlayer.get(a)[5]
								&& redPlayer.get(e)[0] == redPlayer.get(b)[0] && redPlayer
								.get(e)[1] == redPlayer.get(b)[5])
						|| (redPlayer.get(d)[3] == redPlayer.get(a)[1]
								&& redPlayer.get(d)[4] == redPlayer.get(a)[0]
								&& redPlayer.get(e)[3] == redPlayer.get(b)[1] && redPlayer
								.get(e)[4] == redPlayer.get(b)[0])
						|| (redPlayer.get(d)[4] == redPlayer.get(a)[2]
								&& redPlayer.get(d)[5] == redPlayer.get(a)[1]
								&& redPlayer.get(e)[4] == redPlayer.get(b)[2] && redPlayer
								.get(e)[5] == redPlayer.get(b)[1])
						|| (redPlayer.get(d)[5] == redPlayer.get(a)[3]
								&& redPlayer.get(d)[0] == redPlayer.get(a)[2]
								&& redPlayer.get(e)[5] == redPlayer.get(b)[3] && redPlayer
								.get(e)[0] == redPlayer.get(b)[2])) {
					while (l < 6) {
						sumXd += redPlayer.get(d)[l];
						sumYe += redPlayer.get(e)[l];
						sumXa += redPlayer.get(a)[l];
						sumYb += redPlayer.get(b)[l];
						l++;
					}
					ba = new int[] { sumXa / 6, sumYb / 6, sumXd / 6, sumYe / 6 };
					for (int i = 0; i < redAreas.size(); i++) {
						if (Arrays.equals(redAreas.get(i), ba))
							redExist = true;
					}
					if (!redExist)
						redAreas.add(ba);
					else
						redExist = false;
					l = 0;
					sumXd = 0;
					sumXa = 0;
					sumYe = 0;
					sumYb = 0;
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		textArea.setText("clicked");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		textArea.setText("pressed");
		posX = e.getX();
		posY = e.getY() - 20;
		clickCount++;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		textArea.setText("released");
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		textArea.setText("entered");
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		textArea.setText("exited");
	}
}

/*
 * 
 * public static int[] removeElements(int[] input, int deleteMe) { int[] result
 * = new int[6]; int i = 0; for (int item : input) { if (deleteMe != item) {
 * result[i] = item; i++; } } return result; } StringBuffer resX = new
 * StringBuffer(); StringBuffer resY = new StringBuffer(); for (int ii = 0; ii <
 * x.length; ii++) { if (ii != 5) resX.append(x[ii] + ", "); else
 * resX.append(x[ii]); } for (int ii = 0; ii < y.length; ii++) { if (ii != 5)
 * resY.append(y[ii] + ", "); else resY.append(y[ii]); }
 * textArea.setText(resX.toString() + "   posX - " + posX + "\n" +
 * resY.toString() + "   posY - " + posY); if (poly.xpoints.length > 6)
 * poly.xpoints = removeElements(poly.xpoints, poly.xpoints[7]); if
 * (poly.ypoints.length > 6) poly.ypoints = removeElements(poly.ypoints,
 * poly.ypoints[7]);
 */

/*
 * 
 * for (int c = 0; c < bluePlayer.get(a).length; c++) { for (int f = 0; f <
 * bluePlayer.get(a).length; f++) { if (c != bluePlayer.get(a).length - 1 && f
 * != bluePlayer.get(a).length - 1 && f != 0) { if (a != d && b != e) { if
 * ((bluePlayer.get(d)[c] == bluePlayer.get(a)[f] || bluePlayer .get(d)[c] ==
 * bluePlayer.get(a)[f - 1]) && (bluePlayer.get(e)[c] == bluePlayer .get(b)[f]
 * || bluePlayer.get(e)[c + 1] == bluePlayer .get(b)[f - 1] || ((bluePlayer
 * .get(b)[0] == bluePlayer.get(e)[2]) && (bluePlayer .get(b)[5] ==
 * bluePlayer.get(e)[3])))) { if ((bluePlayer.get(d)[c + 1] == bluePlayer
 * .get(a)[f + 1] || bluePlayer.get(d)[c + 1] == bluePlayer .get(a)[f]) &&
 * (bluePlayer.get(e)[c + 1] == bluePlayer .get(b)[f + 1] || bluePlayer.get(e)[c
 * + 1] == bluePlayer .get(b)[f - 1] || (bluePlayer .get(b)[0] == bluePlayer
 * .get(e)[2]) && (bluePlayer.get(b)[5] == bluePlayer .get(e)[3]))) { while (l <
 * 6) { sumXd += bluePlayer.get(d)[l]; sumYe += bluePlayer.get(e)[l]; sumXa +=
 * bluePlayer.get(a)[l]; sumYb += bluePlayer.get(b)[l]; l++; } ba = new int[] {
 * sumXa / 6, sumYb / 6, sumXd / 6, sumYe / 6 }; for (int i = 0; i <
 * blueAreas.size(); i++) { if (Arrays.equals(blueAreas.get(i), ba)) blueExist =
 * true; } if (!blueExist) blueAreas.add(ba); else blueExist = false; l = 0;
 * sumXd = 0; sumXa = 0; sumYe = 0; sumYb = 0; } } } } } }
 */

