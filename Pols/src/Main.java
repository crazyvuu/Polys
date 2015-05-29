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
	private ArrayList<Point> bluePlayer = new ArrayList<Point>();
	private ArrayList<Point> redPlayer = new ArrayList<Point>();
	private ArrayList<Point> blueAreas = new ArrayList<Point>();
	private ArrayList<Point> redAreas = new ArrayList<Point>();
	private ArrayList<Point> currentArea = new ArrayList<Point>();
	private ArrayList<Point> currentRedArea = new ArrayList<Point>();
	private ArrayList<Point> currentRedSurroundArea = new ArrayList<Point>();
	private ArrayList<Point> currentBlueSurroundArea = new ArrayList<Point>();
	private ArrayList<Integer> pBluePos = new ArrayList<Integer>();
	private ArrayList<Integer> pRedPos = new ArrayList<Integer>();
	private ArrayList<ArrayList<Point>> blue = new ArrayList<ArrayList<Point>>();
	private ArrayList<ArrayList<Point>> red = new ArrayList<ArrayList<Point>>();
	Point redPoint;
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
		Point point;
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
				point = new Point((int) sumCount(x) / 6, (int) sumCount(y) / 6);
				checkArrays(point);
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
								redPlayer.add(point);
								pRedPos.add(posCount);
							} else {
								g.setColor(Color.BLUE);
								bluePlayer.add(point);
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
			if (red.size() > 0 && red.size() > 0) {
				for (int i = 0; i < red.size(); i++) {
					poly = new Polygon();
					red.set(i, grahamScan(red.get(i)));
					for (int j = 0; j < red.get(i).size(); j++) {
						poly.addPoint(red.get(i).get(j).x, red.get(i).get(j).y);
					}
					g.drawPolygon(poly);
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

	public double sumCount(double[] coords) {
		int i = 0;
		double sum = 0;
		while (i < 6) {
			sum += coords[i];
			i++;
		}
		return sum;
	}

	// Проверка на вхождение шестиугольника в множество шестиугольников игроков
	public void checkArrays(Point point) {
		for (int i = 0; i < bluePlayer.size(); i++) {
			if (bluePlayer.contains(point))
				blueHave = true;
		}
		for (int i = 0; i < redPlayer.size(); i++) {
			if (redPlayer.contains(point))
				redHave = true;
		}
	}

	// Проверка на соседство шестиугольников
	public void checkAreas() {
		for (int i = 0; i < bluePlayer.size(); i++) {
			for (int j = 0; j < bluePlayer.size(); j++) {
				if (playerCheck(bluePlayer, i, j)) {
					ba = new Point(bluePlayer.get(i).x, bluePlayer.get(i).y);
					if (!blueAreas.contains(ba)) {
						blueAreas.add(ba);
						currentArea.add(ba);
						currentArea = grahamScan(currentArea);
						checkSurroundPoints(currentArea, 0);
						if (36 > currentArea.get(currentArea.size() - 1)
								.distance(currentArea.get(0))
								&& currentArea.get(currentArea.size() - 1)
										.distance(currentArea.get(0)) > 33
								&& currentArea.size() > 2
								&& checkPointsSequence(currentArea)
								&& currentRedSurroundArea.size() != 0) {
							currentArea = deleteWrongPoints(currentArea, 0);
							ArrayList<Point> blueNew = new ArrayList<Point>();
							blueNew.add(currentArea.get(0));
							for (int gb = 1; gb < currentArea.size(); gb++) {
								for (int g = 1; g < currentArea.size(); g++) {
									if (36 > currentArea.get(gb).distance(
											blueNew.get(blueNew.size() - 1))
											&& currentArea
													.get(gb)
													.distance(
															blueNew.get(blueNew
																	.size() - 1)) > 33) {
										if (!blueNew.contains(currentArea
												.get(g)))
											blueNew.add(currentArea.get(g));
									}
								}
							}
							blue.add(blueNew);
							currentArea.clear();
							currentRedSurroundArea.clear();
						}
					}
				}
			}
		}
		for (int i = 0; i < redPlayer.size(); i++) {
			for (int j = 0; j < redPlayer.size(); j++) {
				if (playerCheck(redPlayer, i, j)) {
					ba = new Point(redPlayer.get(i).x, redPlayer.get(i).y);
					if (!redAreas.contains(ba)) {
						redAreas.add(ba);
						currentRedArea.add(ba);
						currentRedArea = grahamScan(currentRedArea);
						if (36 > currentRedArea.get(currentRedArea.size() - 1)
								.distance(currentRedArea.get(0))
								&& currentRedArea
										.get(currentRedArea.size() - 1)
										.distance(currentRedArea.get(0)) > 33
								&& currentRedArea.size() > 2
								&& checkPointsSequence(currentRedArea)
								&& currentBlueSurroundArea.size() != 0) {
							currentRedArea = deleteWrongPoints(currentRedArea,
									0);
							ArrayList<Point> redNew = new ArrayList<Point>();
							redNew.add(currentRedArea.get(0));
							for (int gb = 1; gb < currentRedArea.size(); gb++) {
								for (int g = 1; g < currentRedArea.size(); g++) {
									/*
									 * if (36 > currentRedArea.get(gb).distance(
									 * redNew.get(redNew.size() - 1)) &&
									 * currentRedArea .get(gb) .distance(
									 * redNew.get(redNew .size() - 1)) > 33) {
									 */
									if (!redNew.contains(currentRedArea.get(g)))
										redNew.add(currentRedArea.get(gb));
									// }
								}
							}
							red.add(redNew);
							currentRedArea.clear();
						}
					}
				}
			}
		}
	}

	// Проверка отсутствие разрывов в контуре
	public boolean checkPointsSequence(ArrayList<Point> points) {
		boolean result = true;
		for (int i = 0; i < points.size(); i++) {
			if (i != points.size() - 1) {
				if (points.get(i).distance(points.get(i + 1)) > 36) {
					result = false;
					break;
				}
			} else {
				if (points.get(i).distance(points.get(0)) > 36) {
					result = false;
					break;
				}
			}
		}
		return result;
	}

	// Проверка на вхождение вражеских точек в занятую область
	public void checkSurroundPoints(ArrayList<Point> points, int f) {
		if (f == 0) {
			for (int i = 0; i < points.size(); i++) {
				for (int j = 0; j < redPlayer.size(); j++) {
					if (points.get(i).distance(redPlayer.get(j)) < 36) {
						for (int k = 1; k < points.size(); k++) {
							if (points.get(k).distance(redPlayer.get(j)) < 36) {
								if (!currentRedSurroundArea.contains(redPlayer
										.get(j)))
									currentRedSurroundArea
											.add(redPlayer.get(j));
							}
						}
					}
				}
			}
		} else {
			for (int i = 0; i < points.size(); i++) {
				for (int j = 0; j < bluePlayer.size(); j++) {
					if (points.get(i).distance(bluePlayer.get(j)) < 36) {
						for (int k = 1; k < points.size(); k++) {
							if (points.get(k).distance(bluePlayer.get(j)) < 36) {
								if (!currentBlueSurroundArea
										.contains(bluePlayer.get(j)))
									currentBlueSurroundArea.add(bluePlayer
											.get(j));
							}
						}
					}
				}
			}
		}
	}

	// Удаление точек, не окружающих точки соперника и входящих в контур
	public ArrayList<Point> deleteWrongPoints(ArrayList<Point> points, int f) {
		boolean included = false;
		if (f == 0) {
			for (int i = 0; i < points.size(); i++) {
				for (int j = 0; j < currentRedSurroundArea.size(); j++) {
					if (points.get(i).distance(currentRedSurroundArea.get(j)) < 36) {
						included = true;
						break;
					}
				}
				if (!included) {
					points.remove(i);
					currentArea.remove(i);
					i--;
				} else
					included = false;
			}
		} else {
			for (int i = 0; i < points.size(); i++) {
				for (int j = 0; j < currentBlueSurroundArea.size(); j++) {
					if (points.get(i).distance(currentBlueSurroundArea.get(j)) < 36) {
						included = true;
						break;
					}
				}
				if (!included) {
					points.remove(i);
					currentRedArea.remove(i);
					i--;
				} else
					included = false;
			}
		}
		return points;
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

	public boolean playerCheck(ArrayList<Point> player, int i, int j) {
		if (player.get(i).distance(player.get(j)) < 36 && i != j)
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
