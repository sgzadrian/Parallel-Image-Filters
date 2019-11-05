import java.awt.image.BufferedImage;

public class Negative extends Filter {

    Negative( String filename ) {
        super( filename );
    }

    Negative( BufferedImage base, ImagePane panel ) {
        super( base, panel );
        run();
    }


    public void run() {
        filterName = "Negative";
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int[] p = getRGB( i, j );
                p[0] = 255 - p[0];
                p[1] = 255 - p[1];
                p[2] = 255 - p[2];
                setRGB( i, j, p );
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = ( endTime - startTime );
        write( duration );
    }

}
