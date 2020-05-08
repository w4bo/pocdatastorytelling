package fr.univtours.info.simpleStory;

import fr.univtours.info.model.Structural.Act;
import fr.univtours.info.model.Structural.Episode;
import fr.univtours.info.model.factual.Collector;
import fr.univtours.info.model.factual.Exploration;
import fr.univtours.info.model.factual.Finding;
import fr.univtours.info.model.intentional.*;
import fr.univtours.info.model.intentional.Character;
import fr.univtours.info.model.presentational.VisualStory;
import fr.univtours.info.model.Structural.Story;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class StoryCreator {

    Story theStory;
    Goal theGoal;
    Exploration theExploration;

    AnalyticalQuestion currentQuestion;
    Collector currentCollector;
    Message currentMessage;
    Episode currentEpisode;
    Measure currentMeasure;
    Act currentAct;
    //ArrayList<Protagonist> currentProtagonists;

    Collection<Finding> currentAnswer;
    String currentGraphicString;
    byte[] currentGraphic;
    boolean currentInsightIsGraphic=false;

    PDDocument thePDF;
    File thePDFfile;

    public StoryCreator(){

    }

    public Goal getGoal(){
        return theGoal;
    }

    public AnalyticalQuestion getCurrentQuestion() {
        return currentQuestion;
    }

    public Message getCurrentMessage() {
        return currentMessage;
    }

    public Measure getCurrentMeasure(){
        return currentMeasure;
    }

    public Act getCurrentAct(){
        return currentAct;
    }

    public PDDocument getThePDF(){
        return thePDF;
    }

    public File getThePDFfile(){
        //return thePDFfile;
        return new File("/Users/marcel/Desktop/test.pdf");
    }

    public String newGoal(String aGoal){
        theGoal = new SimpleGoal();
        theGoal.addText(aGoal);

        theStory = new SimpleStory();
        theStory.has(theGoal);
        theGoal.has(theStory);
        theExploration = new SimpleExploration(); // only one exploration
        theGoal.solves(theExploration);
        //currentProtagonists=new ArrayList<Protagonist>();
        return aGoal;
    }


    public String newQuestion(String question){
        AnalyticalQuestion anAnalyticalQuestion = new SimpleAnalyticalQuestion();
        theGoal.poses(anAnalyticalQuestion);
        anAnalyticalQuestion.poses(theGoal);
        anAnalyticalQuestion.addText(question);

        currentQuestion=anAnalyticalQuestion;

        return question;
    }



    public String newDescribeCollector(String query) {

        Collector c = new SimpleDescribeCollector(query);
        currentQuestion.implement(c);
        theExploration.tries(c);
        currentCollector=c;


        Collection<Finding> col =c.fetches();
        currentAnswer = col;
        /*
        Iterator<Insight> it= col.iterator();
        String res="";

        while(it.hasNext()){
            Insight i=it.next();
            //currentObservation.produces(i);
            res=res+i.toString();
        }

        return res;
        */
         return query;

    }

    public void addDescribeInsight(byte[] describeResult, String base64){
        currentCollector.fetches(new SimpleDescribeFinding(describeResult, base64));
        currentGraphic=describeResult;
        currentGraphicString=base64;
        currentInsightIsGraphic=true;
    }


    public String newSQLCollector(String query){

        Collector c = new SimpleSQLCollector(query);
        currentQuestion.implement(c);
        theExploration.tries(c);
        currentCollector=c;

        try {
            c.run();
        }

        catch(Exception e){
            e.printStackTrace();
        }

        currentInsightIsGraphic=false;

        Collection<Finding> col =c.fetches();
        currentAnswer = col;

        Iterator<Finding> it= col.iterator();
        String res="";

        while(it.hasNext()){
            Finding i=it.next();
            //currentObservation.produces(i);
            res=res+i.toString();
        }

        return res;
    }


    public String newMessage(String theMessage){

        Message o = new SimpleMessage();
        o.addText(theMessage);
        currentMessage =o;
        currentQuestion.generates(currentMessage);
        currentMessage.generates(currentQuestion);

        Iterator<Finding> it= currentAnswer.iterator();
        String res="";

        while(it.hasNext()){
            Finding i=it.next();
            currentMessage.produces(i);
            res=res+i.toString();
        }


        return theMessage;
    }


    public String newCharacter(String theCharacter){
        Character p = new SimpleCharacter();
        currentMessage.bringsOut(p);
        p.addText(theCharacter);
        return theCharacter;
    }

    public String newAct(String theAct){
        currentAct=new SimpleAct();
        theStory.includes(currentAct);

        //currentAct.narrates(currentMessage);

        currentAct.addText(theAct);

        return(theAct);
    }


    public String newMeasure(String theMeasure){
        currentMeasure =new SimpleMeasure();
        currentMeasure.addText(theMeasure);
        currentMessage.includes(currentMeasure);

        return(theMeasure);
    }



    public String newEpisode(String theEpisode){
        //if(currentInsightIsGraphic){
        //    currentEpisode=new SimpleGraphicEpisode();
        //    ((SimpleGraphicEpisode) currentEpisode).setGraphic(currentGraphic);
        //    ((SimpleGraphicEpisode) currentEpisode).setStringGraphic(currentGraphicString);
        //}else{
            currentEpisode=new SimpleEpisode();
        //}

        // attaches current message characters
        currentAct.includes(currentEpisode);
        for(Character p : currentMessage.bringsOut()){
            currentEpisode.playsIn(p);
        }
        // attaches current message measures
        for(Measure m : currentMessage.includes()){
            currentEpisode.refersTo(m);
        }


        currentEpisode.narrates(currentMessage);
        currentEpisode.addText(theEpisode);

        return(theEpisode);
    }




    public String render(String msg){
        VisualStory vs=new SimpleVisualStory();
        vs.renders(theStory);  // just attach
        vs.renders(); // and then renders
        thePDF=((SimpleVisualStory) vs).getThePDF();

        //vs.print(); // and then prints
        return msg;
    }




    public static void main(String args[]) throws Exception{

        // I was only for testing purpose, please drop me

        Story theStory=new SimpleStory();
        Goal theGoal = new SimpleGoal();
        //String goalText="a straight story";
        //theGoal.addText(goalText);
        theStory.has(theGoal);

        Act currentAct =null;
        Measure currentMeasure =null;

        Exploration theExploration = new SimpleExploration(); // only one exploration
        theGoal.solves(theExploration);

        boolean stop=false;

        while(!stop){
            // ask SQL query
            String query="select sum(lo_revenue), d_year, p_brand \n" +
                    "from lineorder, date, part, supplier \n" +
                    " where lo_orderdate = d_datekey \n" +
                    " and lo_partkey = p_partkey \n" +
                    " and lo_suppkey = s_suppkey \n" +
                    " and p_category = 'MFGR#12' \n" +
                    " and s_region = 'AMERICA' \n" +
                    " group by d_year, p_brand \n" +
                    " order by d_year, p_brand;\n"; //fake
            AnalyticalQuestion anAnalyticalQuestion = new SimpleAnalyticalQuestion();
            anAnalyticalQuestion.addText(query);
            Collection<Finding> col = ((SimpleAnalyticalQuestion) anAnalyticalQuestion).answer();
            theGoal.poses(anAnalyticalQuestion);

            // show insights and ask if worthy
            boolean isWorthy=true; //Fake
            if(isWorthy){
                // ask if new act
                boolean newAct=true; //Fake
                if(newAct){
                    currentAct=new SimpleAct();
                    theStory.includes(currentAct);
                    currentMeasure = new SimpleMeasure();

                    // ask for text
                }

                Message currentMessage = new SimpleMessage();
                anAnalyticalQuestion.generates(currentMessage);
                currentMessage.generates(anAnalyticalQuestion);

                //currentMessage.bringsOut(currentObservation);
                //currentObservation.addText(anAnalyticalQuestion.toString());

                for(Finding i : col){
                    // may ask the author if insight is accepted or not
                    currentMessage.produces(i);

                }



                Episode currentEpisode= new SimpleEpisode();
                currentEpisode.narrates(currentMessage);
                currentAct.includes(currentEpisode);
               // currentMeasure.bringsOut(currentMessage);
                currentAct.narrates(currentMeasure);

                // how many protagonists
                int nbProtagonists = 1; // fake
                for(int i=0;i<nbProtagonists;i++){
                    Character aCharacter = new SimpleCharacter();
                    currentEpisode.playsIn(aCharacter);
                    currentMessage.bringsOut(aCharacter);
                }


            }

            // ask more analytical questions? else stop
            stop=true; //fake
        }

        // ask to complement the story, acts, episodes with texts
        //theStory.addText("ask the author");

        VisualStory vs=new SimpleVisualStory();
        vs.renders(theStory);  // just attach
        vs.renders(); // and then renders
        vs.print(); // and then prints
    }


}
