package com.hospital.assistant.model;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public interface Intents {
    static Map<String, IntentDto> getIntentsMap() {
        IntentDto emergencyIntent = new IntentDto("A patient from room 1 calls for an emergency",
                                                  Role.DOCTOR,
                                                  Priority.HIGH);
        IntentDto temperatureIntent = new IntentDto("A patient from room 1 needs their temperature measured",
                                                    Role.NURSE,
                                                    Priority.MODERATE);
        IntentDto hurtsIntent = new IntentDto("A patient from room 1 wants painkillers", Role.NURSE, Priority.HIGH);
        IntentDto bandageIntent = new IntentDto("A patient from room 1 wants their bandage taken care of",
                                                Role.NURSE,
                                                Priority.MODERATE);
        IntentDto catheterIntent = new IntentDto("A patient from room 1 wants their catheter taken care of",
                                                 Role.NURSE,
                                                 Priority.MODERATE);
        IntentDto bloodPressureIntent = new IntentDto("A patient from room 1 wants their blood pressure measured",
                                                      Role.NURSE,
                                                      Priority.MODERATE);
        IntentDto wetUnderHandIntent = new IntentDto("A patient from room 1 reports it's getting wet under their hand",
                                                     Role.NURSE,
                                                     Priority.MODERATE);
        IntentDto medicalDripBagIntent = new IntentDto(
            "A patient from room 1 is having troubles with their medical drip bag",
            Role.NURSE,
            Priority.HIGH);
        IntentDto peeIntent = new IntentDto("A patient from room 1 needs assistance to pee",
                                            Role.SANITARY,
                                            Priority.HIGH);
        IntentDto poopIntent = new IntentDto("A patient from room 1 needs assistance to poop",
                                             Role.SANITARY,
                                             Priority.MODERATE);
        IntentDto diaperIntent = new IntentDto("A patient from room 1 wants their diaper changed",
                                               Role.SANITARY,
                                               Priority.HIGH);
        IntentDto utilityIntent = new IntentDto("A patient from room 1 needs assistance with an utility",
                                                Role.SANITARY,
                                                Priority.LOW);

        return ImmutableMap.<String, IntentDto>builder()
            .put("BandageIntent", bandageIntent)
            .put("BloodPressureIntent", bloodPressureIntent)
            .put("CatheterIntent", catheterIntent)
            .put("DiaperIntent", diaperIntent)
            .put("EmergencyIntent", emergencyIntent)
            .put("HurtsIntent", hurtsIntent)
            .put("MedicalDripBagIntent", medicalDripBagIntent)
            .put("PeeIntent", peeIntent)
            .put("PoopIntent", poopIntent)
            .put("TemperatureIntent", temperatureIntent)
            .put("UtilityIntent", utilityIntent)
            .put("WetUnderHandIntent", wetUnderHandIntent)
            .build();
    }
}
