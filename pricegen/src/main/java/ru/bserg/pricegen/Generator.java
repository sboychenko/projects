package ru.bserg.pricegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import ru.bserg.pricegen.commons.Commons;
import ru.bserg.pricegen.data.Data;
import ru.bserg.pricegen.exception.LoadException;

public class Generator {
	HSSFWorkbook workbook;
	HSSFSheet sheet;
	
	private String inputName = null;
	private String templateName = "template.xls";
	private boolean protect = false;
	private String PASSWORD = "shig";
	private boolean BOOLVAR = false;
	private boolean ZIROCNT = false;
	
	CellStyle unlockedCellStyle;
	
	public Generator(String inputName) {
		this.inputName = inputName;
	}
	
	public void process(String outName) throws FileNotFoundException, IOException, LoadException {
		
		workbook = new HSSFWorkbook(new FileInputStream(templateName));
        sheet = workbook.getSheetAt(0);
        
        unlockedCellStyle = workbook.createCellStyle();
    	unlockedCellStyle.setLocked(false);
        
        HSSFRow headerRow = getRow("header");
        HSSFRow lineRow = getRow("line");
        HSSFRow startRow = getRow("start");
        int start = startRow.getRowNum();
        int last;
        
        DataLoader dataLoader = new DataLoader();
        
        List<Data> list = dataLoader.read(new File(inputName));
        
        int l = 0;
        for (int i = 0; i < list.size();i++) {
        	Data data = list.get(i);
        	
        	if (i == 0 || !data.getBrand().equals(list.get(i-1).getBrand()) ) {
        		pasteRow(headerRow, start + l++, data);
        	}
            pasteRow(lineRow, start + l++, data);
        }
        last = l-1;
        
        removeRow(getRow("header").getRowNum());
        removeRow(getRow("line").getRowNum());
        removeRow(getRow("start").getRowNum());
        if (protect) {
        	sheet.protectSheet(PASSWORD);
        	unlock("unlock");
        }
        
        replaceFields(start, last);
        
        FileOutputStream out = new FileOutputStream(outName);
        workbook.write(out);
        out.close();
	}
	
