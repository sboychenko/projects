package ru.bserg.pricegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ru.bserg.pricegen.data.Data;
import ru.bserg.pricegen.exception.LoadException;
import ru.bserg.pricegen.exception.ParseException;

public class DataLoader {
	String ENCODING = "WINDOWS-1251";
	String DELIM = "~";
	
	public DataLoader() {
		
	}
	
	public DataLoader(String encoding, String delim) {
		this.ENCODING = encoding;
		this.DELIM = delim;
	}

	public ArrayList<Data> read(File f) throws LoadException, IOException {
		if (!f.exists() || !f.isFile()) {
			throw new LoadException("File "+ f.getAbsolutePath() + " not found!");
		}
		
		ArrayList<Data> list = new ArrayList<Data>();
		
		BufferedReader in = null;
		int i = 1;
		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "WINDOWS-1251"));
			String line = null;
			while ((line = in.readLine()) != null) {
					list.add(getPart(line));
					i++;
			}
		} catch (ParseException e) {
			throw new LoadException("Error read "+i+" line of " + f.getAbsolutePath(), e);
		} catch (Exception e) {
			throw new LoadException("Error in load data from file [" + f.getAbsolutePath() + "]", e);
		} finally {
			if (in != null) in.close();
		}
		
		return list;
	}

	private Data getPart(String str) throws ParseException {
		Data data = new Data();
		try {
			String[] parts = str.split("~");
			data.setNum(Integer.parseInt(parts[2]));
			data.setBrand(parts[0]);
			data.setGroup(parts[1]);
			data.setCode(Long.parseLong(parts[3]));
			data.setName(parts[4]);
			data.setSku(parts[5]);
			data.setPrice(Double.parseDouble(parts[6]));
			data.setBool(("ÄÀ".equalsIgnoreCase(parts[7])) ? true : false );
			data.setDoub(Double.parseDouble(parts[8]));
		} catch (Exception e) {
			throw new ParseException("Error parse line [" +str+"]", e);
		}
		return data;
	}
	
	public static void main(String[] args) throws Exception {
		DataLoader ir = new DataLoader();
		//System.out.println(ir.read(new File("Tovar.txt")));
		
		ArrayList<Data> list = ir.read(new File("Tovar.txt"));
		System.out.println("<");
		for (Data data : list) {
			System.out.println(data);
		}
		System.out.println(">");
		
	}
}
