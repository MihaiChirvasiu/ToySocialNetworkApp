package com.example.toysocialnetwork.Domain;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;

public class PDFReport {

    private String name;

    public PDFReport(String name){
        this.name = name;
    }

    public void writeToFile(String tile, List<Message> messageList) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(25, 725);
        content.showText(tile);
        content.newLine();
        content.setFont(PDType1Font.HELVETICA, 22);
        for (int i = 0; i < messageList.size(); i++) {
            content.showText(messageList.get(i).getFromUser().getEmail() + ": " + messageList.get(i).getMessage());
            content.newLine();
        }
        content.endText();
        content.close();
        document.save(name);
        document.close();
    }

    public void writeToFileFriends(String title, List<Message> messageList, List<Friendship> friends) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 30);
        content.setLeading(15);
        content.newLineAtOffset(25, 725);
        content.showText(title);
        content.newLine();
        content.setFont(PDType1Font.HELVETICA, 22);
        for (int i = 0; i < messageList.size(); i++) {
            content.showText(messageList.get(i).getFromUser().getEmail() + ": " + messageList.get(i).getMessage());
            content.newLine();
        }
        for(int i = 0; i < friends.size(); i++){
            content.showText(friends.get(i).getSecondUser().getEmail());
            content.newLine();
        }
        content.endText();
        content.close();
        document.save(name);
        document.close();
    }
}
