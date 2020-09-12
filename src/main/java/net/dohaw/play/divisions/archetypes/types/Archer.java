package net.dohaw.play.divisions.archetypes.types;

import net.dohaw.play.divisions.Stat;
import net.dohaw.play.divisions.archetypes.ArchetypeKey;
import net.dohaw.play.divisions.archetypes.ArchetypeWrapper;
import net.dohaw.play.divisions.archetypes.RegenType;
import net.dohaw.play.divisions.archetypes.specializations.Speciality;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityKey;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityWrapper;
import net.dohaw.play.divisions.archetypes.spells.SpellKey;
import net.dohaw.play.divisions.archetypes.spells.SpellWrapper;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Archer extends ArchetypeWrapper {

    public Archer(ArchetypeKey ARCHETYPE) {
        super(ARCHETYPE);
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("archer", "arch", "ar");
    }

    @Override
    public List<String> getDefaultItems() {
        return null;
    }

    @Override
    public Map<SpecialityKey, SpecialityWrapper> getSpecialities() {
        return new HashMap<SpecialityKey, SpecialityWrapper>(){{
            put(SpecialityKey.CLOAK, Speciality.CLOAK);
            put(SpecialityKey.CONTROL, Speciality.CONTROL);
            put(SpecialityKey.DECEPTION, Speciality.DECEPTION);
            put(SpecialityKey.SOUL_PIERCING, Speciality.SOUL_PIERCING);
        }};
    }

    @Override
    public List<Material> getProficientItems() {
        return null;
    }

    @Override
    public Map<SpellKey, SpellWrapper> getSpells() {
        return null;
    }

    @Override
    public Map<Stat, Double> getDefaultStats() {
        return null;
    }

    @Override
    public RegenType getRegenType() {
        return null;
    }


}
