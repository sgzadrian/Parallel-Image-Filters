import java.awt.image.BufferedImage;

public class LinearFilter extends Filter implements Runnable {

    public static int BOX   = 0;
    public static int GAUSS = 1;
    public static int DIFF  = 2;

    int filter = 0;

    LinearFilter( String filename ) {
        super( filename );
    }

    LinearFilter( BufferedImage image, int section, Filter original, int filter ) {
        super( image, section, original );
        this.filter = filter;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        switch ( filter ) {
            case 0:
                box( 3 );
                break;
            case 1:
                gauss();
                break;
            case 2:
                diff();
                break;
        }
        original.setFilterName( filterName );
        if ( sectionId != -1 ) {
            filterName += "_" + sectionId;
        }
        applyMask();
        long endTime = System.currentTimeMillis();
        time = ( endTime - startTime );
        // write( time );
        original.mergeImages( original, image, sectionId, time );
    }

    public void gauss() {
        filterName = "GaussBlur";
        double[] mask = {
            0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 1, 3, 4, 3, 1, 0,
            0, 1, 4, 9, 4, 1, 0,
            0, 1, 3, 4, 3, 1, 0,
            0, 0, 1, 1, 1, 0, 0,
            0, 0, 0, 0, 0, 0, 0
        };
        maskSize = 7;
        int size = mask.length;
        for( int i = 0; i < size; i++ ) {
            mask[ i ] /= size;
        }
        this.mask = mask;
    }

    public void box( int maskSize ) {
        filterName = "Box";
        this.maskSize = maskSize;
        int size = maskSize * maskSize;
        double[] mask = new double[ size ];
        double maskValue = 1.0 / size;
        for( int i = 0; i < size; i++ ) {
            mask[ i ] = maskValue;
        }
        this.mask = mask;
    }

    public void diff() {
        filterName = "Diff";
        double[] mask = {
            0, 0, -1, 0, 0,
            0, -1, -2, -1, 0,
            -1, -2, 16, -2, -1,
            0, -1, -2, -1, 0,
            0, 0, -1, 0, 0
        };
        maskSize = 5;
        this.mask = mask;
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
    }

    public static void splitAndRun( BufferedImage baseImage, int filter, ImagePane panel ) {
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
                    Thread th = new Thread( new LinearFilter( res[ i ], i, f, filter ) );
                    th.start();
                }
            }
        });
        base.start();
    }
}
