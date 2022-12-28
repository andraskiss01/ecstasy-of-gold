package com.ak17apps.ecstasyofgold.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Generator {
    private static String[] names = {"Seth Bullock", "Rose Dunn", "Jesse James", "Wyatt Earp", "Geronimo",
            "Butch Cassidy", "Wild Bill Hickok", "Annie Oakley", "Buffalo Bill", "Billy the Kid"};

    public static List<String> getNames(){
        Random rnd = new Random();
        List<String> selectedNames = new ArrayList<>();

        while(selectedNames.size() < 10) {
            String randomName = names[rnd.nextInt(10)];

            if(!selectedNames.contains(randomName)){
                selectedNames.add(randomName);
            }
        }

        return selectedNames;
    }

    public static List<Integer> getFiringPowers(){
        Random rnd = new Random();
        List<Integer> selectedPowers = new ArrayList<>();

        while(selectedPowers.size() < 10){
            int randomPower = rnd.nextInt(10) + 1;

            if(!selectedPowers.contains(randomPower)){
                selectedPowers.add(randomPower);
            }
        }

        return selectedPowers;
    }
}
