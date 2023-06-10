package org.scheduler.onsite.utils;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GraphPlotter extends JFrame {

	private JFreeChart chart;

	private static class Point {

		private double x;

		private double y;

		public Point(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + " )";
		}
	}

	private XYSeriesCollection dataset = new XYSeriesCollection();

	public GraphPlotter(String plotSubject, String xAxisLabel, String yAxisLabel) {
		super(plotSubject);

		JPanel chartPanel = createChartPanel(plotSubject, xAxisLabel, yAxisLabel);
		add(chartPanel, BorderLayout.CENTER);

		setSize(640, 480);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}

	public void addDataPoint(String graphName, int generation, double fitness) {
		addDataPoint(graphName, new Point(generation, fitness));
		setVisible(true);
	}

	public void saveAsImage(String filePath) {
		try {
			ChartUtilities.saveChartAsJPEG(new File(filePath), chart, 1920, 1080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addDataPoint(String graphName, Point p) {
		XYSeries series = null;
		try {
			series = dataset.getSeries(graphName);
		} catch (Exception e) {
			series = new XYSeries(graphName);
			dataset.addSeries(series);
		}
		series.add(p.x, p.y);
	}

	private JPanel createChartPanel(String chartTitle, String xAxisLabel, String yAxisLabel) {

		boolean showLegend = true;
		boolean createURL = false;
		boolean createTooltip = false;

		chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL,
				showLegend, createTooltip, createURL);
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		plot.setRenderer(renderer);

		return new ChartPanel(chart);

	}

}