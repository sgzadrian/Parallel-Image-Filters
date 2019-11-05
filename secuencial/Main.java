
public class Main {

    public static void main( String[] args ) {
        // String image = "base.jpg";
        String image = "FHD.jpg";

        GrayScale g = new GrayScale( image );
        g.run();

        Sepia s = new Sepia( image );
        s.run();

        Negative n = new Negative( image );
        n.run();

        LinearFilter lf = new LinearFilter( image );
        lf.box( 5 );

        lf = new LinearFilter( image );
        lf.gauss();

        lf = new LinearFilter( image );
        lf.diff();
    }
}
