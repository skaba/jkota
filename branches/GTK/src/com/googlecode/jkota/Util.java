package com.googlecode.jkota;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Util {
	public static String errorMessage(Exception e) {
		StringWriter out=new StringWriter();
		e.printStackTrace(new PrintWriter(out));
		out.flush();
		return out.getBuffer().toString();
	}
}
