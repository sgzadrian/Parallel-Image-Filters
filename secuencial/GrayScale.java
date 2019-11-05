import java.awt.image.BufferedImage;

public class GrayScale extends Filter {

    GrayScale( String filename ) {
        super( filename );
    }

    GrayScale( BufferedImage base, ImagePane panel ) {
        super( base, panel );
        run();
    }

    public void run() {
        filterName = "GrayScale";
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int[] p = getRGB( i, j );
                int avg = ( p[0] + p[1] + p[2] ) / 3;
                p[0] = avg;
                p[1] = avg;
                p[2] = avg;
                setRGB( i, j, p );
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = ( endTime - startTime );
        write( duration );
    }

}
