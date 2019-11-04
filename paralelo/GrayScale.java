import java.awt.image.BufferedImage;

public class GrayScale extends Filter implements Runnable {

    GrayScale( String filename ) {
        super( filename );
    }

    GrayScale( BufferedImage image, int section, Filter original ) {
        super( image, section, original );
    }

    @Override
    public void run() {
        filterName = "GrayScale";
        original.setFilterName( filterName );
        if ( sectionId != -1 ) {
            filterName += "_" + sectionId;
        }
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
        time = ( endTime - startTime );
        // write( time );
        original.mergeImages( original, image, sectionId, time );
    }

    public static void splitAndRun( String image ) {
        Thread base = new Thread(new Runnable(){
            @Override
            public void run() {
                Filter f = new Filter( image );
                BufferedImage[] res = f.splitImage( f.getImage() );
                for( int i = 0; i < res.length; i++ ) {
                    Thread th = new Thread( new GrayScale( res[ i ], i, f ) );
                    th.start();
                }
            }
        });
        base.start();
    }

}
