import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Filter {

    int width = 0, height = 0;
    BufferedImage image = null;

    double[] mask;
    int maskSize = 0;

    String filterName = "test";

    //final String IN_DIR = "../../assets/";
    final String IN_DIR = "../assets/";
    //final String OUT_DIR = "../output/";
    final String OUT_DIR = "output/";

    Filter( String filename ) {
        read( filename );
    }

    public void read( String filename ) {
        try {
            File tmpFile = new File( IN_DIR + filename );
            // image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            image = ImageIO.read( tmpFile );
            width = image.getWidth();
            height = image.getHeight();
            // System.out.println( "Size :" + width + "x" + height );
        } catch( IOException e ) {
            System.out.println( "Error: "+e );
        }
    }

    public void write( long time ) {
        try {
            File outFile = new File( OUT_DIR + filterName + ".jpg" );
            ImageIO.write( image, "jpg", outFile );
            System.out.println( filterName + " completed in: " + time + "ms" );
        } catch( IOException e ) {
            System.out.println("Error: "+e);
        }
    }

    public int[] getRGB( int x, int y ) {
        return getRGB( this.image, x, y );
    }

    public int[] getRGB( BufferedImage image, int x, int y ) {
        int p = image.getRGB( x, y );
        int[] rgb = {
            ( p >> 16 ) & 0xff,
            ( p >> 8 ) & 0xff,
            p & 0xff
        };
        return rgb;
    }

    public void setRGB( int x, int y, int[] values ) {
        setRGB( this.image, x, y, values );
    }

    public void setRGB( BufferedImage image, int x, int y, int[] values ) {
        int p = ( values[0] << 16 ) | ( values[1] << 8 ) | values[2];
        image.setRGB( x, y, p );
    }

    public void applyMask() {
        int auxSize = maskSize / 2;
        BufferedImage tmpImg = new BufferedImage( width + auxSize * 2, height + auxSize * 2, BufferedImage.TYPE_INT_RGB );
        tmpImg.setRGB( auxSize, auxSize, width, height, image.getRGB( 0, 0, width, height, null, 0, width ), 0, width );
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                setRGB( i, j, convolution( getSubImg( tmpImg, i, j, maskSize ) ) );
            }
        }
    }

    private int[] convolution( int[] pixels ) {
        int[] res = { 0, 0, 0 };
        for( int i = 0; i < pixels.length; i++ ) {
            int r = ( pixels[ i ] >> 16 ) & 0xff;
            int g = ( pixels[ i ] >> 8 ) & 0xff;
            int b = pixels[ i ] & 0xff;
            res[ 0 ] += mask[ i ] * r;
            res[ 1 ] += mask[ i ] * g;
            res[ 2 ] += mask[ i ] * b;
        }
        for( int i = 0; i < res.length; i++ ) {
            res[ i ] = res[ i ] > 255 ? 255 : res[ i ];
        }
        return res;
    }

    public int[] getSubImg( BufferedImage image, int x, int y, int size ) {
        return image.getRGB( x, y, size, size, null, 0, size );
    }

}
