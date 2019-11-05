import java.awt.image.BufferedImage;

public class Negative extends Filter implements Runnable {

    Negative( String filename ) {
        super( filename );
    }

    Negative( BufferedImage image, int section, Filter original ) {
        super( image, section, original );
    }

    @Override
    public void run() {
        filterName = "Negative";
        original.setFilterName( filterName );
        if ( sectionId != -1 ) {
            filterName += "_" + sectionId;
        }
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
        time = ( endTime - startTime );
        // write( duration );
        original.mergeImages( original, image, sectionId, time );
    }

    public static void splitAndRun( BufferedImage baseImage, ImagePane panel ) {
        Thread base = new Thread(new Runnable(){
            @Override
            public void run() {
                int w = baseImage.getWidth();
                int h = baseImage.getHeight();
                BufferedImage image = new BufferedImage( w, h, 1 );
                image.setRGB( 0, 0, w, h, baseImage.getRGB( 0, 0, w, h, null, 0, w ), 0, w );
                Filter f = new Filter( image, panel );
                BufferedImage[] res = f.splitImage( f.getImage() );
                for( int i = 0; i < res.length; i++ ) {
                    Thread th = new Thread( new Negative( res[ i ], i, f ) );
                    th.start();
                }
            }
        });
        base.start();
    }
}
