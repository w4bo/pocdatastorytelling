package fr.univtours.info.simpleStory;

import fr.univtours.info.model.discursal.Episode;
import fr.univtours.info.model.intentional.Observation;
import fr.univtours.info.model.intentional.Protagonist;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleEpisode extends BaseEpisode {


    public SimpleEpisode(){

        super();
    }



    @Override
    public String toString() {

        String episodeProtagonists = "";
        for(Protagonist p : theProtagonists){
            episodeProtagonists = episodeProtagonists+ p.toString() + "\n";
        }
        return "Episode: " + theText + "\n" +theObservation.toString() + "\n"
                + episodeProtagonists;
    }

}
