package com.stabilise.character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.files.FileHandle;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.stabilise.core.Constants;
import com.stabilise.core.Resources;
import com.stabilise.item.BoundedContainer;
import com.stabilise.item.Container;
import com.stabilise.item.armour.Armour;
import com.stabilise.item.weapon.Weapon;
import com.stabilise.util.Log;
import com.stabilise.util.annotation.Incomplete;
import com.stabilise.util.io.IOUtil;
import com.stabilise.util.io.data.Compression;
import com.stabilise.util.io.data.DataCompound;
import com.stabilise.util.io.data.Exportable;
import com.stabilise.util.io.data.Format;
import com.stabilise.world.dimension.Dimension;

/**
 * Data about a character.
 */
@Incomplete
public class CharacterData implements Exportable {
    
    //--------------------==========--------------------
    //-----=====Static Constants and Variables=====-----
    //--------------------==========--------------------
    
    /** The file name of the character data file. */
    public static final String FILE_DATA = "data";
    /** The name of the directory holding a player's private world. */
    public static final String DIR_PRIVATE_WORLD = "playerDim/";
    
    //--------------------==========--------------------
    //------------=====Member Variables=====------------
    //--------------------==========--------------------
    
    /** The character's "file system name". This is usually the same as their
     * actual name. */
    public String fileSystemName;
    /** The character's creation date. */
    public long creationDate;
    /** The date at which the character was last played. */
    public long lastPlayed;
    
    /** The character's name. */
    public String name;
    /** The character's hash, which is used to distinguish characters with the
     * same name. This is stored as a string as it is used as the name of NBT
     * tags. */
    public String hash;
    
    /** The character's max health. */
    public int maxHealth;
    /** The character's max mana. */
    public int maxMana;
    
    /** The character's inventory. */
    public final Container inventory =
            new BoundedContainer(Constants.INVENTORY_CAPACITY);
    
    /** Equipped armour. */
    public final Armour[] armour = new Armour[4];
    /** Equipped weapon. */
    public Weapon weapon;
    
    
    private boolean loaded = false;
    
    
    
    /**
     * Creates a blank CharacterData.
     */
    public CharacterData() {}
    
    /**
     * Creates a new CharacterData.
     * 
     * @param characterName The character's name. This serves as both the file
     * system name and the aesthetic name by default.
     * 
     * @throws NullPointerException if {@code characterName} is {@code null}.
     * @throws IllegalArgumentException if {@code characterName} is empty.
     */
    public CharacterData(String characterName) {
        if(characterName.equals("")) // throws NPE
            throw new IllegalArgumentException("characterName is empty");
        
        fileSystemName = characterName;
        name = characterName;
    }
    
    /**
     * Confirms the creation of the character by setting {@code creationDate}
     * to that of the current time, and generating the character's hash.
     */
    public void create() {
        creationDate = System.currentTimeMillis();
        lastPlayed = creationDate;
        
        genHash();
    }
    
    /**
     * Generates a hash for this character.
     */
    private void genHash() {
        // Hash based on a bunch of things hopefully unique to the user to
        // minimise hash collisions.
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher()
               .putLong(creationDate)
               .putLong(System.nanoTime())
               .putLong(System.currentTimeMillis())
               .putString(System.getProperty("java.version"), Charsets.UTF_8)
               .putString(System.getProperty("os.name"), Charsets.UTF_8)
               .putString(System.getProperty("os.version"), Charsets.UTF_8)
               .putString(System.getProperty("user.name"), Charsets.UTF_8)
               .putLong(ThreadLocalRandom.current().nextLong()) // sensitive to prior user actions
               .hash();
        
        hash = hc.toString();
    }
    
    /**
     * Loads the character data.
     * 
     * <p>If this data has already been loaded (i.e. this method has returned
     * without throwing an exception), this method does nothing.
     * 
     * @throws IOException if an I/O error occurs.
     */
    public void load() throws IOException {
        if(loaded)
            return;
        
        importFromCompound(IOUtil.read(getFile(), Format.NBT, Compression.GZIP));
        
        loaded = true;
    }
    
