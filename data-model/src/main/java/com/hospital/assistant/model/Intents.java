package com.hospital.assistant.model;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Intents {
    private Intents() {

    }

    public static Map<String, Pair<String, Role>> getIntentsMap() {
        Map<String, Pair<String, Role>> intentsMap = new HashMap<>();

        Pair<String, Role> emergencyIntent = new Pair<>("A patient from room 1 calls for an emergency", Role.DOCTOR);
        Pair<String, Role> temperatureIntent = new Pair<>("A patient from room 1 needs their temperature measured", Role.NURSE);
        Pair<String, Role> hurtsIntent = new Pair<>("A patient from room 1 wants painkillers", Role.NURSE);
        Pair<String, Role> bandageIntent = new Pair<>("A patient from room 1 wants their bandage taken care of", Role.NURSE);
        Pair<String, Role> catheterIntent = new Pair<>("A patient from room 1 wants their catheter taken care of", Role.NURSE);
        Pair<String, Role> bloodPressureIntent = new Pair<>("A patient from room 1 wants their blood pressure measured", Role.NURSE);
        Pair<String, Role> wetUnderHandIntent = new Pair<>("A patient from room 1 reports it's getting wet under their hand", Role.NURSE);
        Pair<String, Role> medicalDripBagIntent = new Pair<>("A patient from room 1 is having troubles with their medical drip bag", Role.NURSE);
        Pair<String, Role> peeIntent = new Pair<>("A patient from room 1 needs assistance to pee", Role.SANITARY);
        Pair<String, Role> poopIntent = new Pair<>("A patient from room 1 needs assistance to poop", Role.SANITARY);
        Pair<String, Role> diaperIntent = new Pair<>("A patient from room 1 wants their diaper changed", Role.SANITARY);
        Pair<String, Role> somethingFellIntent = new Pair<>("A patient from room 1 needs assistance to poop", Role.SANITARY);
        Pair<String, Role> utilityIntent = new Pair<>("A patient from room 1 needs assistance with an utility", Role.SANITARY);

        intentsMap.put("BandageIntent", bandageIntent);
        intentsMap.put("BloodPressureIntent", bloodPressureIntent);
        intentsMap.put("CatheterIntent", catheterIntent);
        intentsMap.put("DiaperIntent", diaperIntent);
        intentsMap.put("EmergencyIntent", emergencyIntent);
        intentsMap.put("HurtsIntent", hurtsIntent);
        intentsMap.put("MedicalDripBagIntent", medicalDripBagIntent);
        intentsMap.put("PeeIntent", peeIntent);
        intentsMap.put("PoopIntent", poopIntent);
        intentsMap.put("SomethingFellIntent", somethingFellIntent);
        intentsMap.put("TemperatureIntent", temperatureIntent);
        intentsMap.put("UtilityIntent", utilityIntent);
        intentsMap.put("WetUnderHandIntent", wetUnderHandIntent);

        return intentsMap;
    }
}
