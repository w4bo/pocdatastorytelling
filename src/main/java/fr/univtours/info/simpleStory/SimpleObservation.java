package fr.univtours.info.simpleStory;

import fr.univtours.info.model.factual.Insight;
import fr.univtours.info.model.intentional.AnalyticalQuestion;
import fr.univtours.info.model.intentional.Observation;
import fr.univtours.info.model.intentional.Protagonist;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleObservation implements Observation {
    String theText;
    Collection<Insight> theInsights;
    Collection<Protagonist> theProtagonists;
    AnalyticalQuestion theAnalyticalQuestion;

    public SimpleObservation(){
        theInsights = new ArrayList<Insight>();
        theProtagonists = new ArrayList<Protagonist>();
    }
    @Override
    public void addText(String aText) {

        this.theText=aText.substring(1,aText.length()-1).replace("\\n","\n");

    }

    @Override
    public String toString() {
        return "Observation: " + theText;
    }

    @Override
    public void poses(AnalyticalQuestion anAnalyticalQuestion) {

    }

    @Override
    public void generates(AnalyticalQuestion anAnalyticalQuestion) {
        theAnalyticalQuestion=anAnalyticalQuestion;
    }

    @Override
    public AnalyticalQuestion generates() {
        return theAnalyticalQuestion;
    }

    @Override
    public void produces(Insight anInsight) {

        theInsights.add(anInsight);

    }

    @Override
    public Collection<Insight> produces() {
        return theInsights;
    }

    @Override
    public void bringsOut(Protagonist aProtagonist) {
        theProtagonists.add(aProtagonist);
    }

    @Override
    public Collection<Protagonist> bringsOut() {
        return theProtagonists;
    }
}
