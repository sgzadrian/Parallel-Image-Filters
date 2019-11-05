import java.util.Arrays;

public class LinearFilter extends Filter {

    LinearFilter( String filename ) {
        super( filename );
    }

    public void run() {
        long startTime = System.currentTimeMillis();
        applyMask();
        long endTime = System.currentTimeMillis();
        long duration = ( endTime - startTime );
        write( duration );
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
        int size = maskSize * maskSize;
        for( int i = 0; i < size; i++ ) {
            mask[ i ] /= size;
        }
        this.mask = mask;
        run();
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
        run();
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
        run();
    }

}
