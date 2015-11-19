GTitle : CompositeView {
    var g_comp, g_title;
    var labelFont, bRect;

    *new {
        arg parent, bounds = Rect(0, 0, 400, 70);
        ^super.new(parent, bounds).init
    }

    init {
        bRect = super.bounds;
        labelFont = Font("Helvetica", 30);
    }

    composer {
        arg comp;
        g_comp = StaticText.new(super, Rect(0, 0, bRect.width, bRect.height/2)).string_(comp).font_(labelFont);
    }

    piece {
        arg t;
        g_title = StaticText.new(super, Rect(0, bRect.height/2, bRect.width, bRect.height/2)).string_(t).font_(labelFont).stringColor_(Color.white);
    }
}