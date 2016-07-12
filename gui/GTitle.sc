GTitle : CompositeView {
    var g_comp, g_title;
    var labelFont;

    *new {
        arg parent, bounds = Rect(0, 0, 400, 70), composer, piece;
        ^super.new(parent, bounds).init(composer, piece);
    }

    init {
        arg c, p;
        var tRect, pRect;

        labelFont = Font("Helvetica", 30);
        tRect = Rect(0, 0, 400, 35);
        pRect = Rect(0, 35, 400, 35);


        g_comp = StaticText(super, tRect).string_(c).font_(labelFont);
        g_title = StaticText(this, pRect).string_(p).font_(labelFont).stringColor_(Color.white);
    }
}