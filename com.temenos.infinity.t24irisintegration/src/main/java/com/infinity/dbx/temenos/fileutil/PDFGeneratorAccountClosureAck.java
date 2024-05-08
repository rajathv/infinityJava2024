package com.infinity.dbx.temenos.fileutil;

import static java.awt.Color.BLACK;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFGeneratorAccountClosureAck {


	private Map<String, Object> changeRepaymentAccountFields;
	
	
	private static final Logger log = LogManager.getLogger(PDFGeneratorChangeRepaymentDayReqAck.class);

	public PDFGeneratorAccountClosureAck(Map<String, Object> changeRepaymentAccountFields) {
		this.changeRepaymentAccountFields = changeRepaymentAccountFields;
		
	}

	public String generateFileAndGetBase64() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, os);
			document.open();

			Rectangle rect = new Rectangle(36, 108);
			rect.enableBorderSide(1);
			rect.enableBorderSide(2);
			rect.enableBorderSide(4);
			rect.enableBorderSide(8);
			rect.setBorder(2);
			rect.setBorderColor(BLACK);
			document.add(rect);

			//Adding Infinity Logo
			addLogo(document);

			//adding header
			addHeaderLabel(document, "Account Closure Request Details");

			//Creating Table
			document.add(this.getTable());

			document.close();
			return Base64.getEncoder().encodeToString(os.toByteArray());
		} catch (Exception e) {
			return null;
		}
	}

	public byte[] generateFileAndGetByteArray() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, os);
			document.open();

			Rectangle rect = new Rectangle(36, 108);
			rect.enableBorderSide(1);
			rect.enableBorderSide(2);
			rect.enableBorderSide(4);
			rect.enableBorderSide(8);
			rect.setBorder(2);
			rect.setBorderColor(BLACK);
			document.add(rect);

			//Adding Infinity Logo
			addLogo(document);

			//adding header
			addHeaderLabel(document, "Account Closure Request Details");

			//Creating Table
			document.add(this.getTable());
			
			document.close();
			return os.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}


	public void addLogo(Document document) {
		InputStream is = PDFGeneratorChangeRepaymentDayReqAck.class.getClassLoader().getResourceAsStream("infinity_logo.png");
		try {
			Image infinityLogo = Image.getInstance(IOUtils.toByteArray(is));
			infinityLogo.setSpacingAfter(70);
			infinityLogo.scaleAbsoluteHeight(40);
			infinityLogo.scaleAbsoluteWidth(90);
			document.add(infinityLogo);
		} catch (Exception e) {

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					log.error("Error Occurred while adding infinity logo in PDF ", e);
				}
			}
		}
	}

	public void addHeaderLabel(Document document, String label) {
		PdfPTable tabAcc = new PdfPTable(1);
		tabAcc.setSpacingBefore(10);
		tabAcc.setWidthPercentage(100);
		Font font = new Font(Font.TIMES_ROMAN, 24, Font.BOLD, Color.BLACK);
		Phrase stat1=new Phrase("\n" + label + "\n\n",font);
		PdfPCell lbl = new PdfPCell(stat1);
		lbl.setBorderWidthTop(2);
		lbl.setBorderWidthBottom(2);
		lbl.setBorderWidthLeft(0);
		lbl.setBorderWidthRight(0);
		tabAcc.addCell(lbl);
		document.add(tabAcc);
	}

	private PdfPTable getTable() {
		PdfPTable table = new PdfPTable(2);
		table.setSpacingBefore(10);
		table.setWidthPercentage(100);
		table.setWidths(new float[]{1.5f, 2.2f});

		for (Map.Entry<String, Object> entry : this.changeRepaymentAccountFields.entrySet()) {
			try {
				Paragraph key = new Paragraph(entry.getKey());
				if (entry.getValue().toString().isEmpty()) {
					continue;
				}
				Paragraph value = new Paragraph(entry.getValue().toString());
				PdfPCell keyCell = new PdfPCell();
				PdfPCell valueCell = new PdfPCell();

				keyCell.setFixedHeight(1.0f);
				keyCell.setPadding(1.0f);
				keyCell.setPaddingTop(2);
				keyCell.setPaddingBottom(2);
				keyCell.addElement(key);


				valueCell.addElement(value);

				table.addCell(keyCell);
				table.addCell(valueCell);
				table.setSpacingAfter(24);

			} catch (Exception e) {
				log.error("Error Occurred while creating table in PDF ", e);
			}
		}

		return table;
	}
}


