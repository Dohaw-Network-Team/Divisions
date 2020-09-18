package net.dohaw.play.divisions.archetypes.spells;

import net.dohaw.play.divisions.archetypes.ArchetypeKey;
import net.dohaw.play.divisions.archetypes.ArchetypeWrapper;
import net.dohaw.play.divisions.archetypes.Wrapper;
import net.dohaw.play.divisions.archetypes.WrapperHolder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Spell extends WrapperHolder {

    public static final SpellWrapper INVISIBLE_STRIKE = new InvisibleStrike("", ArchetypeKey.EVOKER, SpellKey.INVISIBLE_STRIKE, 1);
    public static final SpellWrapper SMITE = new Smite("", ArchetypeKey.CLERIC, SpellKey.SMITE, 1);
    public static final SpellWrapper FROST_STRIKE = new FrostStrike("", ArchetypeKey.WIZARD, SpellKey.FROST_STRIKE, 1);

    public static SpellWrapper getSpellByItemKey(String customItemKey){
        for(Map.Entry<Enum, Wrapper> entry : wrappers.entrySet()){
            SpellWrapper spell = (SpellWrapper) entry.getValue();
            if(spell.getCustomItemBindedToKey().equalsIgnoreCase(customItemKey)){
                return spell;
            }
        }
        return null;
    }

    public static List<SpellWrapper> getArchetypeSpells(ArchetypeWrapper archetypeWrapper){
        List<SpellWrapper> spells = new ArrayList<>();
        for(Map.Entry<Enum, Wrapper> entry : wrappers.entrySet()){
            SpellWrapper spell = (SpellWrapper) entry.getValue();
            if(spell.getArchetype() == archetypeWrapper.getKEY()){
                spells.add(spell);
            }
        }
        return spells;
    }

    public static List<SpellWrapper> getArchetypeSpellsUnlocked(ArchetypeWrapper archetype, int level){
        List<SpellWrapper> archetypeSpells = getArchetypeSpells(archetype);
        List<SpellWrapper> spellsUnlocked = new ArrayList<>();
        for(SpellWrapper wrapper : archetypeSpells){
            if(wrapper.getLevelUnlocked() == level){
                spellsUnlocked.add(wrapper);
            }
        }
        return spellsUnlocked;
    }

}
