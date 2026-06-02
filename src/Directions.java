public enum Directions {
    NORTH(O, -1),
    SOUTH(0, 1),
    EAST(-1, 0),
    WEST(1, 0);

    public final dx;
    public final dy;

    Directions(int dx, int dy){
        this.dx = dx;
        this.dy = dy;
    }

}