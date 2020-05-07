package fr.univtours.info.simpleStory;

import fr.univtours.info.model.discursal.Act;
import fr.univtours.info.model.discursal.Episode;
import fr.univtours.info.model.presentational.*;
import fr.univtours.info.model.discursal.Story;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

public class SimpleVisualStory implements VisualStory {
    Collection<Dashboard> theDashboards;
    Story theStory;
    String theRendering="";
    PDDocument thePDF;

    public SimpleVisualStory(){
        theDashboards = new ArrayList<Dashboard>();
    }

    public PDDocument getThePDF(){
        return thePDF;
    }

    @Override
    public void print() {

        //System.out.println(theRendering);
    }

    @Override
    public void renders(Story aStory) {
        // this is where the visual story creates its component before rendering
        theStory=aStory;

        for(Act act : theStory.includes()){
            Dashboard d = new SimpleDashboard();
            this.contains(d);
            d.renders(act);


            for(Episode ep : act.includes()){
                if(ep.getClass().toString().compareTo("SimpleGraphicEpisode")==0){
                    DashboardComponent dbc = new DescribeDashboardComponent();
                    d.contains(dbc);
                    dbc.renders(ep);
                }else{
                    DashboardComponent dbc = new SimpleDashboardComponent();
                    d.contains(dbc);
                    dbc.renders(ep);
                }

            }

            //d.renders(act);
        }

    }

    @Override
    public void contains(Dashboard aDashboard) {
        theDashboards.add(aDashboard);
    }

    @Override
    public Collection<Dashboard> contains() {
        return theDashboards;
    }

    @Override
    public Story renders() {
        theRendering= "This is the story for goal: " + theStory.has().toString() + "\n";
        for(Dashboard d : theDashboards){ // I'm here
            d.renders();
            theRendering=theRendering + ((SimpleDashboard) d).getRendering() + "\n";
        }
        try {
            PDDocument pdf = createPDF();
            this.thePDF=pdf;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return theStory;
    }


    public PDDocument createPDF() throws IOException {
        //Creating PDF document object
        PDDocument document = new PDDocument();

        System.out.println("PDF created");

        for (int i=0; i<1; i++) {
            //Creating a blank page
            PDPage blankPage = new PDPage();

            //Adding the blank page to the document
            document.addPage( blankPage );

            PDPageContentStream contentStream = new PDPageContentStream(document, blankPage);
            contentStream.beginText();
            //String text=new String(theRendering);
            String text=theRendering;


            //Setting the leading
            contentStream.setLeading(14.5f);

            //Setting the position for the line
            contentStream.newLineAtOffset(25, 725);

            String[] toPrint = text.split("\n");

            // prints title (goal)
            contentStream.setFont(PDType1Font.TIMES_BOLD, 12);
            contentStream.showText(toPrint[0]);
            contentStream.newLine();
            contentStream.newLine();

            contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
            for (int x=1; x<toPrint.length; x++){

               if(toPrint[x].startsWith("Act")){
                   contentStream.setFont(PDType1Font.TIMES_BOLD_ITALIC, 11);
                   contentStream.showText(toPrint[x]);
                   contentStream.newLine();
                   contentStream.newLine();

               }else {
                   if(toPrint[x].startsWith("data")) {

                           int index = toPrint[x].indexOf(',');

                           if (index != -1) toPrint[x] = toPrint[x].substring(index + 1);

                           byte[] bytes = Base64.getDecoder().decode(new String(toPrint[x]).getBytes("UTF-8"));

                       PDImageXObject pdImage = PDImageXObject.createFromByteArray(document,bytes,"insight");
                       contentStream.drawImage(pdImage, 70, 250);
                   }
                else{
                       contentStream.setFont(PDType1Font.TIMES_ROMAN, 10);
                       contentStream.showText(toPrint[x]);
                       contentStream.newLine();
                   }

               }
            }


            contentStream.endText();
            System.out.println("Content added");
            contentStream.close();

        }


        //Closing the document
        //document.close();

        /*
        PDPage my_page = new PDPage();
        document.addPage(my_page);

        PDPageContentStream contentStream = new PDPageContentStream(document, my_page);
        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

        //Setting the position for the line
        contentStream.newLineAtOffset(25, 500);



        //Adding text in the form of string
        contentStream.showText(theRendering);

        //Ending the content stream
        contentStream.endText();

        System.out.println("Content added");

        //Closing the content stream
        contentStream.close();


         */
        //Saving the document
        document.save("/Users/marcel/Documents/RECHERCHE/STUDENTS/Faten/pocdatastory/public/pdfs/test.pdf");

        //Closing the document
        document.close();
        return document;
    }



}
