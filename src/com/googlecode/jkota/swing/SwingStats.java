package com.googlecode.jkota.swing;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.googlecode.jkota.BaseDownloader;
import com.googlecode.jkota.QuotaInfo;
import com.googlecode.jkota.SettingsManager;
import com.googlecode.jkota.Unit;

public class SwingStats extends JFrame {
	public SwingStats() {
		super("İnternet Kullanım İstatistikleri");
		SettingsManager settings=SettingsManager.getInstance();
		Unit viewUnit=BaseDownloader.getInstance(settings.getSetting("updater")).getViewUnit();
		JFreeChart chart = ChartFactory.createLineChart(
			null,
			"Ay",
			"Miktar ("+viewUnit+")",
			createDataSet(viewUnit),
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		chart.setAntiAlias(true);
		ChartPanel chartPanel = new ChartPanel(chart);
		getContentPane().add(chartPanel);
		setSize(800,400);
		setResizable(false);
		SwingUtil.center(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private DefaultCategoryDataset  createDataSet(Unit unit) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		SettingsManager settings=SettingsManager.getInstance();
		BaseDownloader downloader=BaseDownloader.getInstance(settings.getSetting("updater"));
		for(int i=0;i<downloader.getQuotaSize();i++) {
			QuotaInfo info = downloader.getQuota(i);
			dataset.addValue(info.getDownloadedBytes()/unit.getDivider(), "Download", info.getMonth());
			dataset.addValue(info.getUploadedBytes()/unit.getDivider(), "Upload", info.getMonth());
		}
		return dataset;
	}
}