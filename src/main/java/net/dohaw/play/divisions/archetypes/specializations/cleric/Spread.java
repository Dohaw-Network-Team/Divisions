package net.dohaw.play.divisions.archetypes.specializations.cleric;

import net.dohaw.play.divisions.Stat;
import net.dohaw.play.divisions.archetypes.specializations.Speciality;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityKey;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityWrapper;

import java.util.List;
import java.util.Map;

public class Spread extends SpecialityWrapper {

    public Spread(SpecialityKey KEY) {
        super(KEY);
    }

    @Override
    public List<String> getAliases() {
        return null;
    }

    @Override
    public Map<Stat, Double> getMaxStats() {
        return null;
    }
}