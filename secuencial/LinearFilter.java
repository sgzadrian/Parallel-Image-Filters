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
            0.0, 1.0, 2.0, 1.0, 0.0,
            1.0, 3.0, 5.0, 3.0, 1.0,
            2.0, 5.0, 9.0, 5.0, 2.0,
            1.0, 3.0, 5.0, 3.0, 1.0,
            0.0, 1.0, 2.0, 1.0, 0.0
        };
        maskSize = 5;
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
        System.out.println( maskValue );
        for( int i = 0; i < size; i++ ) {
            mask[ i ] = maskValue;
        }
        System.out.println( Arrays.toString( mask ) );
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
