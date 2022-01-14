package com.example.toysocialnetwork.Domain;

import com.example.toysocialnetwork.Paging.Page;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.documentinterchange.prepress.PDBoxStyle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFReport {

    private String name;

    public PDFReport(String name){
        this.name = name;
    }

    /**
     * Creates a PDF file with the messages received by a user
     * @param tile The title of the PDF
     * @param messageList The list of messages
     * @throws IOException for the file
     */
    public void writeToFile(String tile, List<Message> messageList) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(200, 725);
        tile = tile + " Report";
        content.showText(tile);
        content.endText();

        //PDPageContentStream contentTable = new PDPageContentStream(document, page);

        final int rowsM = messageList.size() + 1;
        final int colM = 4;
        final float rowHeightM = 20f;
        final float tableWidthM = page.getMediaBox().getWidth() - (2 * 50);
        final float tableHeightM = rowHeightM * rowsM;
        final float colWidthM = tableWidthM /(float) colM;
        final float cellMarginM = 5f;

        float nextyM = 650 ;
        for (int i = 0; i <= rowsM; i++) {
            content.drawLine(50 ,nextyM,50 + tableWidthM,nextyM);
            nextyM-= rowHeightM;
        }

        float nextxM = 50;
        for (int i = 0; i <= colM; i++) {
            content.drawLine(nextxM,650,nextxM,650-tableHeightM);
            nextxM += colWidthM;
        }

        content.setFont(PDType1Font.HELVETICA_BOLD,16);

        float textxM = 50+cellMarginM;
        float textyM = 650-15;
        String headerTextM = "FirstName";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerTextM);
        content.endText();
        textxM += colWidthM;

        String headerText2M = "LastName";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText2M);
        content.endText();
        textxM += colWidthM;

        String headerText3M = "Message";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText3M);
        content.endText();
        textxM += colWidthM;

        String headerText4M = "Date Received";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText4M);
        content.endText();
        textxM += colWidthM;

        textyM-=rowHeightM;
        textxM = 50+cellMarginM;


        if(messageList.size() > 0) {
            content.setFont(PDType1Font.HELVETICA,8);
            for (int i = 0; i < messageList.size(); i++) {
                List<String> text = new ArrayList<>();
                text.add(messageList.get(i).getFromUser().getFirstName());
                text.add(messageList.get(i).getFromUser().getLastName());
                text.add(messageList.get(i).getMessage());
                if(messageList.get(i).getDate().toLocalTime().getHour() > 9 && messageList.get(i).getDate().toLocalTime().getMinute() > 9)
                    text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  " +messageList.get(i).getDate().toLocalTime().getHour()+ ":" +messageList.get(i).getDate().toLocalTime().getMinute());
                else
                    if(messageList.get(i).getDate().toLocalTime().getHour() > 9)
                        text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  " +messageList.get(i).getDate().toLocalTime().getHour()+ ":0" +messageList.get(i).getDate().toLocalTime().getMinute());
                    else
                        if(messageList.get(i).getDate().toLocalTime().getMinute() > 9)
                            text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  0" +messageList.get(i).getDate().toLocalTime().getHour()+ ":" +messageList.get(i).getDate().toLocalTime().getMinute());
                        else
                            text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  0" +messageList.get(i).getDate().toLocalTime().getHour()+ ":0" +messageList.get(i).getDate().toLocalTime().getMinute());
                for (int j = 0; j < colM; j++) {
                    content.beginText();
                    content.moveTextPositionByAmount(textxM, textyM);
                    content.drawString(text.get(j));
                    content.endText();
                    textxM += colWidthM;
                }
                textyM -= rowHeightM;
                textxM = 50 + cellMarginM;
            }
        }
        content.close();
        document.save(name);
        document.close();
    }

    /**
     * Creates a PDF file with all the new friends made and messages received by a user
     * @param title The title of the PDF file
     * @param messageList The list of messages received
     * @param friends The list of new friends made
     * @throws IOException for the file
     */
    public void writeToFileFriends(String title, List<Message> messageList, List<Friendship> friends) throws IOException {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("MessagesReceived", messageList.size());
        dataset.setValue("NewFriends", friends.size());
        JFreeChart chart = ChartFactory.createPieChart("User's Activity", dataset, true, true, false);
        //ChartPanel panel = new ChartPanel(chart);
        BufferedImage chartImage = chart.createBufferedImage(500, 500);
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(210, 725);
        title = title + " Report";
        content.showText(title);
        content.newLine();
        content.endText();
        PDImageXObject pdfChartImage = JPEGFactory.createFromImage(document, chartImage, 1f);
        content.drawImage(pdfChartImage, 50, 200);
        content.beginText();
        content.newLine();
        content.endText();
        content.close();
        PDPage page2 = new PDPage();
        document.addPage(page2);
        content = new PDPageContentStream(document, page2);
        //PDPageContentStream contentTable = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(180, 725);
        content.showText("New Friends Log");
        content.newLine();
        content.endText();

        final int rows = friends.size() + 1;
        final int cols = 3;
        final float rowHeight = 20f;
        final float tableWidth = page2.getMediaBox().getWidth() - (2 * 50);
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth /(float) cols;
        final float cellMargin = 5f;

        float nexty = 650 ;
        for (int i = 0; i <= rows; i++) {
            content.drawLine(50 ,nexty,50 + tableWidth,nexty);
            nexty-= rowHeight;
        }

        float nextx = 50;
        for (int i = 0; i <= cols; i++) {
            content.drawLine(nextx,650,nextx,650-tableHeight);
            nextx += colWidth;
        }

        content.setFont(PDType1Font.HELVETICA_BOLD,16);

        float textx = 50+cellMargin;
        float texty = 650-15;
        String headerText = "FirstName";
        content.beginText();
        content.moveTextPositionByAmount(textx,texty);
        content.drawString(headerText);
        content.endText();
        textx += colWidth;

        String headerText2 = "LastName";
        content.beginText();
        content.moveTextPositionByAmount(textx,texty);
        content.drawString(headerText2);
        content.endText();
        textx += colWidth;

        String headerText3 = "Friends Since";
        content.beginText();
        content.moveTextPositionByAmount(textx,texty);
        content.drawString(headerText3);
        content.endText();
        textx += colWidth;

        texty-=rowHeight;
        textx = 50+cellMargin;


        if(friends.size() > 0) {
            content.setFont(PDType1Font.HELVETICA,8);
            for (int i = 0; i < friends.size(); i++) {
                List<String> text = new ArrayList<>();
                text.add(friends.get(i).getSecondUser().getFirstName());
                text.add(friends.get(i).getSecondUser().getLastName());
                if(friends.get(i).getDate().toLocalTime().getHour() > 9 && friends.get(i).getDate().toLocalTime().getMinute() > 9)
                    text.add(friends.get(i).getDate().toLocalDate().toString()+ "  " +friends.get(i).getDate().toLocalTime().getHour()+ ":" +friends.get(i).getDate().toLocalTime().getMinute());
                else
                if(friends.get(i).getDate().toLocalTime().getHour() > 9)
                    text.add(friends.get(i).getDate().toLocalDate().toString()+ "  " +friends.get(i).getDate().toLocalTime().getHour()+ ":0" +friends.get(i).getDate().toLocalTime().getMinute());
                else
                if(friends.get(i).getDate().toLocalTime().getMinute() > 9)
                    text.add(friends.get(i).getDate().toLocalDate().toString()+ "  0" +friends.get(i).getDate().toLocalTime().getHour()+ ":" +friends.get(i).getDate().toLocalTime().getMinute());
                else
                    text.add(friends.get(i).getDate().toLocalDate().toString()+ "  0" +friends.get(i).getDate().toLocalTime().getHour()+ ":0" +friends.get(i).getDate().toLocalTime().getMinute());
                for (int j = 0; j < cols; j++) {
                    content.beginText();
                    content.moveTextPositionByAmount(textx, texty);
                    content.drawString(text.get(j));
                    content.endText();
                    textx += colWidth;
                }
                texty -= rowHeight;
                textx = 50 + cellMargin;
            }
        }

        content.close();



        PDPage page3 = new PDPage();
        document.addPage(page3);
        content = new PDPageContentStream(document, page3);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(150, 725);
        content.showText("Received Messages Log");
        content.newLine();
        content.endText();

        final int rowsM = messageList.size() + 1;
        final int colM = 4;
        final float rowHeightM = 20f;
        final float tableWidthM = page2.getMediaBox().getWidth() - (2 * 50);
        final float tableHeightM = rowHeightM * rowsM;
        final float colWidthM = tableWidthM /(float) colM;
        final float cellMarginM = 5f;

        float nextyM = 650 ;
        for (int i = 0; i <= rowsM; i++) {
            content.drawLine(50 ,nextyM,50 + tableWidthM,nextyM);
            nextyM-= rowHeightM;
        }

        float nextxM = 50;
        for (int i = 0; i <= colM; i++) {
            content.drawLine(nextxM,650,nextxM,650-tableHeightM);
            nextxM += colWidthM;
        }

        content.setFont(PDType1Font.HELVETICA_BOLD,16);

        float textxM = 50+cellMarginM;
        float textyM = 650-15;
        String headerTextM = "FirstName";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerTextM);
        content.endText();
        textxM += colWidthM;

        String headerText2M = "LastName";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText2M);
        content.endText();
        textxM += colWidthM;

        String headerText3M = "Message";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText3M);
        content.endText();
        textxM += colWidthM;

        String headerText4M = "Date Received";
        content.beginText();
        content.moveTextPositionByAmount(textxM,textyM);
        content.drawString(headerText4M);
        content.endText();
        textxM += colWidthM;

        textyM-=rowHeightM;
        textxM = 50+cellMarginM;


        if(messageList.size() > 0) {
            content.setFont(PDType1Font.HELVETICA,8);
            for (int i = 0; i < messageList.size(); i++) {
                List<String> text = new ArrayList<>();
                text.add(messageList.get(i).getFromUser().getFirstName());
                text.add(messageList.get(i).getFromUser().getLastName());
                text.add(messageList.get(i).getMessage());
                if(messageList.get(i).getDate().toLocalTime().getHour() > 9 && messageList.get(i).getDate().toLocalTime().getMinute() > 9)
                    text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  " +messageList.get(i).getDate().toLocalTime().getHour()+ ":" +messageList.get(i).getDate().toLocalTime().getMinute());
                else
                if(messageList.get(i).getDate().toLocalTime().getHour() > 9)
                    text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  " +messageList.get(i).getDate().toLocalTime().getHour()+ ":0" +messageList.get(i).getDate().toLocalTime().getMinute());
                else
                if(messageList.get(i).getDate().toLocalTime().getMinute() > 9)
                    text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  0" +messageList.get(i).getDate().toLocalTime().getHour()+ ":" +messageList.get(i).getDate().toLocalTime().getMinute());
                else
                    text.add(messageList.get(i).getDate().toLocalDate().toString()+ "  0" +messageList.get(i).getDate().toLocalTime().getHour()+ ":0" +messageList.get(i).getDate().toLocalTime().getMinute());
                for (int j = 0; j < colM; j++) {
                    content.beginText();
                    content.moveTextPositionByAmount(textxM, textyM);
                    content.drawString(text.get(j));
                    content.endText();
                    textxM += colWidthM;
                }
                textyM -= rowHeightM;
                textxM = 50 + cellMarginM;
            }
        }

        content.close();
        document.save(name);
        document.close();
    }
}
