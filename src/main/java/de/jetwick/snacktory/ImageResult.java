package de.jetwick.snacktory;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class which encapsulates the data from an image found under an element
 *
 * @author Chris Alexander, chris@chris-alexander.co.uk
 */
public class ImageResult {

    private static final Logger logger = LoggerFactory.getLogger(ArticleTextExtractor.class);

    public String src;
    public Integer weight;
    public String title;
    public int height;
    public int width;
    public int naturalHeight;
    public int naturalWidth;
    public float aspectRatio;
    public String formatName;
    public String alt;
    public boolean noFollow;
    public Element element;

    //analyzeImage attempts to download image and retrieves width, height, aspect, format data
    //from the image.
    private void analyzeImage(String location) {
        URLConnection con = null;
        InputStream stream = null;
        try {
            con = new URL(location).openConnection();
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            stream = con.getInputStream();
            Object iis = ImageIO.createImageInputStream(stream);
            ImageReader reader = (ImageReader) ImageIO.getImageReaders(iis).next();

            this.formatName = reader.getFormatName();
            reader.setInput(iis);
            this.naturalHeight = reader.getHeight(0);
            this.naturalWidth = reader.getWidth(0);
            this.aspectRatio = reader.getAspectRatio(0);
        } catch (Exception e) {
            logger.info("Unable to get image @ " + location + ". Exception " + e.getMessage());
        } finally {
            if (stream != null)
                try { stream.close(); }
                catch (Exception e2) {
                    logger.info("Unable to close open stream for image reading of " + location
                            + " after previous exception\n  " + e2.getMessage());
                }
        }
    }

    public ImageResult(String src, Integer weight, String title, int height, int width,
                       String alt, boolean noFollow) {
        this.src = src;
        this.weight = weight;
        this.title = title;
        this.height = height;
        this.width = width;
        this.alt = alt;
        this.noFollow = noFollow;

        //VZ: A bit of an optimization, we only analyze images that are potential candidates
        if ( (weight == 0 || weight > 100) &&
             (height == 0 || height > 100) && !noFollow  )
            analyzeImage(src);
    }
}
