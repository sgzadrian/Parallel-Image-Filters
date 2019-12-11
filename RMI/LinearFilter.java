import java.awt.image.BufferedImage;

public class LinearFilter extends Filter implements Runnable {

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
            case RemoteFilter.BOX:
                box( 3 );
                break;
            case RemoteFilter.GAUSS:
                gauss();
                break;
            case RemoteFilter.DIFF:
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
        original.mergeImages( original, image, sectionId, filter, time );
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

    public static void splitAndRun( BufferedImage baseImage, int filter, FiltersInterface server ) {
        Thread base = new Thread(new Runnable(){
            @Override
            public void run() {
                Filter original = new Filter( baseImage, server );
                BufferedImage[] res = original.splitImage( baseImage );
                for( int i = 0; i < res.length; i++ ) {
                    Thread th = new Thread( new LinearFilter( res[ i ], i, original, filter ) );
                    th.start();
                }
            }
        });
        base.start();
    }


}
