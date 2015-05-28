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
import java.util.Collections;
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
	private ArrayList<Point> currentRedArea = new ArrayList<Point>();
	private ArrayList<Integer> pBluePos = new ArrayList<Integer>();
	private ArrayList<Integer> pRedPos = new ArrayList<Integer>();
	private ArrayList<ArrayList<Point>> blue = new ArrayList<ArrayList<Point>>();
	private ArrayList<ArrayList<Integer>> xRed = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> yRed = new ArrayList<ArrayList<Integer>>();
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
		checkAreas();
		if (!blueAreas.isEmpty()) {
			g.setColor(Color.green);
			g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			if (blue.size() > 0) {
				for (int i = 0; i < blue.size(); i++) {
					poly = new Polygon();
					blue.set(i, grahamScan(blue.get(i)));
					for (int j = 0; j < blue.get(i).size(); j++) {
						poly.addPoint(blue.get(i).get(j).x,
								blue.get(i).get(j).y);
					}
					g.drawPolygon(poly);
				}
			}
		}
		if (!redAreas.isEmpty()) {
			g.setColor(Color.yellow);
			g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND));
			if (xRed.size() > 0 && yRed.size() > 0) {
				for (int i = 0; i < xRed.size(); i++) {
					g.drawPolygon(convertIntegers(xRed.get(i)),
							convertIntegers(yRed.get(i)), xRed.get(i).size());
				}
			}
		}
	}

	// Ковертация ArrayList<Integer> в int[] для построения полигона
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
				if (playerCheck(bluePlayer, d, a, b, e)) {
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
							ArrayList<Point> blueNew = new ArrayList<Point>();
							currentArea = grahamScan(currentArea);
							blueNew.add(currentArea.get(0));
							for (int gb = 1; gb < currentArea.size(); gb++) {
								for (int g = 1; g < currentArea.size(); g++) {
									/*if (36 > currentArea.get(gb).distance(
											blueNew.get(blueNew.size() - 1))
											&& currentArea
													.get(gb)
													.distance(
															blueNew.get(blueNew
																	.size() - 1)) > 33) {*/
										if (!blueNew.contains(currentArea
												.get(g)))
											blueNew.add(currentArea.get(g));
									//}
								}
							}
							blue.add(blueNew);
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
				if (playerCheck(redPlayer, d, a, b, e)) {
					while (l < 6) {
						sumXd += redPlayer.get(d)[l];
						sumYe += redPlayer.get(e)[l];
						l++;
					}
					ba = new Point(sumXd / 6, sumYe / 6);
					if (!redAreas.contains(ba)) {
						redAreas.add(ba);
						currentRedArea.add(ba);
						if (36 > ba.distance(currentRedArea.get(0))
								&& ba.distance(currentRedArea.get(0)) > 33
								&& !ba.equals(currentRedArea.get(1))) {
							ArrayList<Integer> xRedNew = new ArrayList<Integer>();
							ArrayList<Integer> yRedNew = new ArrayList<Integer>();
							xRedNew.add(currentRedArea.get(0).x);
							yRedNew.add(currentRedArea.get(0).y);
							for (int gb = 1; gb < currentRedArea.size(); gb++) {
								if (36 > currentRedArea.get(gb)
										.distance(
												new Point(xRedNew.get(xRedNew
														.size() - 1),
														yRedNew.get(yRedNew
																.size() - 1)))
										&& currentRedArea.get(gb).distance(
												new Point(xRedNew.get(xRedNew
														.size() - 1),
														yRedNew.get(yRedNew
																.size() - 1))) > 33) {
									xRedNew.add(currentRedArea.get(gb).x);
									yRedNew.add(currentRedArea.get(gb).y);
								}
							}
							xRed.add(xRedNew);
							yRed.add(yRedNew);
							currentRedArea.clear();
						}
					}
					l = 0;
					sumXd = 0;
					sumYe = 0;
				}
			}
		}
	}

	// Определение расположения точки c относительно вектора ab (слева > 0,
	// справа < 0)
	public int rotate(Point a, Point b, Point c) {
		return (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x);
	}

	// Алгоритм сортировки точек Graham scan
	public ArrayList<Point> grahamScan(ArrayList<Point> points) {
		int k = 0;
		for (int j = 1; j < points.size(); j++) {
			if (points.get(j).x < points.get(0).x) {
				Collections.swap(points, 0, j);
			}
		}
		for (int j = 2; j < points.size(); j++) {
			k = j;
			while (k > 1
					&& rotate(points.get(0), points.get(k - 1), points.get(k)) < 0) {
				Collections.swap(points, k - 1, k);
				k--;
			}
		}
		return points;
	}

	/*
	 * public ArrayList<Point> grahamScanForCure(ArrayList<Point> cur) { int k =
	 * 0; for (int j = 0; j < cur.size(); j++) { if (cur.get(j).x <
	 * cur.get(0).x) { Collections.swap(cur, 0, j); Collections.swap(cur, 0, j);
	 * } } for (int j = 0; j < cur.size(); j++) { k = j; while (k > 1 &&
	 * rotate(cur.get(j).x, cur.get(j).y, cur.get(k - 1).x, cur.get(k - 1).y,
	 * cur.get(k).x, cur.get(k).y) < 0) { Collections.swap(cur, k - 1, k);
	 * Collections.swap(cur, k - 1, k); k--; } } return cur; }
	 */

	public boolean playerCheck(ArrayList<double[]> player, int d, int a, int b,
			int e) {
		if ((player.get(d)[0] == player.get(a)[4]
				&& player.get(d)[1] == player.get(a)[3]
				&& player.get(e)[0] == player.get(b)[4] && player.get(e)[1] == player
				.get(b)[3])
				|| (player.get(d)[1] == player.get(a)[5]
						&& player.get(d)[2] == player.get(a)[4]
						&& player.get(e)[1] == player.get(b)[5] && player
						.get(e)[2] == player.get(b)[4])
				|| (player.get(d)[2] == player.get(a)[0]
						&& player.get(d)[3] == player.get(a)[5]
						&& player.get(e)[0] == player.get(b)[0] && player
						.get(e)[1] == player.get(b)[5])
				|| (player.get(d)[3] == player.get(a)[1]
						&& player.get(d)[4] == player.get(a)[0]
						&& player.get(e)[3] == player.get(b)[1] && player
						.get(e)[4] == player.get(b)[0])
				|| (player.get(d)[4] == player.get(a)[2]
						&& player.get(d)[5] == player.get(a)[1]
						&& player.get(e)[4] == player.get(b)[2] && player
						.get(e)[5] == player.get(b)[1])
				|| (player.get(d)[5] == player.get(a)[3]
						&& player.get(d)[0] == player.get(a)[2]
						&& player.get(e)[5] == player.get(b)[3] && player
						.get(e)[0] == player.get(b)[2])
				|| (player.get(d)[5] == player.get(a)[3]
						&& player.get(d)[0] == player.get(a)[2]
						&& player.get(e)[3] == player.get(b)[5] && player
						.get(e)[2] == player.get(b)[0]))
			return true;
		else
			return false;

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
		posY = e.getY() - t;
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
