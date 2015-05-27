import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class Main extends JComponent implements MouseListener {
	private static final long serialVersionUID = 1L;
	private static JFrame frame;
	private static JTextArea textArea;
	private int posX = 0;
	private int posY = 0;
	private Polygon poly = new Polygon();
	private ArrayList<double[]> bluePlayer = new ArrayList<double[]>();
	private ArrayList<double[]> redPlayer = new ArrayList<double[]>();
	private ArrayList<Point> blueAreas = new ArrayList<Point>();
	private ArrayList<Point> redAreas = new ArrayList<Point>();
	private ArrayList<Point> currentArea = new ArrayList<Point>();
	// private ArrayList<int[]> blueTakenAreas = new ArrayList<int[]>();
	// private ArrayList<int[]> redTakenAreas = new ArrayList<int[]>();
	private ArrayList<Integer> pBluePos = new ArrayList<Integer>();
	private ArrayList<Integer> pRedPos = new ArrayList<Integer>();
	private ArrayList<ArrayList<Integer>> xBlue = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> yBlue = new ArrayList<ArrayList<Integer>>();
	private boolean blueHave = false, redHave = false;
	int t = 20;
	double r = t * Math.sqrt(3) / 2;
	private Point ba;
	int firstPosition = 0;

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

	// Строение поля с текущим положением игроков
	public void paint(Graphics g2) {
		int l = 0, posCount = 0;
		Graphics2D g = (Graphics2D) g2;
		double[] x = { t, 1.5 * t, t, 2 * t, 1.5 * t + t, 2 * t };
		double[] z = { t, 1.5 * t - t, t, 2 * t, 1.5 * t + t, 2 * t };
		double[] y = { t, t + r, t + 2 * r, t + 2 * r, t + r, t };
		for (int j = 0; j < 20; j++) {
			poly.reset();
			if (j % 2 == 0) {
				while (l < 6) {
					x[l] = z[l];
					poly.addPoint((int) x[l], (int) y[l]);
					l++;
				}
				l = 0;
			} else {
				while (l < 6) {
					x[l] = (int) (z[l] + 1.5 * t);
					poly.addPoint((int) x[l], (int) y[l]);
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
							if (redPlayer.size() < bluePlayer.size()) {
								g.setColor(Color.RED);
								redPlayer.add(x.clone());
								redPlayer.add(y.clone());
								pRedPos.add(posCount);
							} else {
								g.setColor(Color.BLUE);
								bluePlayer.add(x.clone());
								bluePlayer.add(y.clone());
								pBluePos.add(posCount);
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
					poly.addPoint((int) x[l], (int) y[l]);
					l++;
				}
				l = 0;
				posCount++;
			}
			while (l < 6) {
				y[l] += r;
				l++;
			}
			l = 0;
		}
		// firstPosition = pBluePos.get(0);
		checkAreas();
		if (!blueAreas.isEmpty()) {
			/*
			 * for (int i = pBluePos.get(0); i < pBluePos.size(); i++) { if
			 * (pRedPos.contains(i + 19)) { if (pBluePos.contains(i + 20)) { i
			 * += 20; } else i -= 21; } if (pRedPos.contains(i - 21)) { if
			 * (pBluePos.contains(i - 20)) i -= 20; else i -= 40; } if
			 * (pRedPos.contains(i - 40)) { if (pBluePos.contains(i - 20)) i -=
			 * 20; else i -= 19; } if (pRedPos.contains(i - 20)) { if
			 * (pBluePos.contains(i + 20)) i += 20; else i -= 40; } if
			 * (pRedPos.contains(i + 20)) { if (pBluePos.contains(i + 50)) i +=
			 * 40; else i += 19; } if (pRedPos.contains(i + 40)) { if
			 * (pBluePos.contains(i + 20)) i += 20; else i += 19; } }
			 */
			for (int i = 0; i < blueAreas.size(); i++) {
				int[] eq = new int[] { (int) (blueAreas.get(i).x - 1.5 * t),
						(int) (blueAreas.get(i).y + r), blueAreas.get(i).x,
						(int) (blueAreas.get(i).y + 2 * r),
						(int) (blueAreas.get(i).x + 1.5 * t),
						(int) (blueAreas.get(i).y + r),
						(int) (blueAreas.get(i).x + 1.5 * t),
						(int) (blueAreas.get(i).y - r), blueAreas.get(i).x,
						(int) (blueAreas.get(i).y - 2 * r),
						(int) (blueAreas.get(i).x - 1.5 * t),
						(int) (blueAreas.get(i).y - r) };
				for (int j = 0; j < eq.length - 1; j += 2) {
					int[] check = new int[] { eq[j], eq[j + 1] };
					if (redAreas.contains(check)) {
						for (int jb = j + 2; jb < eq.length; jb += 2) {
							if (blueAreas.contains(new int[] { eq[jb],
									eq[jb + 1] })) {
								// xBlue.add(blueAreas.get(i).x);
								// yBlue.add(blueAreas.get(i).y);
								j = eq.length;
								jb = eq.length;
								l++;
							}
						}
					}
				}
				g.setColor(Color.green);
				g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND)); // g.drawLine(blueAreas.get(i)[0],
													// blueAreas.get(i)[1],
													// blueAreas.get(i)[2],
													// blueAreas.get(i)[3]);
			}
			if (xBlue.size() > 0 && yBlue.size() > 0) {
				for (int i = 0; i < xBlue.size(); i++) {
					g.drawPolygon(convertIntegers(xBlue.get(i)),
							convertIntegers(yBlue.get(i)), xBlue.get(i).size());
				}
			}
		}
		if (!redAreas.isEmpty()) {
			for (int i = 0; i < redAreas.size(); i++) {
				g.setColor(Color.yellow);
				g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND));
				// g.drawLine(redAreas.get(i)[0], redAreas.get(i)[1],
				// redAreas.get(i)[2], redAreas.get(i)[3]);
			}
		}
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

	// Проверка на вхождение шестиугольника в множество шестиугольников игроков
	public void checkArrays(double[] x, double[] y) {
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

	// Проверка на соседство шестиугольников
	public void checkAreas() {
		int l = 0, sumXd = 0, sumYe = 0;
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
						l++;
					}
					ba = new Point(sumXd / 6, sumYe / 6);
					if (!blueAreas.contains(ba)) {
						blueAreas.add(ba);
						currentArea.add(ba);
						if (36 > ba.distance(currentArea.get(0))
								&& ba.distance(currentArea.get(0)) > 33
								&& !ba.equals(currentArea.get(1))) {
							ArrayList<Integer> xBlueNew = new ArrayList<Integer>();
							ArrayList<Integer> yBlueNew = new ArrayList<Integer>();
							for (int gb = 0; gb < currentArea.size(); gb++) {
								xBlueNew.add(currentArea.get(gb).x);
								yBlueNew.add(currentArea.get(gb).y);
							}
							xBlue.add(xBlueNew);
							yBlue.add(yBlueNew);
							currentArea.clear();
						}
					}
					l = 0;
					sumXd = 0;
					sumYe = 0;
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
						l++;
					}
					ba = new Point(sumXd / 6, sumYe / 6);
					if (!redAreas.contains(ba))
						redAreas.add(ba);
					l = 0;
					sumXd = 0;
					sumYe = 0;
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

