public class Sepia extends Filter {

    Sepia( String filename ) {
        super( filename );
    }

    public void run() {
        filterName = "Sepia";
        long startTime = System.currentTimeMillis();
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                int[] p = getRGB( i, j );
                int r = p[0];
                int g = p[1];
                int b = p[2];
                p[0] = (int)( 0.393 * r + 0.769 * g + 0.189 * b );
                p[1] = (int)( 0.349 * r + 0.686 * g + 0.168 * b );
                p[2] = (int)( 0.272 * r + 0.534 * g + 0.131 * b );
                p[0] = p[0] > 255 ? 255 : p[0];
                p[1] = p[1] > 255 ? 255 : p[1];
                p[2] = p[2] > 255 ? 255 : p[2];
                setRGB( i, j, p );
            }
        }
        long endTime = System.currentTimeMillis();
        long duration = ( endTime - startTime );
        write( duration );
    }

}
