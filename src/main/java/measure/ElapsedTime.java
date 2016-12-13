package measure;

/**
 * Created by vkrissz on 2016.12.13..
 */
public class ElapsedTime {
    public static final String JSON_PARSE_PART = "JSON_PARSE_PART";
    public static final String JSON_PARSE_WORLD = "JSON_PARSE_WORLD";
    public static final String RENDER = "RENDER";
    public static final String SEND_IMAGE_PART = "SEND_IMAGE_PART";
    public static final String FULL_IMAGE_RENDER = "FULL_IMAGE_RENDER";
    public static final String IMAGE_PART_RESPONSE = "IMAGE_PART_RESPONSE";
    public static final String THUMBNAIL = "THUMBNAIL";
    public static final String COMPRESS = "COMPRESS";
    public static final String COMPRESS_THUMB = "COMPRESS_THUMB";
    public static final String COMPRESS_STREAM = "COMPRESS_STREAM";
    String type;
    private long startTime = System.currentTimeMillis();
    private long endTime = 0;

    public ElapsedTime(String type) {
        this.type = type;
    }

    public void end() {
        endTime = System.currentTimeMillis();
        TimeLogger.saveTime(this);
    }

    @Override
    public String toString() {
        return type + ';' + startTime + ';' + endTime + ';';
    }
}
