+ String {
   toSeconds {
        var p = this.split($:).asInteger;
        var ex = Array.geom(3, 1, 60).reverse.keep(-1 * p.size);
        ^[p, ex].flopWith(_*_).sum
    }
}