	private HSSFRow getRow(String template) {
		Iterator<Row> itr = sheet.rowIterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Cell cell = row.getCell(0);
        	//System.out.println(cell);
        	if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING 
        			&& ("%"+template+"%").equals(cell.getStringCellValue())) {
        		//cell.setCellValue("");
        		return (HSSFRow)row;
        	}
        }
		return null;
	}
	
	private void replaceFields(int start, int last) {
		start--;
		Iterator<Row> itr = sheet.rowIterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Iterator<Cell> itc = row.cellIterator();
        	while (itc.hasNext()) {
        		Cell cell = itc.next();
        		CellReference cr = new CellReference(cell);
        		// Current date
        		if (cell.getCellType() == Cell.CELL_TYPE_STRING && "<date>".equals(cell.getStringCellValue())) {
        			cell.setCellValue(Commons.getCurrentDateStr());
        		}
        		// Sum
        		if (cell.getCellType() == Cell.CELL_TYPE_STRING && "<sum>".equals(cell.getStringCellValue())) {
        			//System.out.println(Arrays.toString(cr.getCellRefParts()));
        			String ref = cr.getCellRefParts()[2]+String.valueOf(start) + ":" + cr.getCellRefParts()[2]+String.valueOf(last+start);
        			cell.setCellFormula("SUM("+ref+")");
        			
        		}
        		
        		// Cnt
        		if (cell.getCellType() == Cell.CELL_TYPE_STRING && "<cnt>".equals(cell.getStringCellValue())) {
        			String ref = cr.getCellRefParts()[2]+String.valueOf(start) + ":" + cr.getCellRefParts()[2]+String.valueOf(last+start);
        			cell.setCellFormula("SUM("+ref+")");
        		}
        	}
        }
	}
	
	
	private void pasteRow(HSSFRow sourceRow, int destinationRowNum, Data data) {
        // Get the source / new row
        HSSFRow newRow = sheet.getRow(destinationRowNum);
        //HSSFRow sourceRow = sheet.getRow(sourceRowNum);
        
        // If the row exist in destination, push down all rows by 1 else create a new row
        if (newRow != null) {
            sheet.shiftRows(destinationRowNum, sheet.getLastRowNum(), 1);
        } else {
            newRow = sheet.createRow(destinationRowNum);
        }
        
        // Loop through source columns to add to new row
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            // Grab a copy of the old/new cell
            HSSFCell oldCell = sourceRow.getCell(i);
            HSSFCell newCell = newRow.createCell(i);
            // If the old cell is null jump to next cell
            if (oldCell == null) {
                newCell = null;
                continue;
            }

            // Copy style from old cell and apply to new cell
            //HSSFCellStyle newCellStyle = workbook.createCellStyle();
            //newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
            //newCell.setCellStyle(newCellStyle);
            
            newCell.setCellStyle(oldCell.getCellStyle());
            

            // If there is a cell comment, copy
            if (oldCell.getCellComment() != null) {
                newCell.setCellComment(oldCell.getCellComment());
            }

            // If there is a cell hyperlink, copy
            if (oldCell.getHyperlink() != null) {
                newCell.setHyperlink(oldCell.getHyperlink());
            }

            // Set the cell data type
            newCell.setCellType(oldCell.getCellType());

            // Set the cell data value
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
            
            // filling template from Data object
            String str = newCell.getStringCellValue();
            if (str != null) {
            	if (str.startsWith("%") && str.endsWith("%")) {
            		newCell.setCellValue("");
            	}
            	
            	if (str.equals("<brand>")) {
            		newCell.setCellValue(data.getBrand());
            	}
            	
            	if (str.equals("<num>")) { 
            		newCell.setCellValue(data.getNum());
            	} else if (str.equals("<group>")) {
            		newCell.setCellValue(data.getGroup());
            	} else if (str.equals("<code>")) {
            		newCell.setCellValue(data.getCode());
            	} else if (str.equals("<name>")) {
            		newCell.setCellValue(data.getName());
            	} else if (str.equals("<sku>")) {
            		newCell.setCellValue(data.getSku());
            	} else if (str.equals("<price>")) {
            		newCell.setCellValue(data.getPrice());
            	} else if (str.equals("<bool>")) {
            		if (BOOLVAR) {
            			if (data.getBool() != null && data.getBool().booleanValue()) { 
            				newCell.setCellValue("да");
            			}
            			else {
            				newCell.setCellValue("");
            			}
            		} else {
            			newCell.setCellValue(data.getBool());
            		}
            		
            	} else if (str.equals("<doub>")) {
            		newCell.setCellValue(data.getDoub());
            	} else if (str.equals("<formula>")) { 
            		CellReference crCurr = new CellReference(newCell);
            		CellReference cr1 = new CellReference(newCell.getRowIndex(), newCell.getColumnIndex()-1);
            		CellReference cr2 = new CellReference(newCell.getRowIndex(), newCell.getColumnIndex()-2);
            		String ref = cr1.getCellRefParts()[2]+crCurr.getCellRefParts()[1]+ "*" + cr2.getCellRefParts()[2]+crCurr.getCellRefParts()[1];
            		newCell.setCellFormula(ref);
            	} else if (str.equals("<unlock>")) {
            		newCell.setCellStyle(unlockedCellStyle);
            		if (isZIROCNT()) newCell.setCellValue(0);
            		else newCell.setCellValue("");
            	}
            }
        }

        // If there are are any merged regions in the source row, copy to new row
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
                        (newRow.getRowNum() +
                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
                                        )),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }
    }
	
	private void removeRow(int rowIndex) {
	    int lastRowNum=sheet.getLastRowNum();
	    if(rowIndex>=0&&rowIndex<lastRowNum){
	        sheet.shiftRows(rowIndex+1,lastRowNum, -1);
	    }
	    if(rowIndex==lastRowNum){
	        HSSFRow removingRow=sheet.getRow(rowIndex);
	        if(removingRow!=null){
	            sheet.removeRow(removingRow);
	        }
	    }
	}
	
	private void unlock(String template) {
		Iterator<Row> itr = sheet.rowIterator();
        while (itr.hasNext()) {
        	Row row = itr.next();
        	Cell cell = row.getCell(0);
        	//System.out.println(cell);
        	if (cell != null && cell.getCellType() == Cell.CELL_TYPE_STRING 
        			&& ("<"+template+">").equals(cell.getStringCellValue())) {
        		cell.setCellStyle(unlockedCellStyle);
        	}
        }
	}
	
	public static void main(String[] args) throws Exception{
        Generator test = new Generator("Tovar.txt");
        test.setProtect(false);
        test.process("out.xls");
        System.out.println("Well done!");
    }

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public boolean isProtect() {
		return protect;
	}

	public void setProtect(boolean protect) {
		this.protect = protect;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

	public boolean isBOOLVAR() {
		return BOOLVAR;
	}

	public void setBOOLVAR(boolean bOOLVAR) {
		BOOLVAR = bOOLVAR;
	}

	public boolean isZIROCNT() {
		return ZIROCNT;
	}

	public void setZIROCNT(boolean zIROCNT) {
		ZIROCNT = zIROCNT;
	}
	

}
