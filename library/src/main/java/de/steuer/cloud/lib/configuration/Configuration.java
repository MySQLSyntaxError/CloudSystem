package de.steuer.cloud.lib.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.steuer.cloud.lib.utils.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Steuer
 * Created: 13.12.2020
 */

public class Configuration {

    public static final int DETECT = -1;
    public static final int PROPERTIES = 0;
    public static final int CNF = 0;
    public static final int JSON = 1;
    public static final int YAML = 2;
    public static final int ENUM = 5;
    public static final int ENUMERATION = 5;
    public static final Map<String, Integer> format = new TreeMap<>();

    static {
        format.put( "properties", 0 );
        format.put( "con", 0 );
        format.put( "conf", 0 );
        format.put( "Configuration", 0 );
        format.put( "js", 1 );
        format.put( "json", 1 );
        format.put( "yml", 2 );
        format.put( "yaml", 2 );
        format.put( "txt", 5 );
        format.put( "list", 5 );
        format.put( "enum", 5 );
    }

    private final Map<String, Object> nestedCache;
    private ConfigurationSection configurationSection;
    private File file;
    private boolean correct;
    private int type;

    public Configuration( int type ) {
        this.configurationSection = new ConfigurationSection();
        this.nestedCache = new HashMap();
        this.correct = false;
        this.type = -1;
        this.type = type;
        this.correct = true;
        this.configurationSection = new ConfigurationSection();
    }

    public Configuration() {
        this( 2 );
    }

    public Configuration( String file ) {
        this(file, -1 );
    }

    public Configuration(LinkedHashMap file ) {
        this(file.toString(), -1 );
    }

    public Configuration( String file, int type ) {
        this( file, type, new ConfigurationSection() );
    }

