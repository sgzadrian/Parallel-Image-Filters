import java.awt.image.BufferedImage;

public class GrayScale extends Filter implements Runnable {

    GrayScale( String filename ) {
        super( filename );
    }

    GrayScale( BufferedImage image, int section, Filter original ) {
        super( image, section, original );
    }

    GrayScale( BufferedImage image ) {
        super( image );
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
        original.mergeImages( original, image, sectionId, time );
    }

    // public static void splitAndRun( BufferedImage baseImage, ImagePane panel ) {
        // Thread base = new Thread(new Runnable(){
            // @Override
            // public void run() {
                // int w = baseImage.getWidth();
                // int h = baseImage.getHeight();
                // BufferedImage image = new BufferedImage( w, h, 1 );
                // image.setRGB( 0, 0, w, h, baseImage.getRGB( 0, 0, w, h, null, 0, w ), 0, w );
                // Filter f = new Filter( image, panel );
                // BufferedImage[] res = f.splitImage( f.getImage() );
                // for( int i = 0; i < res.length; i++ ) {
                    // Thread th = new Thread( new GrayScale( res[ i ], i, f ) );
                    // th.start();
                // }
            // }
        // });
        // base.start();
    // }

    public static void splitAndRun( BufferedImage baseImage, FiltersInterface server ) {
        Thread base = new Thread(new Runnable(){
            @Override
            public void run() {
                Filter original = new Filter( baseImage, server );
                BufferedImage[] res = original.splitImage( baseImage );
                for( int i = 0; i < res.length; i++ ) {
                    Thread th = new Thread( new GrayScale( res[ i ], i, original ) );
                    th.start();
                }
            }
        });
        base.start();
    }

}
