package com.googlecode.jkota.swing;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.googlecode.jkota.QuotaInfo;
import com.googlecode.jkota.Unit;

public class StatsListModel extends AbstractTableModel implements TableModel {

	private QuotaInfo quotas[];
	private static Class<?>[] classes={ String.class,Double.class,Double.class};
	private Unit viewUnit;
	public StatsListModel(QuotaInfo quotas[],Unit viewUnit) {
		this.quotas=quotas;
		this.viewUnit=viewUnit;
	}

	public int getColumnCount() { return 3; }

	public int getRowCount() {	return quotas.length; }

	@Override
	public Class<?> getColumnClass(int columnIndex) { return classes[columnIndex]; }

	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Ay";
		case 1:
			return "Download ("+viewUnit+")";
		case 2:
			return "Upload ("+viewUnit+")";
		default:
			return "";
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return quotas[rowIndex].getMonth();
		case 1:
			return (double)quotas[rowIndex].getDownloadedBytes()/viewUnit.getDivider();
		case 2:
			return (double)quotas[rowIndex].getUploadedBytes()/viewUnit.getDivider();
		default:
			return null;
		}
	}
}
