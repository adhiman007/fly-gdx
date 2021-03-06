package de.fau.cs.mad.fly.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Level group with a specific name and a list of levels which belong to the
 * level group.
 * 
 * @author Tobi
 * 
 */
public class LevelGroup {
    private int moneyFactor = 1;
    public int id;
    public String name;
    private List<LevelProfile> levels;
    public String path;
    
    /**
     * Comparator for the level heads.
     */
    private Comparator<LevelProfile> levelComparator = new Comparator<LevelProfile>() {
        @Override
        public int compare(LevelProfile first, LevelProfile second) {
            if (first.id < second.id) {
                return -1;
            } else if (first.id > second.id) {
                return 1;
            }
            return 0;
        }
    };
    
    public List<LevelProfile> getLevels() {
        if (levels == null) {
            readLevelGroup();
        }
        return levels;
    }
    
    public LevelProfile getFirstLevel() {
        return getLevels().get(0);
    }
    
    public LevelProfile getLevelProfile(int id) {
        for (LevelProfile lp : getLevels()) {
            if (lp.id == id)
                return lp;
        }
        return null;
    }
    
    public LevelProfile getLastLevelProfile() {
        if (getLevels().size() > 0) {
            return getLevels().get(getLevels().size() - 1);
        } else {
            return getLevels().get(getLevels().size());
        }
    }
    
    private void readLevelGroup() {
        levels = new ArrayList<LevelProfile>();
        // check for all the levels
        JsonReader reader = new JsonReader();
        
        FileHandle dirHandle = Gdx.files.internal(path);
        FileHandle handle = dirHandle.child("group.json");
        if (handle != null) {
            JsonValue json = reader.parse(handle);
            if (json.has("moneyFactor"))
                moneyFactor = json.getInt("moneyFactor");
            JsonValue groups = json.get("levels");
            if (groups != null) {
                for (int i = 0; i < groups.size; i++) {
                    LevelProfile levelProfile = new LevelProfile();
                    JsonValue groupJS = groups.get(i);
                    levelProfile.id = groupJS.getInt("id");
                    levelProfile.name = groupJS.getString("name");
                    levelProfile.file = path + groupJS.getString("file");
                    JsonValue type = groupJS.get("type");
                    if (type == null) {
                        levelProfile.type = 0;
                    } else if (type.asString().equals("tutorial")) {
                        levelProfile.type = 1;
                    }
                    levels.add(levelProfile);
                }
            }
        }
        
        Collections.sort(levels, levelComparator);
    }
    
    public String getLevelName(int levelID) {
        for (LevelProfile level : getLevels()) {
            if (level.id == levelID) {
                return level.name;
            }
        }
        return Integer.toString(levelID);
    }
    
    public double getMoneyFactor() {
        return Math.sqrt(this.moneyFactor);
    }
    
}