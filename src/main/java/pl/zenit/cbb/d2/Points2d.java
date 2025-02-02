package pl.zenit.cbb.d2;

public strictfp class Points2d {

    public static Point2d getTravel(final Point2d from, final Point2d to) {
        return new Point2d(to.getX() - from.getX(), to.getY() - from.getY());
    }

    public static Point2d moveBy(final Point2d sourcePosition, final Point2d shift) {
        return new Point2d(sourcePosition.getX() + shift.getX(), sourcePosition.getY() + shift.getY());
    }

    public static Point2d abs(final Point2d p) {
        return new Point2d(Math.abs(p.getX()), Math.abs(p.getY()));
    }

    public static Point2d mirrorHorizontally(final Point2d p) {
        return new Point2d(-p.getX(), p.getY());
    }

    public static Point2d mirrorVertically(final Point2d p) {
        return new Point2d(p.getX(), -p.getY());
    }

    public static Point2d mirrorDiagonally(final Point2d p) {
        return new Point2d(-p.getX(), -p.getY());
    }

    public static double euclideanDistanceBetween(final Point2d p1, final Point2d p2) {
        if ( p1 == null || p2 == null ) {
            return 0;
        }
        Point2d travel = getTravel(p1, p2);
        travel = travel
              .withX(travel.getX() * travel.getX())
              .withY(travel.getY() * travel.getY());
        return (float) Math.sqrt(travel.getX() + travel.getY());
    }
    
    public static int chebyshevDistanceBetween(final Point2d p1, final Point2d p2) {
        if ( p1 == null || p2 == null ) {
            return 0;
        }
        Point2d travel = getTravel(p1, p2);
        return Math.max(travel.getX(), travel.getY());
    }
    
    public static int taxicabDistanceBetween(final Point2d p1, final Point2d p2) {
        if ( p1 == null || p2 == null ) {
            return 0;
        }
        Point2d travel = getTravel(p1, p2);
        return Math.abs(travel.getX()) + Math.abs(travel.getY());
    }
    
    public static boolean areEqual(Point2d... points) {
        if ( points == null ) {
            return false;
        }
        if ( points.length == 0 ) {
            return true;
        }
        Point2d src = points[0];
        for ( int i = 1; i < points.length; ++i ) {
            if ( src.getX() != points[i].getX() ) {
                return false;
            }
            if ( src.getY() != points[i].getY() ) {
                return false;
            }
        }
        return true;
    }

}