    public Configuration( File file, int type ) {
        this( file.toString(), type, new ConfigurationSection() );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Configuration( String file, int type, LinkedHashMap<String, Object> defaultMap ) {
        this.configurationSection = new ConfigurationSection();
        this.nestedCache = new HashMap();
        this.correct = false;
        this.type = -1;
        this.load( file, type, new ConfigurationSection( defaultMap ) );
    }

    public Configuration( String file, int type, ConfigurationSection defaultMap ) {
        this.configurationSection = new ConfigurationSection();
        this.nestedCache = new HashMap();
        this.correct = false;
        this.type = -1;
        this.load( file, type, defaultMap );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Configuration( File file, int type, LinkedHashMap<String, Object> defaultMap ) {
        this( file.toString(), type, new ConfigurationSection( defaultMap ) );
    }

    public void reload() {
        this.configurationSection.clear();
        this.nestedCache.clear();
        this.correct = false;
        if ( this.file == null ) {
            throw new IllegalStateException( "Failed to reload Configuration. File object is undefined." );
        } else {
            this.load( this.file.toString(), this.type );
        }
    }

    public boolean load( String file ) {
        return this.load( file, -1 );
    }

    public boolean load( String file, int type ) {
        return this.load( file, type, new ConfigurationSection() );
    }

    public boolean load( String file, int type, ConfigurationSection defaultMap ) {
        this.correct = true;
        this.type = type;
        this.file = new File( file );
        if ( !this.file.exists() ) {
            try {
                this.file.createNewFile();
            } catch ( IOException e ) {
                System.out.println( "Could not create Configuration " + this.file.toString() + " | " + e );
            }

            this.configurationSection = defaultMap;
            this.save();
        } else {
            String content;
            if ( this.type == -1 ) {
                content = "";
                if ( this.file.getName().lastIndexOf( "." ) != -1 && this.file.getName().lastIndexOf( "." ) != 0 ) {
                    content = this.file.getName().substring( this.file.getName().lastIndexOf( "." ) + 1 );
                }

                if ( format.containsKey( content ) ) {
                    this.type = format.get( content );
                } else {
                    this.correct = false;
                }
            }

            if ( !this.correct ) {
                return false;
            }

            content = "";

            try {
                content = FileUtils.readFile( this.file );
            } catch ( IOException e ) {
                e.printStackTrace();
            }

            this.parseContent( content );
            if ( !this.correct ) {
                return false;
            }

            if ( this.setDefault( defaultMap ) > 0 ) {
                this.save();
            }
        }

        return true;
    }

    public boolean load( InputStream inputStream ) {
        if ( inputStream == null ) {
            return false;
        } else {
            if ( this.correct ) {
                String content;
                try {
                    content = FileUtils.readFile( inputStream );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    return false;
                }

                this.parseContent( content );
            }

            return this.correct;
        }
    }

    public boolean check() {
        return this.correct;
    }

    public boolean isCorrect() {
        return this.correct;
    }

    public boolean save( File file ) {
        this.file = file;
        return this.save();
    }

    public boolean save() {
        if ( this.file == null ) {
            throw new IllegalStateException( "Failed to save Configuration. File object is undefined." );
        } else if ( !this.correct ) {
            return false;
        } else {
            String content = "";
            Map.Entry entry;
            switch ( this.type ) {
                case 0:
                    content = this.writeProperties();
                    break;
                case 1:
                    content = ( new GsonBuilder() ).setPrettyPrinting().create().toJson( this.configurationSection );
                    break;
                case 2:
                    DumperOptions dumperOptions = new DumperOptions();
                    dumperOptions.setDefaultFlowStyle( DumperOptions.FlowStyle.BLOCK );
                    Yaml yaml = new Yaml( dumperOptions );
                    content = yaml.dump( this.configurationSection );
                case 3:
                case 4:
                default:
                    break;
                case 5:
                    for ( Iterator var5 = this.configurationSection.entrySet().iterator(); var5.hasNext(); content = content + entry.getKey() + "\r\n" ) {
                        Object o = var5.next();
                        entry = (Map.Entry) o;
                    }
            }

            try {
                FileUtils.writeFile( this.file, content );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public void set( String key, Object value ) {
        this.configurationSection.set( key, value );
    }

    public Object get( String key ) {
        return this.get( key, null);
    }

    public <T> T get( String key, T defaultValue ) {
        return this.correct ? this.configurationSection.get( key, defaultValue ) : defaultValue;
    }

    public ConfigurationSection getSection( String key ) {
        return this.correct ? this.configurationSection.getSection( key ) : new ConfigurationSection();
    }

    public boolean isSection( String key ) {
        return this.configurationSection.isSection( key );
    }

    public ConfigurationSection getSections( String key ) {
        return this.correct ? this.configurationSection.getSections( key ) : new ConfigurationSection();
    }

    public ConfigurationSection getSections() {
        return this.correct ? this.configurationSection.getSections() : new ConfigurationSection();
    }

    public int getInt( String key ) {
        return this.getInt( key, 0 );
    }

    public int getInt( String key, int defaultValue ) {
        return this.correct ? this.configurationSection.getInt( key, defaultValue ) : defaultValue;
    }

    public boolean isInt( String key ) {
        return this.configurationSection.isInt( key );
    }

    public long getLong( String key ) {
        return this.getLong( key, 0L );
    }

    public long getLong( String key, long defaultValue ) {
        return this.correct ? this.configurationSection.getLong( key, defaultValue ) : defaultValue;
    }

    public boolean isLong( String key ) {
        return this.configurationSection.isLong( key );
    }

    public double getDouble( String key ) {
        return this.getDouble( key, 0.0D );
    }

    public double getDouble( String key, double defaultValue ) {
        return this.correct ? this.configurationSection.getDouble( key, defaultValue ) : defaultValue;
    }

    public boolean isDouble( String key ) {
        return this.configurationSection.isDouble( key );
    }

    public String getString( String key ) {
        return this.getString( key, "" );
    }

    public String getString( String key, String defaultValue ) {
        return this.correct ? this.configurationSection.getString( key, defaultValue ) : defaultValue;
    }

    public boolean isString( String key ) {
        return this.configurationSection.isString( key );
    }

    public boolean getBoolean( String key ) {
        return this.getBoolean( key, false );
    }

    public boolean getBoolean( String key, boolean defaultValue ) {
        return this.correct ? this.configurationSection.getBoolean( key, defaultValue ) : defaultValue;
    }

    public boolean isBoolean( String key ) {
        return this.configurationSection.isBoolean( key );
    }

    public List getList( String key ) {
        return this.getList( key, null);
    }

    public List getList( String key, List defaultList ) {
        return this.correct ? this.configurationSection.getList( key, defaultList ) : defaultList;
    }

    public boolean isList( String key ) {
        return this.configurationSection.isList( key );
    }

    public List<String> getStringList( String key ) {
        return this.configurationSection.getStringList( key );
    }

    public List<Integer> getIntegerList( String key ) {
        return this.configurationSection.getIntegerList( key );
    }

    public List<Boolean> getBooleanList( String key ) {
        return this.configurationSection.getBooleanList( key );
    }

    public List<Double> getDoubleList( String key ) {
        return this.configurationSection.getDoubleList( key );
    }

    public List<Float> getFloatList( String key ) {
        return this.configurationSection.getFloatList( key );
    }

    public List<Long> getLongList( String key ) {
        return this.configurationSection.getLongList( key );
    }

    public List<Byte> getByteList( String key ) {
        return this.configurationSection.getByteList( key );
    }

    public List<Character> getCharacterList( String key ) {
        return this.configurationSection.getCharacterList( key );
    }

    public List<Short> getShortList( String key ) {
        return this.configurationSection.getShortList( key );
    }

    public List<Map> getMapList( String key ) {
        return this.configurationSection.getMapList( key );
    }

    public void setAll( LinkedHashMap<String, Object> map ) {
        this.configurationSection = new ConfigurationSection( map );
    }

    public boolean exists( String key ) {
        return this.configurationSection.exists( key );
    }

    public boolean exists( String key, boolean ignoreCase ) {
        return this.configurationSection.exists( key, ignoreCase );
    }

    public void remove( String key ) {
        this.configurationSection.remove( key );
    }

    public Map<String, Object> getAll() {
        return this.configurationSection.getAllMap();
    }

    public void setAll( ConfigurationSection section ) {
        this.configurationSection = section;
    }

    public ConfigurationSection getRootSection() {
        return this.configurationSection;
    }

    public int setDefault( LinkedHashMap<String, Object> map ) {
        return this.setDefault( new ConfigurationSection( map ) );
    }

    public int setDefault( ConfigurationSection map ) {
        int size = this.configurationSection.size();
        this.fillDefaults(map, this.configurationSection);
        return this.configurationSection.size() - size;
    }

    private void fillDefaults(ConfigurationSection defaultMap, ConfigurationSection data ) {
        Iterator var3 = defaultMap.keySet().iterator();

        while ( var3.hasNext() ) {
            String key = (String) var3.next();
            if ( !data.containsKey( key ) ) {
                data.put( key, defaultMap.get( key ) );
            }
        }

    }

    private void parseList( String content ) {
        content = content.replace( "\r\n", "\n" );
        String[] var2 = content.split( "\n" );
        int var3 = var2.length;

        for ( int var4 = 0; var4 < var3; ++var4 ) {
            String v = var2[var4];
            if ( !v.trim().isEmpty() ) {
                this.configurationSection.put( v, Boolean.valueOf( true ) );
            }
        }

    }

    private String writeProperties() {
        String content = "#Properties Configuration file\r\n#" + ( new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" ) ).format( new Date() ) + "\r\n";

        Object v;
        Object k;
        for (Iterator var2 = this.configurationSection.entrySet().iterator(); var2.hasNext(); content = content + k + "=" + v + "\r\n" ) {
            Object o = var2.next();
            Map.Entry entry = (Map.Entry) o;
            v = entry.getValue();
            k = entry.getKey();
            if ( v instanceof Boolean ) {
                v = ( (Boolean) v ).booleanValue() ? "on" : "off";
            }
        }

        return content;
    }

    private void parseProperties( String content ) {
        String[] var2 = content.split( "\n" );
        int var3 = var2.length;

        for ( int var4 = 0; var4 < var3; ++var4 ) {
            String line = var2[var4];
            if ( Pattern.compile( "[a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*" ).matcher( line ).matches() ) {
                String[] b = line.split( "=", -1 );
                String k = b[0];
                String v = b[1].trim();
                String v_lower = v.toLowerCase();
                if ( this.configurationSection.containsKey( k ) ) {
                    System.out.println( "[Configuration] Repeated property " + k + " on file " + this.file.toString() );
                }

                byte var11 = -1;
                switch ( v_lower.hashCode() ) {
                    case 3521:
                        if ( v_lower.equals( "no" ) ) {
                            var11 = 5;
                        }
                        break;
                    case 3551:
                        if ( v_lower.equals( "on" ) ) {
                            var11 = 0;
                        }
                        break;
                    case 109935:
                        if ( v_lower.equals( "off" ) ) {
                            var11 = 3;
                        }
                        break;
                    case 119527:
                        if ( v_lower.equals( "yes" ) ) {
                            var11 = 2;
                        }
                        break;
                    case 3569038:
                        if ( v_lower.equals( "true" ) ) {
                            var11 = 1;
                        }
                        break;
                    case 97196323:
                        if ( v_lower.equals( "false" ) ) {
                            var11 = 4;
                        }
                }

                switch ( var11 ) {
                    case 0:
                    case 1:
                    case 2:
                        this.configurationSection.put( k, Boolean.valueOf( true ) );
                        break;
                    case 3:
                    case 4:
                    case 5:
                        this.configurationSection.put( k, Boolean.valueOf( false ) );
                        break;
                    default:
                        this.configurationSection.put( k, v );
                }
            }
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public Object getNested( String key ) {
        return this.get( key );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> T getNested( String key, T defaultValue ) {
        return this.get( key, defaultValue );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public <T> T getNestedAs( String key, Class<T> type ) {
        return (T) this.get( key );
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void removeNested( String key ) {
        this.remove( key );
    }

    private void parseContent( String content ) {
        switch ( this.type ) {
            case 0:
                this.parseProperties( content );
                break;
            case 1:
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                this.configurationSection = new ConfigurationSection(gson.fromJson( content, (new TypeToken<LinkedHashMap<String, Object>>() {
                } ).getType() ));
                break;
            case 2:
                DumperOptions dumperOptions = new DumperOptions();
                dumperOptions.setDefaultFlowStyle( DumperOptions.FlowStyle.BLOCK );
                Yaml yaml = new Yaml( dumperOptions );
                this.configurationSection = new ConfigurationSection(yaml.loadAs( content, LinkedHashMap.class ));
                if ( this.configurationSection == null ) {
                    this.configurationSection = new ConfigurationSection();
                }
                break;
            case 3:
            case 4:
            default:
                this.correct = false;
                break;
            case 5:
                this.parseList( content );
        }

    }

    public Set<String> getKeys() {
        return this.correct ? this.configurationSection.getKeys() : new HashSet();
    }

    public Set<String> getKeys( boolean child ) {
        return this.correct ? this.configurationSection.getKeys( child ) : new HashSet();
    }


}
