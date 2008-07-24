package com.googlecode.jkota.swing;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		BaseDownloader downloader=BaseDownloader.getInstance(settings.getSetting("updater"));
		QuotaInfo quotas[]=new QuotaInfo[downloader.getQuotaSize()];
		for(int i=0;i<downloader.getQuotaSize();i++) {
			QuotaInfo info = downloader.getQuota(i);
			quotas[quotas.length-i-1]=info;
			dataset.addValue((double)info.getDownloadedBytes()/viewUnit.getDivider(), "Download", info.getMonth());
			dataset.addValue((double)info.getUploadedBytes()/viewUnit.getDivider(), "Upload", info.getMonth());
		}

		JFreeChart chart = ChartFactory.createLineChart(
			null,
			"Ay",
			"Miktar ("+viewUnit+")",
			dataset,
			PlotOrientation.VERTICAL,
			true,
			true,
			false
		);
		chart.setAntiAlias(true);
		ChartPanel chartPanel = new ChartPanel(chart);

		StatsListModel model = new StatsListModel(quotas,viewUnit);
		JTable statslist=new JTable(model);
		JScrollPane scroller=new JScrollPane(statslist);
		Container contentPane=getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(chartPanel,BorderLayout.PAGE_START);
		contentPane.add(scroller,BorderLayout.CENTER);
		setSize(780,580);
		SwingUtil.center(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}