    /**
     * Saves the character data.
     * 
     * @throws IOException if an I/O exception is encountered while attempting
     * to save the character data.
     */
    public void save() throws IOException {
        lastPlayed = new Date().getTime();
        
        DataCompound data = Format.NBT.newCompound();
        exportToCompound(data);
        
        IOUtil.writeSafe(getFile(), data, Compression.GZIP);
    }
    
    @Override
    public void importFromCompound(DataCompound o) {
        hash = o.getString("hash");
        name = o.getString("name");
        maxHealth = o.getI32("maxHealth");
        maxMana = o.getI32("maxMana");
        
        inventory.importFromCompound(o.getCompound("inventory"));
    }
    
    @Override
    public void exportToCompound(DataCompound o) {
        o.put("hash", hash);
        o.put("name", name);
        o.put("maxHealth", maxHealth);
        o.put("maxMana", maxMana);
        
        inventory.exportToCompound(o.childCompound("inventory"));
    }
    
    /**
     * Gets this character's filesystem directory.
     */
    public FileHandle getDir() {
        return getCharacterDir(fileSystemName);
    }
    
    /**
     * Gets this character data's filesystem handle.
     */
    public FileHandle getFile() {
        return getDir().child(FILE_DATA);
    }
    
    /**
     * Gets this character's private dimension's filesystem directory.
     */
    public FileHandle getDimensionDir() {
        return getDir().child(DIR_PRIVATE_WORLD);
    }
    
    /**
     * Gets the internal dimension name for this character's personal
     * dimension.
     */
    public String getDimensionName() {
        return Dimension.getDimensionName(this);
    }
    
    @Override
    public int hashCode() {
        return hash != null ? hash.hashCode() : 0;
    }
    
    public String toString() {
        return (name != null && hash != null) ?
                "CharacterData[\"" + name + "\"," + hash + "]" :
                "CharacterData[Uninitialised]";
    }
    
    //--------------------==========--------------------
    //------------=====Static Functions=====------------
    //--------------------==========--------------------
    
    /**
     * Gets the list of created characters. The returned array is sorted in
     * descending order of the time and date at which the character was last
     * used, such that the most recently used character is at the head of the
     * array.
     * 
     * @return An array of created characters.
     */
    public static CharacterData[] getCharactersList() {
        IOUtil.createDir(Resources.DIR_CHARS);
        FileHandle[] characterDirs = Resources.DIR_CHARS.list();
        
        List<CharacterData> characters = new ArrayList<>(characterDirs.length);
        
        // Cycle over all the folders in the characters directory and determine
        // their validity.
        for(FileHandle charDir : characterDirs) {
            CharacterData character = new CharacterData(charDir.name());
            try {
                character.load();
                characters.add(character);
            } catch(IOException e) {
                Log.get().postSevere("Could not load character data for character \""
                        + charDir.name() + "\"!");
            }
        }
        
        // Now, we convert the List to a conventional array
        CharacterData[] characterArr = characters.toArray(new CharacterData[0]);
        
        // Sort the characters using Java's built-in Comparable interface
        Arrays.sort(characterArr);
        
        return characterArr;
    }
    
    /**
     * Gets a character's directory.
     * 
     * @param characterName The character's file system name.
     * 
     * @return The file representing the character's directory on the file 
     * system.
     * @throws NullPointerException if {@code characterName} is {@code null}.
     * @throws IllegalArgumentException if {@code characterName} is empty.
     */
    public static FileHandle getCharacterDir(String characterName) {
        if(characterName.length() == 0)
            throw new IllegalArgumentException("characterName is empty");
        return Resources.DIR_CHARS.child(IOUtil.getLegalString(characterName) + "/");
    }
    
    /**
     * Gets the data for the default 'Player' character.
     * 
     * @return The default character's data.
     */
    public static CharacterData defaultCharacter() {
        CharacterData data = new CharacterData("Player");
        data.hash = "";
        data.loaded = true;
        return data;
    }
    
}
