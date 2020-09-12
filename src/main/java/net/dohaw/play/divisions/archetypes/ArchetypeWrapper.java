package net.dohaw.play.divisions.archetypes;

import net.dohaw.play.divisions.Stat;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityKey;
import net.dohaw.play.divisions.archetypes.specializations.SpecialityWrapper;
import net.dohaw.play.divisions.archetypes.spells.SpellKey;
import net.dohaw.play.divisions.archetypes.spells.SpellWrapper;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public abstract class ArchetypeWrapper extends Wrapper{

    public ArchetypeWrapper(ArchetypeKey KEY) {
        super(KEY);
    }

    /*
        Default item string keys. Gets the CustomItem object via the CustomItemManager class
     */
    public abstract List<String> getDefaultItems();

    public abstract Map<Stat, Double> getDefaultStats();

    public abstract Map<SpecialityKey, SpecialityWrapper> getSpecialities();

    public abstract List<Material> getProficientItems();

    public abstract RegenType getRegenType();

    public abstract Map<SpellKey, SpellWrapper> getSpells();

}
