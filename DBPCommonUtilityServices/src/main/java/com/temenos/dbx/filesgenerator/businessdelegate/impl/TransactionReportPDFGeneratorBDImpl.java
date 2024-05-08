package com.temenos.dbx.filesgenerator.businessdelegate.impl;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.temenos.dbx.filesgenerator.businessdelegate.api.TransactionReportPDFGeneratorBD;
import com.temenos.dbx.transaction.dto.BankDTO;
import com.temenos.dbx.transaction.dto.TransactionDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TransactionReportPDFGeneratorBDImpl implements TransactionReportPDFGeneratorBD {

    private static final Logger LOG = LogManager.getLogger(TransactionReportPDFGeneratorBDImpl.class);
    private final Color TRANS_TABLE_FIELD_FONT_COLOR = new Color(128, 128, 128);
    private final Color TRANS_TABLE_VALUE_FONT_COLOR = new Color(0, 0, 0);
    private TransactionDTO transaction;
    private BankDTO bank;

    @Override
    public byte[] generateFileAsByte(TransactionDTO transaction, BankDTO bank) {
        this.transaction = transaction;
        this.bank = bank;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        Rectangle pageSize = document.getPageSize();
        final float pageTop = pageSize.getTop() - 70;
        final float pageLeft = pageSize.getLeft() + 45;
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();

        // main table
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        // add Logo
        addLogo(table, pageTop, pageLeft);

        // add Transfer Details
        table.addCell(_createBankTable());
        document.add(table);
        try {
            os.close();
            document.close();
            writer.close();
        } catch (IOException e) {
            LOG.error("Error occurred while closing output stream");
        }

        return os.toByteArray();

    }


    private void addLogo(PdfPTable table, float pageTop, float pageLeft) {
        InputStream is = TransactionReportPDFGeneratorBDImpl.class.getClassLoader().getResourceAsStream("infinity_logo.png");

        try {
            Image infinityLogo = Image.getInstance(IOUtils.toByteArray(is));
            infinityLogo.setSpacingAfter(10);
            infinityLogo.scaleAbsolute(100, 28);
            infinityLogo.setAbsolutePosition(pageLeft, pageTop);
            PdfPCell cellLogo = new PdfPCell();
            cellLogo.addElement(infinityLogo);
            Paragraph bankAddrParagraph = new Paragraph(
                    bank.getAddress1() + "," + bank.getAddress2() + ".Phone:" + bank.getPhone(),
                    new Font(Font.HELVETICA, 7, Font.NORMAL, TRANS_TABLE_FIELD_FONT_COLOR));
            cellLogo.addElement(bankAddrParagraph);
            cellLogo.setPaddingTop(15);
            cellLogo.setPaddingLeft(15f);
            cellLogo.setPaddingBottom(20);
            cellLogo.setBorder(Rectangle.BOX);
            cellLogo.setBorderColor(Color.DARK_GRAY);
            table.addCell(cellLogo);
        } catch (Exception e) {
            LOG.error("Error creating file", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    LOG.error("Error Occurred while adding infinity logo in PDF ", e);
                }
            }
        }
    }

    private PdfPTable _createBankTable() {

        PdfPTable bankTable = new PdfPTable(2);
        bankTable.setSpacingBefore(10);
        bankTable.setWidthPercentage(100);
        bankTable.setWidths(new float[]{0.5f, 2.2f});
        PdfPCell addrCell = new PdfPCell(new Paragraph("Transfer Details",
                new Font(Font.HELVETICA, 8, Font.NORMAL, TRANS_TABLE_VALUE_FONT_COLOR)));
        addrCell.setColspan(2);
        _setBorder(addrCell);
        bankTable.addCell(addrCell);
        bankTable.completeRow();

        bankTable.addCell(_noBorderCell3("Reference Number:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getTransactionId())) {
            bankTable.addCell(_noBorderCell1(transaction.getTransactionId(), TRANS_TABLE_VALUE_FONT_COLOR));
        }

        bankTable.completeRow();

        bankTable.addCell(_noBorderCell("From Account:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getFromNickName())) {
            bankTable.addCell(_noBorderCell2(transaction.getFromNickName(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        createRecipientCell(bankTable);
        bankTable.addCell(_noBorderCell("Date:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getTransactionDate())) {
            bankTable.addCell(_noBorderCell2(transaction.getTransactionDate(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        bankTable.addCell(_noBorderCell("Amount:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getAmount())) {
            bankTable.addCell(_noBorderCell2(transaction.getAmount(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        bankTable.addCell(_noBorderCell("Payment Description:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getDescription())) {
            bankTable.addCell(_noBorderCell2(transaction.getDescription(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        bankTable.addCell(_noBorderCell("Transaction Type:", TRANS_TABLE_FIELD_FONT_COLOR));
        if (StringUtils.isNotBlank(transaction.getTransactionType())) {
            bankTable.addCell(_noBorderCell2(transaction.getTransactionType(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        if (StringUtils.isNotBlank(transaction.getFrequencyType())
                && !"Once".equalsIgnoreCase(transaction.getFrequencyType())) {
            bankTable.addCell(_noBorderCell("Frequency:", TRANS_TABLE_FIELD_FONT_COLOR));
            bankTable.addCell(_noBorderCell2(transaction.getFrequencyType(), TRANS_TABLE_VALUE_FONT_COLOR));

        }
        bankTable.completeRow();
        if (StringUtils.isNotBlank(transaction.getRecurrenceDesc())) {
            bankTable.addCell(_noBorderCell("Recurrence:", TRANS_TABLE_FIELD_FONT_COLOR));
            bankTable.addCell(_noBorderCell2(transaction.getRecurrenceDesc(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        if (StringUtils.isNotBlank(transaction.getFrequencyStartDate())) {
            bankTable.addCell(_noBorderCell("Start Date:", TRANS_TABLE_FIELD_FONT_COLOR));
            bankTable.addCell(_noBorderCell2(transaction.getFrequencyStartDate(), TRANS_TABLE_VALUE_FONT_COLOR));

        }
        bankTable.completeRow();
        if (StringUtils.isNotBlank(transaction.getFrequencyEndDate())) {
            bankTable.addCell(_noBorderCell("End Date:", TRANS_TABLE_FIELD_FONT_COLOR));
            bankTable.addCell(_noBorderCell2(transaction.getFrequencyEndDate(), TRANS_TABLE_VALUE_FONT_COLOR));

        }
        bankTable.completeRow();
        if (StringUtils.isNotBlank(transaction.getTransactionsNotes())) {
            bankTable.addCell(_noBorderCell("Note:", TRANS_TABLE_FIELD_FONT_COLOR));
            bankTable.addCell(_noBorderCell2(transaction.getTransactionsNotes(), TRANS_TABLE_VALUE_FONT_COLOR));
        }
        bankTable.completeRow();
        PdfPCell blankCell2 = new PdfPCell(new Paragraph(""));
        blankCell2.setColspan(2);
        blankCell2.setPaddingTop(6);
        blankCell2.setPaddingBottom(150);
        blankCell2.setBorderColor(Color.WHITE);
        bankTable.addCell(blankCell2);
        return bankTable;
    }

    public void createRecipientCell(PdfPTable transferTable) {
        String toRecipient = null;
        if (StringUtils.isNotBlank(transaction.getToNickName())) {
            toRecipient = transaction.getToNickName();
        } else if (StringUtils.isNotBlank(transaction.getToAccountName())) {
            toRecipient = transaction.getToAccountName();
        } else if (StringUtils.isNotBlank(transaction.getPayeeNickName())) {
            toRecipient = transaction.getPayeeNickName();
        } else if (StringUtils.isNotBlank(transaction.getPayeeName())) {
            toRecipient = transaction.getPayeeName();
        } else if (StringUtils.isNotBlank(transaction.getPayPersonName())) {
            toRecipient = transaction.getPayPersonName();
        } else if (StringUtils.isNotBlank(transaction.getPayPersonPhone())) {
            toRecipient = transaction.getPayPersonPhone();
        } else if (StringUtils.isNotBlank(transaction.getPayPersonEmail())) {
            toRecipient = transaction.getPayPersonEmail();
        }
        if (StringUtils.isNotBlank(toRecipient)) {
            transferTable.addCell(_noBorderCell("To Recipient:", TRANS_TABLE_VALUE_FONT_COLOR));
        }
    }

    private PdfPCell _setBorder(PdfPCell cell) {
        cell.setPaddingLeft(15f);
        cell.setPaddingTop(7f);
        cell.setPaddingBottom(7f);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(Color.BLACK);
        cell.setPaddingLeft(15f);

        return cell;
    }

    public static PdfPCell _noBorderCell(String text, Color fontColor) {
        Paragraph cellText = new Paragraph(text, new Font(Font.HELVETICA, 8, Font.NORMAL, fontColor));
        PdfPCell cell = new PdfPCell();
        cell.addElement(cellText);
        cell.setBorderColor(Color.WHITE);
        cell.setPaddingTop(2);
        cell.setPaddingLeft(15f);
        cell.setPaddingBottom(2);
        return cell;
    }

    public static PdfPCell _noBorderCell2(String text, Color fontColor) {
        Paragraph cellText = new Paragraph(text, new Font(Font.HELVETICA, 8, Font.NORMAL, fontColor));
        PdfPCell cell = new PdfPCell();
        cell.addElement(cellText);
        cell.setBorderColor(Color.WHITE);
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        return cell;
    }

    public static PdfPCell _noBorderCell1(String text, Color fontColor) {
        Paragraph cellText = new Paragraph(text, new Font(Font.HELVETICA, 8, Font.NORMAL, fontColor));
        PdfPCell cell = new PdfPCell();
        cell.addElement(cellText);
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(Color.BLACK);
        cell.setPaddingTop(4);
        cell.setPaddingBottom(2);
        return cell;
    }

    public static PdfPCell _noBorderCell3(String text, Color fontColor) {
        Paragraph cellText = new Paragraph(text, new Font(Font.HELVETICA, 8, Font.NORMAL, fontColor));
        PdfPCell cell = new PdfPCell();
        cell.addElement(cellText);
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(Color.BLACK);
        cell.setPaddingTop(2);
        cell.setPaddingLeft(15f);
        cell.setPaddingBottom(2);
        return cell;
    }
}