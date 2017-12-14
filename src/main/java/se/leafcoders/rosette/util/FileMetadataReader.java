package se.leafcoders.rosette.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.tomcat.util.http.fileupload.IOUtils;

public class FileMetadataReader {

    public static Long[] readImageSize(byte[] fileData) {
        InputStream inputStream = new ByteArrayInputStream(fileData);
        try {
            BufferedImage image = ImageIO.read(inputStream);
            if (image != null) {
                return new Long[] { new Long(image.getWidth()), new Long(image.getHeight()) }; 
            }
        } catch (Exception ignore) {
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public static Long readAudioDuration(byte[] fileData, String mimeType) {
        InputStream inputStream = new ByteArrayInputStream(fileData);
        try {
            long duration = -1;
            if (mimeType.equals("audio/mpeg") || mimeType.equals("audio/mp3")) {
                AudioFileFormat baseFileFormat = new RosetteMpegAudioFileReader().getAudioFileFormat(inputStream, inputStream.available());
                duration = ((long) baseFileFormat.properties().get("duration")) / 1000000;
            } else {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                IOUtils.closeQuietly(audioInputStream);
                duration = Math.round(0.5 + frames / format.getFrameRate());
            }
            if (duration > 0) {
                return duration;
            }
        } catch (Exception ignore) {
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

}
