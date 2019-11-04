import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Filter {

    int width = 0, height = 0;
    BufferedImage image;

    double[] mask;
    int maskSize = 0;

    String filterName = "test";

    final String IN_DIR = "../../assets/";
    final String OUT_DIR = "../output/";

    Filter original = null;
    int[][] sections;
    int sectionId = -1;
    long time = 0;

    Filter( String filename ) {
        read( filename );
    }

    Filter( BufferedImage image, int section, Filter original ) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        sectionId = section;
        this.original = original;
    }

    public void read( String filename ) {
        try {
            File tmpFile = new File( IN_DIR + filename );
            // image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            image = ImageIO.read( tmpFile );
            width = image.getWidth();
            height = image.getHeight();
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
            System.out.println( "Error: "+e );
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

    public BufferedImage getImage() {
        return image;
    }

    public void setFilterName( String filterName ) {
        this.filterName = filterName;
    }

    public void applyMask() {
        int auxSize = maskSize / 2;
        BufferedImage tmpImg = new BufferedImage( width + auxSize * 2, height + auxSize * 2, BufferedImage.TYPE_INT_RGB );
        tmpImg.setRGB( auxSize, auxSize, width, height, image.getRGB( 0, 0, width, height, null, 0, width ), 0, width );
        // Left
        int[] border = image.getRGB( 0, 0, 1, height, null, 0, 1 );
        for( int i = 0; i < auxSize; i++ ) {
            tmpImg.setRGB( i, auxSize, 1, height, border, 0, 1 );
        }
        // Right
        border = image.getRGB( width - 1, 0, 1, height, null, 0, 1 );
        for( int i = 0; i < auxSize; i++ ) {
            tmpImg.setRGB( width + 1 + i, auxSize, 1, height, border, 0, 1 );
        }
        // Top
        int tmpWidth = tmpImg.getWidth();
        border = tmpImg.getRGB( 0, auxSize, tmpWidth, 1, null, 0, tmpWidth );
        for( int i = 0; i < auxSize; i++ ) {
            tmpImg.setRGB( 0, i, tmpWidth, 1, border, 0, tmpWidth );
        }
        // Bottom
        border = tmpImg.getRGB( 0, height + auxSize - 1, tmpWidth, 1, null, 0, tmpWidth );
        for( int i = 0; i < auxSize; i++ ) {
            tmpImg.setRGB( 0, height + auxSize + i, tmpWidth, 1, border, 0, tmpWidth );
        }
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

    public BufferedImage[] splitImage( BufferedImage img ) {
        BufferedImage[] results;
        int w = img.getWidth();
        int h = img.getHeight();
        if ( w <= 512 || h <= 512 ) {
            sections = new int[1][2];
            results = new BufferedImage[ 1 ];
            results[0] = img;
        } else {
            int size = w / 512;
            size = size % 2 == 0 ? size : size + 1;
            while( w % size != 0 && h % size != 0 ) {
                size -= 2;
            }
            int threads = size * size;
            sections = new int[ threads ][2];
            results = new BufferedImage[ threads ];
            int subWidth = w / size;
            int subHeight = h / size;
            for( int i = 0, current = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++, current++ ) {
                    int posX = subWidth * i;
                    int posy = subHeight * j;
                    sections[ current ][0] = posX;
                    sections[ current ][1] = posy;
                    results[ current ] = img.getSubimage( posX, posy, subWidth, subHeight );
                }
            }
        }
        return results;
    }

    public void mergeImages( Filter baseImage, BufferedImage tmpImg, int section, long time ) {
        if ( time > this.time ) {
            this.time = time;
        }
        int w = tmpImg.getWidth();
        int h = tmpImg.getHeight();
        image.setRGB( sections[ section ][0], sections[ section ][1], w, h, tmpImg.getRGB( 0, 0, w, h, null, 0, w ), 0, w );
        if ( section == sections.length - 1 ) {
            baseImage.write( time );
        }
    }
}
